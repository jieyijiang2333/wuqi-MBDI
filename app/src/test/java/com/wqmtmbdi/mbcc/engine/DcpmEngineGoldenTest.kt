package com.wqmtmbdi.mbcc.engine

import com.wqmtmbdi.mbcc.data.ACHIEVEMENTS
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.FACTIONS
import com.wqmtmbdi.mbcc.data.QUESTIONS
import com.wqmtmbdi.mbcc.data.RESONANCE_QUESTIONS
import com.wqmtmbdi.mbcc.data.RESONANCE_TYPES
import com.wqmtmbdi.mbcc.data.TIER_LIMITS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * DCPM 引擎金标单测：
 * 以独立参考实现 DcpmReference 为金标，验证 DcpmEngine 位级一致（容差 1e-12）。
 */
class DcpmEngineGoldenTest {

    private lateinit var ref: DcpmReference.Params

    @Before
    fun setup() {
        DcpmEngine.init()
        ref = DcpmReference.buildParams()
    }

    private fun assertDoubleArr(a: DoubleArray, b: DoubleArray, tol: Double = 1e-12, label: String = "") {
        assertEquals("$label 长度", a.size, b.size)
        for (i in a.indices) {
            assertEquals("$label[$i]", a[i], b[i], tol)
        }
    }

    @Test
    fun gameData_counts_are_correct() {
        // ★ v1.5.0：全角色库 98 名（内置 20 + 新增 78）
        assertEquals("角色数", 98, CHARACTERS.size)
        // 角色等级必须全员登记，且只有四种取值
        val validRanks = setOf("狂厄级", "高危级", "普管级", "特殊")
        CHARACTERS.forEach { c ->
            val r = com.wqmtmbdi.mbcc.data.rankOf(c.id)
            assertTrue("${c.id} 等级未登记", r.isNotEmpty())
            assertTrue("${c.id} 等级非法: $r", r in validRanks)
        }
        assertEquals("等级表与角色数一致", CHARACTERS.size, com.wqmtmbdi.mbcc.data.CHAR_RANK.size)
        assertEquals("50题", 50, QUESTIONS.size)
        assertEquals("共鸣12题", 12, RESONANCE_QUESTIONS.size)
        assertEquals("6特化", 6, RESONANCE_TYPES.size)
        assertEquals("10阵营", 10, FACTIONS.size)
        assertEquals("20成就", 20, ACHIEVEMENTS.size)
        // v1.5.1：6 套配色 × 浅色/深色 = 12 个主题实例
        assertEquals("6 套配色", 6, com.wqmtmbdi.mbcc.MBDIThemes.FAMILIES.size)
        assertEquals("12 个主题实例", 12, com.wqmtmbdi.mbcc.MBDIThemes.ALL.size)
        // v1.5.0：公告 4 条（id 8 停更公告 / 7 / 6 / 4，历史数据只增不删）
        assertEquals("公告", 4, com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS.size)
        assertEquals("TIER_LIMITS", mapOf(1 to 12, 2 to 20, 3 to 30, 4 to 40, 5 to 50), TIER_LIMITS)
        // 每个角色七维
        CHARACTERS.forEach { c -> assertEquals("${c.id} 七维长度", 7, c.stats.size) }
        // 每题 4 选项 × 7 维
        QUESTIONS.forEach { q ->
            assertEquals("${q.id} 选项数", 4, q.options.size)
            q.options.forEach { o -> assertEquals("${q.id}${o.l} 权重长度", 7, o.s.size) }
        }
        // 各维度理论最大不为 0（保证缩放分母）
        (2..3).forEach { t -> assertTrue("tier$t tmax 全 >0", DcpmEngine.tmaxOf(t).all { it > 0 }) }
    }

    @Test
    fun init_params_match_reference() {
        assertDoubleArr(DcpmEngine.entropyWeights(), ref.entW, label = "entW")
        (2..3).forEach { t ->
            assertDoubleArr(DcpmEngine.tmaxOf(t), ref.tmax[t]!!, label = "tmax$t")
            assertDoubleArr(DcpmEngine.meanOf(t), ref.mean[t]!!, label = "mean$t")
            assertDoubleArr(DcpmEngine.stdOf(t), ref.std[t]!!, label = "std$t")
        }
        CHARACTERS.forEach { c ->
            assertDoubleArr(DcpmEngine.normOf(c.id)!!, ref.z[c.id]!!, label = "z.${c.id}")
            assertEquals("cosa.${c.id}", ref.cosa[c.id]!!, DcpmEngine.cosaOf(c.id), 1e-12)
        }
        assertEquals("dwb", ref.dwb, DcpmEngine.dwbOf(), 1e-12)
    }

