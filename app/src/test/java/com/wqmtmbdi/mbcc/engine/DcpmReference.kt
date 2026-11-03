package com.wqmtmbdi.mbcc.engine

import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.CHR_STATS
import com.wqmtmbdi.mbcc.data.FACTIONS
import com.wqmtmbdi.mbcc.data.QUESTIONS
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * DCPM 引擎参考实现（测试金标，独立直译 HTML L729-1349）。
 * 与 DcpmEngine 结构刻意不同（OOP 可变状态 + 独立命名），降低复制粘贴相关性；
 * 单元测试以它为金标验证 DcpmEngine 的位级一致性。
 */
object DcpmReference {
    private const val DIM = 7
    private const val MU = 0.4
    private const val LAMBDA = 0.2
    private const val GAMMA = 0.7
    private const val ITER = 15
    private const val N = 2500
    /** ★ v1.5.0：与 DcpmEngine 同步 —— 均匀先验目标 = 1/当前角色数（98） */
    private val TARGET = 1.0 / CHARACTERS.size

    /** mulberry32（与 Node/HTML 同算法） */
    class Rand(private var a: Int) {
        fun next(): Double {
            a += 0x6D2B79F5
            var t = a
            t = (t xor (t ushr 15)) * (t or 1)
            t = t xor (t + (t xor (t ushr 7)) * (t or 61))
            return (t xor (t ushr 14)).toLong().and(0xFFFFFFFFL).toDouble() / 4294967296.0
        }
    }

    data class Scored(val id: String, val S: Double, val prob: Double)

    class Params {
        val entW = DoubleArray(DIM)
        val tmax = HashMap<Int, DoubleArray>()
        val mean = HashMap<Int, DoubleArray>()
        val std = HashMap<Int, DoubleArray>()
        val z = HashMap<String, DoubleArray>()
        val cosa = HashMap<String, Double>()
        var dwb = 0.0
        val centroids = HashMap<String, DoubleArray>()
    }

    fun norm(v: DoubleArray): DoubleArray {
        var m = sqrt(v.sumOf { it * it })
        if (m == 0.0) m = 1.0
        return DoubleArray(v.size) { v[it] / m }
    }

    fun dot(a: DoubleArray, b: DoubleArray): Double {
        var s = 0.0
        for (i in a.indices) s += a[i] * b[i]
        return s
    }

