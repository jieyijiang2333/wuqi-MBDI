package com.wqmtmbdi.mbcc.engine

import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.CHR_STATS
import com.wqmtmbdi.mbcc.data.FACTIONS
import com.wqmtmbdi.mbcc.data.FACTIONS_INFO
import com.wqmtmbdi.mbcc.data.QUESTIONS
import com.wqmtmbdi.mbcc.data.TIER_LIMITS
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/** 单角色匹配结果（对齐 HTML dcpmScores 返回对象：id/S/prob/total/sim） */
data class RankResult(val id: String, val S: Double, val prob: Double, val total: Double, val sim: Double)

/** mulberry32 伪随机数生成器（与 Node 金标同算法同种子，用于校准一致性验证） */
class Mulberry32(private var seed: Int) {
    fun next(): Double {
        seed += 0x6D2B79F5
        var t = seed
        t = (t xor (t ushr 15)) * (t or 1)
        t = t xor (t + (t xor (t ushr 7)) * (t or 61))
        return (t xor (t ushr 14)).toLong().and(0xFFFFFFFFL).toDouble() / 4294967296.0
    }
}

/**
 * DCPM 分布校准概率匹配引擎（v1.3.0，对齐 HTML L1135-1349）。
 * 四阶段：①熵权法维度加权 → ②混合度量(加权余弦−μ·加权欧氏) → ③温度软最大 → ④先验迭代校准。
 * 纪律：全链路 Double；禁止任何"看起来更优雅"的改写，先逐行直译。
 */
object DcpmEngine {
    const val MU = 0.4
    const val LAMBDA = 0.2
    const val GAMMA = 0.7
    /**
     * 五档温度：题量越少越「均匀」（容错高），题量越多越「集中」（结果更果断）。
     * 12→0.07 / 20→0.08 / 30→0.09 / 40→0.095 / 50→0.10。
     */
    val TAU = mapOf(1 to 0.07, 2 to 0.08, 3 to 0.09, 4 to 0.095, 5 to 0.10)
    /** 校准签名：题库 / 档位数 / 换算方式一变就要改（98 角色 × 5 档先验 = lvl3） */
    const val CALIB_SIG = "98x50-lvl3"
    const val ITER = 15
    const val N = 2500
    /** ★ v1.5.0：角色池从 20 扩到 98，均匀先验目标随角色数动态走（= 1/n） */
    val TARGET = 1.0 / CHARACTERS.size
    const val D = 7

    private val dimMin = IntArray(D)
    private val dimMax = IntArray(D)
    private val entW = DoubleArray(D)                    // DCPM_ENT_W
    private val norm = HashMap<String, DoubleArray>()    // DCPM_NORM 角色 z 向量
    private val cosa = HashMap<String, Double>()         // DCPM_COSA
    private var dwb = 0.0                                // DCPM_DWB
    private val cidx = HashMap<String, Int>()            // DCPM_CIDX
    private val tmax = HashMap<Int, DoubleArray>()       // DCPM_TMAX
    private val mean = HashMap<Int, DoubleArray>()       // DCPM_MEAN
    private val std = HashMap<Int, DoubleArray>()        // DCPM_STD
    private val factionCentroid = HashMap<String, DoubleArray>()
    private var prior = HashMap<Int, DoubleArray>()      // DCPM_PRIOR {2,3}
    private var inited = false

    /** 向量归一化（对齐 HTML normVec：模长 0 时兜底 1） */
    fun normVec(v: DoubleArray): DoubleArray {
        var m = sqrt(v.sumOf { it * it })
        if (m == 0.0) m = 1.0
        return DoubleArray(v.size) { v[it] / m }
    }

    /** 向量点积（对齐 HTML vecDot） */
    fun vecDot(a: DoubleArray, b: DoubleArray): Double {
        var s = 0.0
        for (i in a.indices) s += a[i] * b[i]
        return s
    }