    @Test
    fun scores_match_reference_for_all_profiles() {
        val profiles = listOf(
            listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            listOf(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0),
            listOf(10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            listOf(0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0),
            listOf(5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0),
            listOf(9.0, 10.0, 5.0, 8.0, 9.0, 5.0, 4.0),
            listOf(0.0, 8.0, 4.0, 5.0, 8.0, 5.0, 4.0),
            listOf(3.0, 8.0, 6.0, 5.0, 9.0, 4.0, 2.0)
        )
        val uniform = DoubleArray(CHARACTERS.size) { 1.0 }
        for (tier in 1..5) {
            for ((idx, p) in profiles.withIndex()) {
                val eng = DcpmEngine.scores(p, tier, uniform)   // 显式均匀先验，避免受 calibrate 测试污染
                val rref = DcpmReference.score(p, tier, ref, null)
                assertEquals("profile$idx tier$tier 排序长度", eng.size, rref.size)
                eng.zip(rref).forEachIndexed { i, (e, r) ->
                    assertEquals("p$idx t$tier #$i id", r.id, e.id)
                    assertEquals("p$idx t$tier #$i S", r.S, e.S, 1e-12)
                    assertEquals("p$idx t$tier #$i prob", r.prob, e.prob, 1e-12)
                }
            }
        }
    }

    @Test
    fun matchFaction_matches_reference() {
        val profiles = listOf(
            listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            listOf(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0),
            listOf(9.0, 10.0, 5.0, 8.0, 9.0, 5.0, 4.0),
            listOf(0.0, 8.0, 4.0, 5.0, 8.0, 5.0, 4.0),
            listOf(5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0)
        )
        for (p in profiles) {
            val (fid, sim) = DcpmEngine.matchFaction(p)
            var best = ""
            var bestSim = -Double.MAX_VALUE
            ref.centroids.forEach { (k, c) ->
                val s = DcpmReference.dot(DcpmReference.norm(p.toDoubleArray()), c)
                if (s > bestSim) { bestSim = s; best = k }
            }
            assertEquals("阵营", best, fid)
            assertEquals("阵营相似度", bestSim, sim, 1e-12)
        }
    }

    @Test
    fun calibrate_matches_reference_prior() {
        val seed = 12345
        val eng = Mulberry32(seed)
        DcpmEngine.calibrate(eng::next)
        val rp = DcpmReference.calibrate(ref, seed)
        for (t in 1..5) {
            val e = DcpmEngine.priorOf(t)
            assertDoubleArr(e, rp[t]!!, tol = 1e-12, label = "prior$t")
            // 不变量：先验归一化、非负
            assertTrue("tier$t 先验全部 >0", e.all { it > 0 })
            assertEquals("tier$t 先验和≈1", 1.0, e.sum(), 1e-9)
        }
    }

    /**
     * ★ v1.5.2 收集补全：同范围内已拥有角色降权、未解锁角色相对抬升；
     *   概率总和仍为 1；范围外角色不受影响（相对概率不变）。
     */
    @Test
    fun collection_boost_reweights_only_in_range() {
        val p = listOf(6.0, 8.0, 5.0, 7.0, 7.0, 6.0, 6.0)
        val plain = DcpmEngine.scores(p, 2, DoubleArray(CHARACTERS.size) { 1.0 })
        assertEquals("概率和=1", 1.0, plain.sumOf { it.prob }, 1e-9)
        val owned = setOf(plain[0].id, plain[1].id, plain[2].id)
        val boosted = DcpmEngine.scores(
            p, 2, DoubleArray(CHARACTERS.size) { 1.0 },
            owned = owned, ownedPenalty = 0.55, freshBonus = 1.10
        )
        assertEquals("补全后概率和仍=1", 1.0, boosted.sumOf { it.prob }, 1e-9)
        // 已拥有角色的概率必须下降
        for (id in owned) {
            val before = plain.first { it.id == id }.prob
            val after = boosted.first { it.id == id }.prob
            assertTrue("$id 应降权: $before -> $after", after < before)
        }
        // 未解锁且进入候选范围的最高概率角色应获得抬升
        val freshTop = boosted.firstOrNull { it.id !in owned }
        assertTrue("应有未解锁角色", freshTop != null)
    }
}