    fun buildParams(): Params {
        val p = Params()
        val n = CHARACTERS.size
        val mn = IntArray(DIM) { Int.MAX_VALUE }
        val mx = IntArray(DIM) { Int.MIN_VALUE }
        for (c in CHARACTERS) for (d in 0 until DIM) {
            if (c.stats[d] < mn[d]) mn[d] = c.stats[d]
            if (c.stats[d] > mx[d]) mx[d] = c.stats[d]
        }
        val cols = Array(DIM) { DoubleArray(n) }
        for (d in 0 until DIM) {
            val span = if (mx[d] - mn[d] != 0) (mx[d] - mn[d]).toDouble() else 1.0
            for (i in 0 until n) cols[d][i] = (CHARACTERS[i].stats[d] - mn[d]) / span
        }
        for (d in 0 until DIM) {
            var total = 0.0
            for (i in 0 until n) total += cols[d][i]
            var ent = 1.0
            if (total > 0) {
                ent = 0.0
                for (i in 0 until n) {
                    val q = cols[d][i] / total
                    if (q > 0) ent -= q * ln(q)
                }
                ent /= ln(n.toDouble())
            }
            p.entW[d] = 1 - ent
        }
        val ew = p.entW.sum()
        for (d in 0 until DIM) p.entW[d] = (1 - LAMBDA) * (p.entW[d] / (if (ew != 0.0) ew else DIM.toDouble())) + LAMBDA / DIM

        for (tier in 1..5) {
            val qs = when (tier) {
                1 -> QUESTIONS.take(12)
                2 -> QUESTIONS.take(20)
                3 -> QUESTIONS.take(30)
                4 -> QUESTIONS.take(40)
                else -> QUESTIONS
            }
            val tm = DoubleArray(DIM)
            val mu = DoubleArray(DIM)
            val var2 = DoubleArray(DIM)
            for (q in qs) {
                val rowMax = DoubleArray(DIM)
                for (o in q.options) for (d in 0 until DIM) {
                    val w = o.s[d].toDouble()
                    mu[d] += w / 4
                    if (w > rowMax[d]) rowMax[d] = w
                }
                for (d in 0 until DIM) tm[d] += rowMax[d]
            }
            for (q in qs) for (d in 0 until DIM) {
                var sq = 0.0
                var m4 = 0.0
                for (o in q.options) {
                    val w = o.s[d].toDouble()
                    sq += w * w
                    m4 += w / 4
                }
                var2[d] += sq / 4 - m4 * m4
            }
            // ★ 水平测量制（2026-09-28，与 DcpmEngine 同步）：
            //   玩家分 = 所选选项该维值的平均（0–10）；基准均值 = 全部选项均值；
            //   基准方差 = Σ每题方差 / 题数²（因为玩家分是 N 题取平均）。
            val nn = qs.size.toDouble()
            for (d in 0 until DIM) {
                if (tm[d] == 0.0) tm[d] = 1.0
                mu[d] = mu[d] / nn
                var2[d] = if (var2[d] < 0) 0.0 else var2[d] / (nn * nn)
            }
            p.tmax[tier] = tm
            p.mean[tier] = mu
            p.std[tier] = DoubleArray(DIM) { sqrt(var2[it]) }
        }

        val mu3 = p.mean[3]!!
        val sd3 = p.std[3]!!
        for (c in CHARACTERS) {
            val z = DoubleArray(DIM) { d -> (c.stats[d] - mu3[d]) / (if (sd3[d] > 1e-9) sd3[d] else 1.0) }
            var acc = 0.0
            for (d in 0 until DIM) acc += p.entW[d] * z[d] * z[d]
            p.z[c.id] = z
            p.cosa[c.id] = sqrt(acc)
        }
        var dw = 0.0
        for (d in 0 until DIM) dw += p.entW[d]
        p.dwb = sqrt(dw)

        for ((fid, members) in FACTIONS) {
            val c = DoubleArray(DIM)
            for (m in members) {
                val s = CHR_STATS[m]!!
                for (d in 0 until DIM) c[d] += s[d]
            }
            val k = members.size
            for (d in 0 until DIM) c[d] /= k
            p.centroids[fid] = norm(c)
        }
        return p
    }

    fun score(pa: List<Double>, tier: Int, p: Params, prior: DoubleArray?): List<Scored> {
        val mu = p.mean[tier]!!
        val sd = p.std[tier]!!
        val q = DoubleArray(DIM) { d -> (pa[d] - mu[d]) / (if (sd[d] > 1e-9) sd[d] else 1.0) }
        var qq = 0.0
        for (d in 0 until DIM) qq += p.entW[d] * q[d] * q[d]
        val qn = sqrt(qq)
        val raw = CHARACTERS.map { c ->
            var num = 0.0
            val z = p.z[c.id]!!
            for (d in 0 until DIM) num += p.entW[d] * q[d] * z[d]
            val cos = if (qn > 0 && p.cosa[c.id]!! > 0) num / (qn * p.cosa[c.id]!!) else 0.0
            var d2 = qq + p.cosa[c.id]!! * p.cosa[c.id]!! - 2 * num
            if (d2 < 0) d2 = 0.0
            c.id to (cos - MU * sqrt(d2) / (if (p.dwb != 0.0) p.dwb else 1.0))
        }
        val tau = when (tier) {
            1 -> 0.07
            2 -> 0.08
            3 -> 0.09
            4 -> 0.095
            else -> 0.10
        }
        var m = -1e9
        for (r in raw) if (r.second > m) m = r.second
        var sum = 0.0
        val probs = DoubleArray(raw.size)
        for (i in raw.indices) {
            var z = exp((raw[i].second - m) / tau)
            if (prior != null) z *= prior[i]
            probs[i] = z
            sum += z
        }
        if (sum <= 0) sum = 1.0
        return raw.mapIndexed { i, r -> Scored(r.first, r.second, probs[i] / sum) }
            .sortedByDescending { it.prob }
    }