    /** 阶段1+归一化：熵权权重 + 每档基准 + 角色 z 向量 + 阵营质心（对齐 HTML IIFE L1147-1195 与 L1319-1329） */
    fun init() {
        if (inited) return
        inited = true
        val C = CHARACTERS
        val n = C.size
        // 1) 熵权权重（基于官方七维 min-max 归一化）
        for (j in 0 until D) { dimMin[j] = Int.MAX_VALUE; dimMax[j] = Int.MIN_VALUE }
        for (i in 0 until n) {
            val s = C[i].stats
            for (j in 0 until D) {
                if (s[j] < dimMin[j]) dimMin[j] = s[j]
                if (s[j] > dimMax[j]) dimMax[j] = s[j]
            }
        }
        val colNorm = Array(D) { DoubleArray(n) }
        for (j in 0 until D) {
            val rng = if (dimMax[j] - dimMin[j] != 0) (dimMax[j] - dimMin[j]).toDouble() else 1.0
            for (i in 0 until n) colNorm[j][i] = (C[i].stats[j] - dimMin[j]) / rng
        }
        for (j in 0 until D) {
            var sum = 0.0
            for (i in 0 until n) sum += colNorm[j][i]
            var e = 1.0
            if (sum > 0) {
                e = 0.0
                for (i in 0 until n) {
                    val p = colNorm[j][i] / sum
                    if (p > 0) e -= p * ln(p)
                }
                e /= ln(n.toDouble())
            }
            entW[j] = 1 - e    // 熵权：区分度越高的维度权重越高
        }
        var sw = 0.0
        for (j in 0 until D) sw += entW[j]
        for (j in 0 until D) entW[j] = (1 - LAMBDA) * (entW[j] / (if (sw != 0.0) sw else D.toDouble())) + LAMBDA / D
        for (i in 0 until n) cidx[C[i].id] = i
        // 2) 每档理论最大值 + 均匀作答人群基准 μ/σ（解析计算，无需模拟）
        val lim = mapOf(
            1 to minOf(12, QUESTIONS.size),
            2 to minOf(20, QUESTIONS.size),
            3 to minOf(30, QUESTIONS.size),
            4 to minOf(40, QUESTIONS.size),
            5 to QUESTIONS.size
        )
        for (tt in 1..5) {
            val qs = QUESTIONS.take(lim[tt]!!)
            val tmaxA = DoubleArray(D)
            val m = DoubleArray(D)
            val v = DoubleArray(D)
            qs.forEach { q ->
                val qmax = DoubleArray(D)
                q.options.forEach { o ->
                    for (d in 0 until D) {
                        val v = o.s[d].toDouble()
                        m[d] += v / 4
                        if (v > qmax[d]) qmax[d] = v
                    }
                }
                for (d in 0 until D) tmaxA[d] += qmax[d]
            }
            qs.forEach { q ->
                for (d in 0 until D) {
                    var sq = 0.0
                    var md = 0.0
                    q.options.forEach { o ->
                        val v = o.s[d].toDouble()
                        sq += v * v
                        md += v / 4
                    }
                    v[d] += sq / 4 - md * md
                }
            }
            // ★ 2026-09-28 题库改为「七维水平测量制」后的基准换算：
            //   玩家某维得分 = 该维在所选选项上的**平均值**（0–10），
            //   不再是"累加值 ÷ 每题最大值的和"。
            //   于是：基准均值 = 全部选项该维的平均值（题库约束"每题每维 4 选项和=20" → 恒为 5）；
            //        基准标准差 = sqrt(Σ每题方差) / 题数（玩家分是 N 题取平均，方差要除以 N²）。
            val nn = qs.size.toDouble()
            for (d in 0 until D) {
                if (tmaxA[d] == 0.0) tmaxA[d] = 1.0
                m[d] = m[d] / nn
                v[d] = maxOf(0.0, v[d]) / (nn * nn)   // 方差缩放（下面统一 sqrt 成标准差）
            }
            tmax[tt] = tmaxA
            mean[tt] = m
            std[tt] = v.map { sqrt(it) }.toDoubleArray()
        }
        // 3) 角色 z 向量（以 50 题人群基准为参照系）与加权度量常量
        val mu = mean[3]!!
        val sg = std[3]!!
        for (i in 0 until n) {
            val c = C[i]
            val v = DoubleArray(D)
            for (j in 0 until D) v[j] = (c.stats[j] - mu[j]) / (if (sg[j] > 1e-9) sg[j] else 1.0)
            var acc = 0.0
            for (j in 0 until D) acc += entW[j] * v[j] * v[j]
            norm[c.id] = v
            cosa[c.id] = sqrt(acc)
        }
        var dw = 0.0
        for (j in 0 until D) dw += entW[j]
        dwb = sqrt(dw)
        // 4) 阵营质心（成员七维平均，归一化；对齐 HTML FACTION_CENTROID）
        FACTIONS.forEach { (fid, cids) ->
            val c = DoubleArray(D)
            cids.forEach { cid ->
                val s = CHR_STATS[cid]!!
                for (i in 0 until D) c[i] += s[i]
            }
            val nn = cids.size
            for (i in 0 until D) c[i] /= nn
            factionCentroid[fid] = normVec(c)
        }
        // 5) 先验：缓存命中直接使用，否则均匀先验（校准前也可正常匹配）
        val cached = AppStorage.loadCalib(CALIB_SIG)
        prior = HashMap()
        if (cached != null && cached.size == 5) {
            for (t in 1..5) prior[t] = cached[t]!!
        } else {
            for (t in 1..5) prior[t] = DoubleArray(n) { 1.0 }
        }
    }