    fun calibrate(p: Params, seed: Int): Map<Int, DoubleArray> {
        val rnd = Rand(seed)
        val n = CHARACTERS.size
        val qOpts = QUESTIONS.map { q -> q.options.map { o -> o.s } }
        val pi = HashMap<Int, DoubleArray>()
        for (t in 1..5) pi[t] = DoubleArray(n) { 1.0 }
        val st = DoubleArray(DIM)
        repeat(ITER) {
            val acc = HashMap<Int, DoubleArray>()
            for (t in 1..5) acc[t] = DoubleArray(n)
            repeat(N) {
                for (qidx in qOpts.indices) {
                    val os = qOpts[qidx][(rnd.next() * 4.0).toInt()]
                    for (d in 0 until DIM) st[d] += os[d].toDouble()
                    // 水平测量制：逐档采样点（12 / 20 / 30 / 40 题；50 题在循环外）
                    when (qidx) {
                        11 -> {
                            val q = DoubleArray(DIM) { d -> st[d] / 12.0 }
                            score(q.toList(), 1, p, pi[1]).forEach { r ->
                                val idx = CHARACTERS.indexOfFirst { c -> c.id == r.id }
                                acc[1]!![idx] += r.prob
                            }
                        }
                        19 -> {
                            val q = DoubleArray(DIM) { d -> st[d] / 20.0 }
                            score(q.toList(), 2, p, pi[2]).forEach { r ->
                                val idx = CHARACTERS.indexOfFirst { c -> c.id == r.id }
                                acc[2]!![idx] += r.prob
                            }
                        }
                        29 -> {
                            val q = DoubleArray(DIM) { d -> st[d] / 30.0 }
                            score(q.toList(), 3, p, pi[3]).forEach { r ->
                                val idx = CHARACTERS.indexOfFirst { c -> c.id == r.id }
                                acc[3]!![idx] += r.prob
                            }
                        }
                        39 -> {
                            val q = DoubleArray(DIM) { d -> st[d] / 40.0 }
                            score(q.toList(), 4, p, pi[4]).forEach { r ->
                                val idx = CHARACTERS.indexOfFirst { c -> c.id == r.id }
                                acc[4]!![idx] += r.prob
                            }
                        }
                    }
                }
                // 水平测量制：全部题取平均（50 题档）
                val q5 = DoubleArray(DIM) { d -> st[d] / qOpts.size.toDouble() }
                score(q5.toList(), 5, p, pi[5]).forEach { r ->
                    val idx = CHARACTERS.indexOfFirst { c -> c.id == r.id }
                    acc[5]!![idx] += r.prob
                }
                for (d in 0 until DIM) st[d] = 0.0
            }
            for (t in 1..5) {
                for (i in 0 until n) {
                    val avg = acc[t]!![i] / N
                    var np = pi[t]!![i] * (TARGET / maxOf(avg, 1e-9)).pow(GAMMA)
                    if (!np.isFinite()) np = pi[t]!![i] * 2
                    pi[t]!![i] = minOf(maxOf(np, 1e-9), 1e6)
                }
                val s = pi[t]!!.sum()
                for (i in 0 until n) pi[t]!![i] /= s
            }
        }
        // 保底之后再归一化一次（与 DcpmEngine 同步）
        for (t in 1..5) {
            var s = 0.0
            for (i in 0 until n) {
                if (pi[t]!![i] < 1e-9) pi[t]!![i] = 1e-9
                s += pi[t]!![i]
            }
            for (i in 0 until n) pi[t]!![i] /= s
        }
        return pi
    }
}