    /**
     * 阶段2+3：玩家画像(0-10浮点) → 按概率排序的角色列表（对齐 HTML dcpmScores L1199-1230）。
     *
     * ★ v1.5.2「收集补全」：娱乐档传入 [owned]（本地已解锁角色 id）后做二次加权 ——
     *   - **已拥有**角色一律 × [ownedPenalty]（降权，不管相似度高低）；
     *   - **大致范围**内的**未解锁**角色 × [freshBonus]（范围内 = 匹配分 S 落在 topS − [range]，最多 20 个）；
     *   - 之后重新归一化。只改「谁排第一」，不改 S（匹配度形状）。
     *   - 严格档 / 收集已满（owned 为空）→ 传 null，行为与旧版完全一致。
     */
    fun scores(
        playerStats: List<Double>,
        tier: Int,
        priorOverride: DoubleArray? = null,
        owned: Set<String>? = null,
        ownedPenalty: Double = 0.55,
        freshBonus: Double = 1.10,
        range: Double = 0.18
    ): List<RankResult> {
        val C = CHARACTERS
        val n = C.size
        val mu = mean[tier]!!
        val sg = std[tier]!!
        val q = DoubleArray(D)
        for (j in 0 until D) q[j] = (playerStats[j] - mu[j]) / (if (sg[j] > 1e-9) sg[j] else 1.0)  // 玩家画像 z 变换（相对人群基准）
        var qq = 0.0
        for (j in 0 until D) qq += entW[j] * q[j] * q[j]
        val qNorm = sqrt(qq)
        val ids = ArrayList<String>(n)
        val Svals = DoubleArray(n)
        for (i in 0 until n) {
            val c = C[i]
            val v = norm[c.id]!!
            var num = 0.0
            for (j in 0 until D) num += entW[j] * q[j] * v[j]
            val cos = if (qNorm > 0 && cosa[c.id]!! > 0) num / (qNorm * cosa[c.id]!!) else 0.0   // 加权余弦（形状）
            var d2 = qq + cosa[c.id]!! * cosa[c.id]!! - 2 * num
            if (d2 < 0) d2 = 0.0
            val S = cos - MU * sqrt(d2) / (if (dwb != 0.0) dwb else 1.0)                         // 强度修正
            ids.add(c.id)
            Svals[i] = S
        }
        val tau = TAU[tier] ?: 0.1
        val pri = priorOverride ?: prior[tier]
        var mx = -1e9
        for (i in 0 until n) if (Svals[i] > mx) mx = Svals[i]
        var wsum = 0.0
        val probs = DoubleArray(n)
        for (i in 0 until n) {
            var z = exp((Svals[i] - mx) / tau)
            if (pri != null) z *= pri[cidx[ids[i]]!!]    // 贝叶斯先验：垫底角色也保留基础概率
            probs[i] = z
            wsum += z
        }
        if (wsum <= 0) wsum = 1.0
        for (i in 0 until n) probs[i] /= wsum

        // ★ 收集补全（只在娱乐档传入 owned 时生效）
        //   ① 已拥有的一律降权（不论相似度高低——「已经有的就别老给我了」）
        //   ② 只有「大致范围」内的未解锁角色才抬升（避免把明显不像的角色硬塞给你）
        if (!owned.isNullOrEmpty()) {
            for (i in 0 until n) {
                if (owned.contains(ids[i])) probs[i] *= ownedPenalty
            }
            var topS = -1e9
            for (i in 0 until n) if (Svals[i] > topS) topS = Svals[i]
            val inRange = (0 until n)
                .filter { Svals[it] >= topS - range }
                .sortedByDescending { Svals[it] }
                .take(20)
            for (i in inRange) {
                if (!owned.contains(ids[i])) probs[i] *= freshBonus
            }
            var s2 = 0.0
            for (i in 0 until n) s2 += probs[i]
            if (s2 > 0) for (i in 0 until n) probs[i] /= s2
        }

        val result = (0 until n).map { i -> RankResult(ids[i], Svals[i], probs[i], probs[i], Svals[i]) }
        return result.sortedByDescending { it.prob }
    }

    /** 玩家阵营判定：与10个阵营质心比对（对齐 HTML matchFaction L1336-1344，吃原始累加值） */
    fun matchFaction(playerStats: List<Double>): Pair<String, Double> {
        val pn = normVec(playerStats.toDoubleArray())
        var best = ""
        var bestSim = -Double.MAX_VALUE
        factionCentroid.forEach { (fid, c) ->
            val sim = vecDot(pn, c)
            if (sim > bestSim) {
                bestSim = sim
                best = fid
            }
        }
        return best to bestSim
    }

    /** 兼容旧调用：返回最佳角色ID（对齐 HTML matchCurrentTier） */
    fun matchCurrentTier(pa: List<Double>, tier: Int): String = scores(pa, tier)[0].id

    /** 角色阵营名（局长返回"特殊"，对齐 HTML renderGallery/openChar） */
    fun factionNameOf(id: String): String {
        var fid: String? = null
        for ((k, v) in FACTIONS) if (id in v) { fid = k; break }
        return if (fid == null) "特殊" else FACTIONS_INFO[fid]?.name ?: "特殊"
    }

    /** 阵营名（fid → 中文名；juzhang 走"特殊"） */
    fun factionName(fid: String?): String = if (fid == null) "特殊" else FACTIONS_INFO[fid]?.name ?: "特殊"

    /**
     * 阶段4：先验迭代校准（蒙特卡洛×乘法更新，对齐 HTML dcpmCalibrate L1235-1280）。
     * @param rng 随机源，默认 kotlin.random；测试传入 Mulberry32(seed)::next 复现金标
     */
    fun calibrate(rng: (() -> Double)? = null) {
        val rnd: () -> Double = rng ?: { kotlin.random.Random.nextDouble() }
        val n = CHARACTERS.size
        val nQ = QUESTIONS.size
        if (nQ < 32) return
        val qOpts = QUESTIONS.map { q -> q.options.map { o -> o.s } }
        val pi = HashMap<Int, DoubleArray>()
        for (t in 1..5) pi[t] = DoubleArray(n) { 1.0 }
        val st = DoubleArray(D)
        for (it in 0 until ITER) {
            val acc = HashMap<Int, DoubleArray>()
            for (t in 1..5) acc[t] = DoubleArray(n)
            for (i in 0 until N) {
                for (qidx in 0 until nQ) {
                    val os = qOpts[qidx][(rnd() * 4.0).toInt()]
                    for (d in 0 until D) st[d] += os[d].toDouble()
                    // 逐档采样点（水平测量制：该档题量取平均；50 题档在循环外统一算）
                    when (qidx) {
                        11 -> {   // 12 题
                            val q = DoubleArray(D)
                            for (d in 0 until D) q[d] = st[d] / 12.0
                            scores(q.toList(), 1, pi[1]).forEach { r -> acc[1]!![cidx[r.id]!!] += r.prob }
                        }
                        19 -> {   // 20 题
                            val q = DoubleArray(D)
                            for (d in 0 until D) q[d] = st[d] / 20.0
                            scores(q.toList(), 2, pi[2]).forEach { r -> acc[2]!![cidx[r.id]!!] += r.prob }
                        }
                        29 -> {   // 30 题
                            val q = DoubleArray(D)
                            for (d in 0 until D) q[d] = st[d] / 30.0
                            scores(q.toList(), 3, pi[3]).forEach { r -> acc[3]!![cidx[r.id]!!] += r.prob }
                        }
                        39 -> {   // 40 题
                            val q = DoubleArray(D)
                            for (d in 0 until D) q[d] = st[d] / 40.0
                            scores(q.toList(), 4, pi[4]).forEach { r -> acc[4]!![cidx[r.id]!!] += r.prob }
                        }
                    }
                }
                val q5 = DoubleArray(D)   // 50 题档软概率累加（水平测量制：全部题取平均）
                for (d in 0 until D) q5[d] = st[d] / nQ.toDouble()
                scores(q5.toList(), 5, pi[5]).forEach { r -> acc[5]!![cidx[r.id]!!] += r.prob }
                for (d in 0 until D) st[d] = 0.0
            }
            for (t in 1..5) {
                for (i in 0 until n) {
                    val avg = acc[t]!![i] / N
                    var np = pi[t]!![i] * (TARGET / maxOf(avg, 1e-9)).pow(GAMMA)
                    if (!np.isFinite()) np = pi[t]!![i] * 2
                    pi[t]!![i] = minOf(maxOf(np, 1e-9), 1e6)
                }
                var s = 0.0
                for (i in 0 until n) s += pi[t]!![i]
                for (i in 0 until n) pi[t]!![i] /= s
            }
        }
        for (t in 1..5) {
            for (i in 0 until n) {
                if (pi[t]!![i] < 1e-9) pi[t]!![i] = 1e-9
            }
            // ★ 保底之后必须再归一化一次：被抬到 1e-9 的元素会让总和 > 1
            var s = 0.0
            for (i in 0 until n) s += pi[t]!![i]
            if (s > 0) for (i in 0 until n) pi[t]!![i] /= s
        }
        synchronized(this) {
            for (t in 1..5) prior[t] = pi[t]!!
        }
        AppStorage.saveCalib(CALIB_SIG, (1..5).associateWith { pi[it]!! })
    }

    /** 当前先验（供测试/调试） */
    fun priorOf(tier: Int): DoubleArray = prior[tier] ?: DoubleArray(CHARACTERS.size) { 1.0 }

    /** 熵权权重（供测试） */
    fun entropyWeights(): DoubleArray = entW.copyOf()

    /** 每档理论最大值（供测试） */
    fun tmaxOf(tier: Int): DoubleArray = tmax[tier]?.copyOf() ?: DoubleArray(D)

    /** 每档人群基准均值（供测试） */
    fun meanOf(tier: Int): DoubleArray = mean[tier]?.copyOf() ?: DoubleArray(D)

    /** 每档人群基准标准差（供测试） */
    fun stdOf(tier: Int): DoubleArray = std[tier]?.copyOf() ?: DoubleArray(D)

    /** 角色 z 向量（供测试） */
    fun normOf(id: String): DoubleArray? = norm[id]?.copyOf()

    /** 角色加权模长（供测试） */
    fun cosaOf(id: String): Double = cosa[id] ?: 0.0

    /** 加权基准模长（供测试） */
    fun dwbOf(): Double = dwb
}
