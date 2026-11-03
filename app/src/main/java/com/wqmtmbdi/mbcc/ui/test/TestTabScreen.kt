package com.wqmtmbdi.mbcc.ui.test

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.GameChar
import com.wqmtmbdi.mbcc.data.QUESTIONS
import com.wqmtmbdi.mbcc.data.RESONANCE_QUESTIONS
import com.wqmtmbdi.mbcc.data.TIER_LIMITS
import com.wqmtmbdi.mbcc.danger
import com.wqmtmbdi.mbcc.engine.DcpmEngine
import com.wqmtmbdi.mbcc.engine.RankResult
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.rim
import com.wqmtmbdi.mbcc.scan
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import com.wqmtmbdi.mbcc.ui.resonance.ResonanceResult
import com.wqmtmbdi.mbcc.ui.resonance.ResonanceResultView
import com.wqmtmbdi.mbcc.ui.resonance.selectResonanceOpt
import com.wqmtmbdi.mbcc.ui.resonance.showResonanceResult
import com.wqmtmbdi.mbcc.ui.resonance.startResonanceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 测试流程阶段 */
enum class TestStage { HOME, TIER_SELECT, QUESTION, RESULT }

/** 选项统一载体（QOption.s 为 List<Number>，ResOption.s 为 Map<String,Int>） */
private data class OptUi(val l: String, val t: String, val s: Any)

/** MBCC 鉴定结果数据（对齐 HTML showResult 计算） */
data class MbccResult(
    val ranked: List<RankResult>,
    val char: GameChar,
    val char2: GameChar?,
    val char3: GameChar?,
    val pScaled: List<Double>,
    val faction: String?,
    val topProb: Double,
    val tier: Int
)

/** 测试页状态（对齐 HTML 全局变量：testTier/currentQ/playerStats/selectedAnswers/testMode） */
class TestState {
    var mode by mutableStateOf("mbcc")                 // mbcc | resonance
    var stage by mutableStateOf(TestStage.HOME)
    var tier by mutableIntStateOf(2)
    var currentQ by mutableIntStateOf(0)
    var playerStats by mutableStateOf(listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
    val selectedAnswers = mutableStateMapOf<Int, List<Double>>()
    var mbccResult by mutableStateOf<MbccResult?>(null)
    var transitioning by mutableStateOf(false)
    /** 结果揭晓动画是否已播完（每次结算结果时重置） */
    var revealDone by mutableStateOf(false)
    // 狂厄共鸣状态（阶段四）
    val resScores = mutableStateMapOf<String, Int>()
    val resAnswers = mutableStateMapOf<Int, Map<String, Int>>()
    var resResult by mutableStateOf<com.wqmtmbdi.mbcc.ui.resonance.ResonanceResult?>(null)
    /** 共鸣「频率接收」仪式是否已播完（每次结算共鸣结果时重置，见 ResonanceReveal） */
    var resRevealed by mutableStateOf(true)

    /**
     * 是否在等「退出本次答题」的确认。
     * ★ 2026-09-29 第 5 批新增：**返回键也走这个确认** ——
     * 以前答题途中按返回键会直接回首页，整场答案无声无息就没了；
     * 现在返回键和答题页右上角的 ✕ 共用同一个确认弹窗。
     */
    var pendingExit by mutableStateOf(false)
}

/**
 * 测试页（人格鉴定 + 狂厄共鸣）。
 *
 * ★ 2026-09-28：[st] 由外层（AppRoot）持有 —— 以前状态存在本函数里，
 * **切到图鉴/我的再切回来，整场测试就没了**（回到首页）。
 * ★ 第 13 批：公告弹窗状态提升到了 AppRoot（全局唯一），这里只通过 [onOpenAnnouncement]
 *   上报「点了铃铛」；自动弹出由 AppRoot 统一负责（见 MainActivity 的 showAnn）。
 */
@Composable
fun TestTabScreen(
    st: TestState,
    modifier: Modifier = Modifier,
    onOpenAnnouncement: () -> Unit = {}
) {
    val t = AppStorage.currentMBDITheme()
    val scroll = rememberScrollState()
    Column(modifier.fillMaxSize()) {
        // 标题栏（人格鉴定 + 公告铃铛）：只有主页显示；进入选档 / 答题 / 结果后整块隐藏
        if (st.stage == TestStage.HOME) {
            // 标题栏：大标题 + 右上角公告图标（未读带红点）
            // 原来是「📢 公告」一整块卡片挤在标题旁边，既占位置又不显眼
            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Lucide.ClipboardList, contentDescription = null,
                        modifier = Modifier.size(22.dp), tint = colorFromHex(t.gold)
                    )
                    Text(
                        "人格鉴定",
                        fontWeight = FontWeight.Bold,
                        fontSize = MbdiType.Title,
                        color = colorFromHex(t.gold),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                val hasUnread = (AppStorage.data.readAnnouncements.maxOrNull() ?: 0) < (ANNOUNCEMENTS.maxOfOrNull { it.id } ?: 0)
                Box(
                    Modifier
                        .size(44.dp)                       // 热区 ≥44dp
                        .clip(RoundedCornerShape(50))
                        .clickable { onOpenAnnouncement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Lucide.Bell, contentDescription = "公告",
                        modifier = Modifier.size(22.dp),
                        tint = colorFromHex(if (hasUnread) t.gold else t.text3)
                    )
                    if (hasUnread) {
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 9.dp, end = 9.dp)
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colorFromHex(t.danger))
                        )
                    }
                }
            }
        }
        when (st.stage) {
            TestStage.HOME -> HomeSection(st)
            TestStage.TIER_SELECT -> TierSelectSection(st)
            TestStage.QUESTION -> QuestionSection(st)
            TestStage.RESULT -> {
                Box(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().verticalScroll(scroll).padding(horizontal = 8.dp)) {
                        val r = st.mbccResult
                        // 「重新测试」直接进档位选择（比回首页再点一次更顺）；「返回主页面」才回首页
                        if (r != null) MbccResultView(
                            r,
                            revealDone = st.revealDone,
                            onRestart = { st.stage = TestStage.TIER_SELECT },
                            onBackHome = { resetToHome(st) }
                        )
                        else st.resResult?.let { rr -> com.wqmtmbdi.mbcc.ui.resonance.ResonanceResultView(rr, onRestart = { resetToHome(st) }) }
                    }
                    // 揭晓动画：盖在可滚动内容之上，只在刚结算完时播一次（点任意处可跳过）
                    val rr = st.mbccResult
                    if (rr != null && !st.revealDone) {
                        ResultReveal(rr, onDone = { st.revealDone = true })
                    }
                    // 狂厄共鸣的「频率接收」仪式（方案 5.6）：波纹从中心扩散 + 中触感，点任意处跳过
                    if (rr == null && st.resResult != null && !st.resRevealed) {
                        com.wqmtmbdi.mbcc.ui.resonance.ResonanceReveal(onDone = { st.resRevealed = true })
                    }
                }
            }
        }
    }
    // ★ 第 13 批：公告弹窗已提升到 AppRoot（全局唯一），这里不再自己渲染 AnnouncementOverlay
}

/**
 * 首页（双玩法入口）。
 *
 * ★ 2026-09-28 第 2 批重做：两个玩法从 Tab 切换改成两张并列入口卡（各自的引导语原样保留在卡内）。
 * ★ 2026-10-01 简洁化（用户要求「排布太多」）：去掉「MBDI 人格鉴定所」小标题、通用引导语
 *   （那段文字与「人格鉴定」卡内的引导语重复）以及「收集进度」卡 —— 首页只留顶栏大标题 +
 *   两张玩法卡；**所有介绍文字都在卡内原样保留**。收集进度统一看图鉴页（那边有总进度）。
 */
@Composable
private fun HomeSection(st: TestState) {
    val t = AppStorage.currentMBDITheme()

    Column(
        // 底部给悬浮胶囊留出高度（导航栏是叠加层，不占高度）
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 12.dp, end = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(MbdiSpace.m)
    ) {
        Spacer(Modifier.height(MbdiSpace.xs))

        // ---- 两个玩法入口（整卡可点）----
        // ★ 2026-09-28：原来是两个 Tag 页切换，各自的引导语也跟着被藏在 Tag 里；
        //   现在取消 Tag，**两段引导语原样保留在各自的入口卡内**。
        ModeCard(
            title = "人格鉴定",
            sub = "七维画像 → 匹配禁闭者",
            meta = "32 题 · 约 5 分钟（也可选 50 题深度档）",
            desc = "狂厄渗透了狄斯城的每一个角落。\n" +
                "有人被它吞噬，有人与它共生，有人在它之中找到了真正的自己。\n\n" +
                "MBDI——狂厄秉性指标（Mania-Borne Disposition Indicator）\n" +
                "以七维性格画像（同理心 / 意志力 / 体质 / 教育程度 / 智商 / 敏捷 / 力量）\n" +
                "测定你的狂厄性质，匹配与你灵魂同频的禁闭者。",
            accent = t.gold
        ) {
            st.mode = "mbcc"
            st.stage = TestStage.TIER_SELECT
        }
        ModeCard(
            title = "狂厄共鸣",
            sub = "六维频率 → 特化归档",
            meta = "12 题 · 约 2 分钟",
            desc = "狂厄是一道永不消逝的频率，在狄斯城的每个角落反复震荡。\n" +
                "每个被侵蚀的灵魂，都在发出属于自己的共鸣。\n\n" +
                "由零镜系统捕获并解码的全新波形——\n" +
                "狂厄共鸣 · 零镜回响特化（Calamity Resonance Spectrum）\n" +
                "你的频率，正在被接收。",
            accent = t.purple
        ) {
            st.mode = "resonance"
            com.wqmtmbdi.mbcc.ui.resonance.startResonanceTest(st)
        }

        Spacer(Modifier.height(MbdiSpace.s))
    }
}

/**
 * 玩法入口卡（双通道 2.0，方案 5.1）。
 *
 * [desc] = **原来那个 Tag 页自己的引导语** —— 取消 Tag 后原文保留在这里，
 * 两个玩法各留各的，不再合并成一段通用说明。
 *
 * ★ 2026-10-01（第 2 批）：卡右缘 12° 斜切装饰线 + 一条缓慢流动的主题色光带（金 / 紫各一条）；
 *   整卡按压 = scale 0.98 + 光带加速 + 轻触感（安静模式：光带静态不流动）。
 */
@Composable
private fun ModeCard(
    title: String,
    sub: String,
    meta: String,
    desc: String,
    accent: String,
    onClick: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val c = colorFromHex(accent)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = MbdiSpring.Snappy,
        label = "modeScale"
    )
    LaunchedEffect(interaction) {
        interaction.interactions.collect { ev ->
            if (ev is androidx.compose.foundation.interaction.PressInteraction.Press) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        // ★ 用户要求：删掉卡内流动光带（保留右缘静态斜切装饰线）
        Column(Modifier.fillMaxWidth().channelEdge(c).padding(MbdiSpace.l)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.width(4.dp).height(44.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(c)
                )
                Column(Modifier.weight(1f).padding(start = MbdiSpace.m)) {
                    Text(title, fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
                    Text(
                        sub,
                        fontSize = MbdiType.Label,
                        color = c,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        meta,
                        fontSize = MbdiType.Caption,
                        color = colorFromHex(t.text3),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    Lucide.ChevronRight, contentDescription = null,
                    modifier = Modifier.size(20.dp), tint = colorFromHex(t.text3)
                )
            }
            Text(
                desc,
                fontSize = MbdiType.Label,
                lineHeight = 20.sp,
                color = colorFromHex(t.text2),
                modifier = Modifier.padding(top = MbdiSpace.m)
            )
        }
    }
}

/**
 * 「双通道」卡面（画在内容之下）：只有右缘 12° 斜切装饰线。
 * ★ 用户要求：原「缓慢流动的主题色光带」已删除。
 */
private fun Modifier.channelEdge(color: Color): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return@drawBehind
    val slant = kotlin.math.tan(Math.toRadians(12.0)).toFloat() * h

    // 右缘 12° 斜切装饰线（一实一淡两道）
    val topX = w - 14.dp.toPx()
    drawLine(color.copy(alpha = 0.35f), Offset(topX, 0f), Offset(topX - slant, h), 1.5.dp.toPx())
    drawLine(color.copy(alpha = 0.15f), Offset(topX - 6.dp.toPx(), 0f), Offset(topX - slant - 6.dp.toPx(), h), 1.dp.toPx())
}

/** 金色主按钮（对齐 HTML .btn-gold） */
@Composable
fun GoldButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    Text(
        text, fontSize = 13.sp, fontWeight = FontWeight.Bold,
        color = colorFromHex(t.bg), letterSpacing = 2.sp, textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(colorFromHex(t.gold), colorFromHex(t.goldDim))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 12.dp)
    )
}

// ===== 档位选择（v1.5.2：严格 / 娱乐 两个 tab） =====

/**
 * 档位选择（v1.5.2 改版）：进鉴定先选模式 —— **严格 / 娱乐** 两个 tab。
 *
 * - 严格（30 / 40 / 50 题）：纯人格匹配，不做收集补全；
 *   顶部告知「角色数值存在相近区间，建议多测几次；本软件为娱乐性项目」。
 * - 娱乐（12 / 20 题）：含收集补全（已拥有角色降权、同范围未解锁抬升），
 *   并说明「同一份答案在不同收集进度下结果可能不同」。
 *
 * ★ 题量口径：标准误 ∝ 1/√N，题越多画像越稳；50 题已是题库上限（再准要扩充题库）。
 */
@Composable
private fun TierSelectSection(st: TestState) {
    val t = AppStorage.currentMBDITheme()
    val lastTier = AppStorage.data.lastTier
    var tab by remember { mutableStateOf("strict") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "选择鉴定模式",
            fontSize = MbdiType.Title, fontWeight = FontWeight.Black, color = colorFromHex(t.gold)
        )
        Text(
            "严格 = 只按人格匹配；娱乐 = 会补全你还没收集到的角色",
            fontSize = MbdiType.Label, color = colorFromHex(t.text3),
            modifier = Modifier.padding(top = 4.dp, bottom = MbdiSpace.m)
        )

        // ---- 严格 / 娱乐 两个 tab ----
        Row(
            Modifier
                .fillMaxWidth()
                .mbdiCard(shape = RoundedCornerShape(MbdiShape.pill))
                .padding(4.dp)
        ) {
            listOf("strict" to "严格", "casual" to "娱乐").forEach { (id, label) ->
                val active = tab == id
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(MbdiShape.pill))
                        .background(if (active) colorFromHex(t.gold).copy(alpha = 0.16f) else Color.Transparent)
                        .clickable { tab = id }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label, fontSize = MbdiType.Body,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = colorFromHex(if (active) t.gold else t.text3)
                    )
                }
            }
        }
        Spacer(Modifier.height(MbdiSpace.m))

        // ---- 模式说明 ----
        if (tab == "strict") {
            ModeNote(
                "角色数值存在大量相近区间，单次结果不一定落在唯一的「最像」上。" +
                    "建议隔一段时间多测几次，取反复出现的角色作为相对准确的结果。" +
                    "本软件是粉丝二创的娱乐性项目，鉴定结果仅供参考。"
            )
        } else {
            ModeNote(
                "娱乐模式会做收集补全：已拥有的角色权重降低，同范围内未解锁的角色更容易出现；" +
                    "同一份答案在不同收集进度下结果可能不同。同样建议多测几次。"
            )
        }
        Spacer(Modifier.height(MbdiSpace.m))

        if (tab == "strict") {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MbdiSpace.m)
            ) {
                TierCard(
                    title = "严格 · 标准",
                    meta = "30 题 · 约 5 分钟",
                    desc = "纯人格匹配 · 不做收集补全",
                    main = t.purpleDim, dim = t.purpleDim,
                    badge = "严格",
                    lastUsed = lastTier == 3,
                    tickAt = 30
                ) { startTest(st, 3) }
                TierCard(
                    title = "严格 · 加强",
                    meta = "40 题 · 约 6 分钟",
                    desc = "纯人格匹配 · 题量更多，画像更稳",
                    main = t.purple, dim = t.purpleDim,
                    badge = "严格",
                    lastUsed = lastTier == 4,
                    tickAt = 40
                ) { startTest(st, 4) }
                TierCard(
                    title = "严格 · 完整",
                    meta = "50 题 · 约 8 分钟",
                    desc = "题库上限 · 最权威的一档",
                    main = t.purple, dim = t.purpleDim,
                    badge = "最权威",
                    lastUsed = lastTier == 5,
                    tickAt = 50
                ) { startTest(st, 5) }
            }
        } else {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MbdiSpace.m)
            ) {
                TierCard(
                    title = "娱乐 · 快速",
                    meta = "12 题 · 约 2 分钟",
                    desc = "补全力度最强 · 适合集角色",
                    main = t.goldDim, dim = t.text3,
                    badge = "娱乐",
                    lastUsed = lastTier == 1,
                    tickAt = 12
                ) { startTest(st, 1) }
                TierCard(
                    title = "娱乐 · 标准",
                    meta = "20 题 · 约 3 分钟",
                    desc = "温和补全 · 比快速档稍稳一点",
                    main = t.gold, dim = t.goldDim,
                    badge = "娱乐",
                    lastUsed = lastTier == 2,
                    tickAt = 20
                ) { startTest(st, 2) }
            }
        }

        Spacer(Modifier.height(MbdiSpace.l))
        MbdiButton("返回", { st.stage = TestStage.HOME }, style = BtnStyle.Secondary, leading = Lucide.ArrowLeft)
    }
}

/** 模式说明小卡（严格 / 娱乐各一段，用户要求把话说清楚） */
@Composable
private fun ModeNote(text: String) {
    val t = AppStorage.currentMBDITheme()
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MbdiShape.small))
            .background(colorFromHex(t.card).copy(alpha = 0.92f))
            .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.small))
            .padding(horizontal = MbdiSpace.m, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Lucide.Info, contentDescription = null,
            modifier = Modifier.size(14.dp).padding(top = 2.dp), tint = colorFromHex(t.text3)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text3)
        )
    }
}

/**
 * 档位「频段卡」（方案 5.2）：
 *  - 两卡主色区分（深度 = 金 / 共振 = 紫），卡面带**刻度尺**并高亮当前题量位置（仪器感）；
 *  - 按下即反馈：整卡亮起（描边变实 + 主题色光晕）+ 轻触感；松手才进入答题（保留「点击即开始」）。
 */
@Composable
private fun TierCard(
    title: String,
    meta: String,
    desc: String,
    main: String,
    dim: String,
    lastUsed: Boolean,
    tickAt: Int,
    badge: String? = null,
    onClick: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val c = colorFromHex(main)
    val shape = RoundedCornerShape(MbdiShape.card)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val glow by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = MbdiSpring.Snappy,
        label = "tierGlow"
    )
    LaunchedEffect(interaction) {
        interaction.interactions.collect { ev ->
            if (ev is androidx.compose.foundation.interaction.PressInteraction.Press) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .mbdiCard(shape = shape)
            .border(1.dp, c.copy(alpha = 0.22f + 0.58f * glow), shape)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(MbdiSpace.l)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(main),
                modifier = Modifier.weight(1f)
            )
            if (badge != null) {
                MbdiChip(badge, colorFromHex(dim), filled = true)
            }
            if (lastUsed) {
                Spacer(Modifier.width(6.dp))
                MbdiChip("上次选的是这个", colorFromHex(dim), filled = true)
            }
        }
        Text(
            meta,
            fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(dim),
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            desc,
            fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text2),
            modifier = Modifier.padding(top = 6.dp)
        )
        Spacer(Modifier.height(MbdiSpace.m))
        // 刻度尺：50 格（对应最高档题量），高亮当前档位所在刻度
        Canvas(Modifier.fillMaxWidth().height(12.dp)) {
            val n = 50
            val gap = size.width / (n - 1)
            val track = colorFromHex(t.brd)
            for (i in 0 until n) {
                val major = (i + 1) % 5 == 0
                val hh = if (major) size.height else size.height * 0.55f
                val hot = i == (tickAt - 1).coerceIn(0, n - 1)
                drawLine(
                    color = if (hot) c else track,
                    start = Offset(i * gap, size.height - hh),
                    end = Offset(i * gap, size.height),
                    strokeWidth = if (hot) 2.5.dp.toPx() else if (major) 1.4.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            // 高亮刻度旁一枚小光点（「当前工作频率」）
            val hx = (tickAt - 1).coerceIn(0, n - 1) * gap
            drawCircle(c.copy(alpha = 0.30f), radius = 3.5.dp.toPx(), center = Offset(hx, size.height * 0.16f))
        }
    }
}

// ===== 题目页 =====

/**
 * 答题页。
 *
 * ★ 2026-09-28 第 2 批重做（方案 4.3）：
 *  - 进度条 3dp → **6dp**，题号加粗，一眼看出答到哪了；
 *  - 「⌂ 主菜单」→ 右上角 **✕**，点击先问一句「退出会清空本次答题」（原来点了直接丢答案）；
 *  - 选项热区 ≥56dp、选中态更明显（底色 + 加粗描边）；
 *  - 选完自动跳转 380ms → **650ms**（太快看不清自己选了什么）；
 *  - 去掉下面那句「请选择一个选项」—— 「下一题」按钮置灰本身就是提示；
 *  - 底部按钮**固定在屏幕底部**，不随题目滚动。
 */
@Composable
private fun QuestionSection(st: TestState) {
    val t = AppStorage.currentMBDITheme()
    val scope = rememberCoroutineScope()
    val isResonance = st.mode == "resonance"
    val mbccQs = if (isResonance) emptyList() else QUESTIONS.take(TIER_LIMITS[st.tier] ?: 32)
    val total = if (isResonance) RESONANCE_QUESTIONS.size else mbccQs.size
    val qLabel: String
    val qText: String
    val opts: List<OptUi>
    if (isResonance) {
        val rq = RESONANCE_QUESTIONS[st.currentQ]
        qLabel = rq.label; qText = rq.text
        opts = rq.options.map { OptUi(it.l, it.t, it.s) }
    } else {
        val q = mbccQs[st.currentQ]
        qLabel = q.label; qText = q.text
        opts = q.options.map { OptUi(it.l, it.t, it.s) }
    }
    val hasAnswer = if (isResonance) st.resAnswers.containsKey(st.currentQ) else st.selectedAnswers.containsKey(st.currentQ)
    val isLast = st.currentQ >= total - 1

    // ★ 2026-10-01（方案 5.3）：题干卡按方向入场 / 最后一题微脉冲 / 切回答题页时能量条闪一次
    val animOn = rememberAnimationsOn()
    var lastQ by remember { mutableIntStateOf(st.currentQ) }
    var dir by remember { mutableIntStateOf(1) }        // 1 = 下一题（从右入场），-1 = 上一题（从左入场）
    if (st.currentQ != lastQ) {
        dir = if (st.currentQ > lastQ) 1 else -1
        lastQ = st.currentQ
    }
    val enter = remember { Animatable(1f) }
    LaunchedEffect(st.currentQ) {
        if (!animOn) {
            enter.snapTo(1f)
            return@LaunchedEffect
        }
        enter.snapTo(0f)
        enter.animateTo(1f, MbdiSpring.Soft)
    }
    // 进入本页时已经答过题（切 Tab / 后台回来）→ 能量条播放一次「续接」闪光
    val resumedAtEntry = remember { st.currentQ > 0 }
    // ★ 用户要求：已删除「最后一题」主按钮下的泛光脉冲（resPulse）

    // ★ 2026-10-01：答题开始后底部导航整屏隐藏（沉浸答题，见 MainActivity）——
    //   原先给悬浮胶囊预留的 100dp 换成「手势条避让 + 一点呼吸」。
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
            .navigationBarsPadding()
            .padding(bottom = MbdiSpace.s)
    ) {
        // ---- 头部：题号 + 进度条 + 退出 ----
        Row(Modifier.fillMaxWidth().padding(top = MbdiSpace.s), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${st.currentQ + 1}",
                style = TextStyle(
                    fontSize = 17.sp, fontWeight = FontWeight.Black,
                    color = colorFromHex(t.gold), fontFeatureSettings = "tnum"
                )
            )
            Text(
                " / $total",
                style = TextStyle(
                    fontSize = MbdiType.Label, color = colorFromHex(t.text3),
                    fontFeatureSettings = "tnum"
                )
            )
            Spacer(Modifier.width(MbdiSpace.m))
            // 能量条（方案 5.3）：细轨 + 渐变能量 + 尾端光点 + 流动高光
            EnergyBar(
                progress = (st.currentQ + 1).toFloat() / total,
                resumed = resumedAtEntry,
                modifier = Modifier.weight(1f)
            )
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50))
                    .clickable { st.pendingExit = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Lucide.X, contentDescription = "退出答题",
                    modifier = Modifier.size(18.dp), tint = colorFromHex(t.text3)
                )
            }
        }
        Spacer(Modifier.height(MbdiSpace.m))
        // ---- 题目 + 选项（题目长时可滚动，底部按钮固定不动）----
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // 题干卡按方向入场（方案 5.3）：下一题从右、上一题从左，轻微上浮
            Column(
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = dir * (1f - enter.value) * 42f
                        alpha = 0.25f + 0.75f * enter.value
                    }
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .padding(MbdiSpace.l)
            ) {
                Text(qLabel, fontSize = MbdiType.Caption, color = colorFromHex(t.purple), letterSpacing = 2.sp)
                Text(
                    qText,
                    fontSize = MbdiType.Body, lineHeight = 24.sp, fontWeight = FontWeight.Medium,
                    color = colorFromHex(t.text),
                    modifier = Modifier.padding(top = MbdiSpace.s, bottom = MbdiSpace.m)
                )
                Column(verticalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                    opts.forEach { o ->
                        val selected = if (isResonance) st.resAnswers[st.currentQ] == o.s
                        else st.selectedAnswers[st.currentQ] == o.s
                        OptionRow(
                            label = o.l,
                            text = o.t,
                            selected = selected,
                            accent = t.purple,
                            // 自动跳转等待期：选中项右下角微光脉动的「记录光点」（方案 5.3）
                            recording = selected && st.transitioning
                        ) {
                            if (!st.transitioning) {
                                st.transitioning = true
                                if (isResonance) selectResonanceOpt(st, o.s as Map<String, Int>, o.l)
                                else selectOption(st, o.s as List<Double>, o.l)
                                scope.launch {
                                    // 跳转放慢到 650ms：原来的 380/260ms 太快，看不清自己选了什么
                                    delay(if (isResonance) 520 else 650)
                                    st.transitioning = false
                                    if (isLast) {
                                        if (isResonance) showResonanceResult(st) else showMbccResult(st)
                                    } else st.currentQ++
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(MbdiSpace.m))
        }
        // ---- 底部固定按钮 ----
        Row(
            Modifier.fillMaxWidth().padding(bottom = MbdiSpace.s),
            horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)
        ) {
            MbdiButton(
                "上一题",
                { st.currentQ-- },
                modifier = Modifier.weight(1f),
                style = BtnStyle.Secondary,
                enabled = st.currentQ > 0,
                leading = Lucide.ArrowLeft
            )
            // 「最后一题 / 下一题」按钮（★ 已删泛光脉冲包裹）
            MbdiButton(
                if (isLast) "查看结果" else "下一题",
                {
                    if (isLast) {
                        if (isResonance) showResonanceResult(st) else showMbccResult(st)
                    } else st.currentQ++
                },
                modifier = Modifier.weight(1f),
                style = BtnStyle.Primary,
                enabled = hasAnswer,
                trailing = Lucide.ArrowRight
            )
        }
    }

    // 退出确认：以前点「主菜单」直接把整场答题丢掉，什么都不问
    if (st.pendingExit) {
        Dialog(onDismissRequest = { st.pendingExit = false }) {
            Column(
                Modifier.fillMaxWidth(0.86f).mbdiSolid(shape = RoundedCornerShape(MbdiShape.panel)).padding(MbdiSpace.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "退出本次答题？",
                    fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                )
                Text(
                    "已经答过的 ${st.currentQ + 1} 题会被清空，下次要重新开始。",
                    fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text3),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = MbdiSpace.s)
                )
                Spacer(Modifier.height(MbdiSpace.l))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                    MbdiButton(
                        "继续答题", { st.pendingExit = false },
                        modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                    )
                    MbdiButton(
                        "退出", { st.pendingExit = false; resetToHome(st) },
                        modifier = Modifier.weight(1f), style = BtnStyle.Danger
                    )
                }
            }
        }
    }
}

/**
 * 选项行（方案 5.3「锁定动画」）：
 * 热区 ≥56dp；选中 = 底色 + **描边从两侧合拢成闭合框** + 左侧色条亮起 + 轻触感；
 * 自动跳转等待期（[recording]）右下角有一枚微光脉动的「记录光点」。
 */
@Composable
private fun OptionRow(
    label: String,
    text: String,
    selected: Boolean,
    accent: String,
    recording: Boolean = false,
    onClick: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val c = colorFromHex(accent)
    val shape = RoundedCornerShape(MbdiShape.small)
    val haptics = LocalHapticFeedback.current
    val animOn = rememberAnimationsOn()

    // 锁定描边：选中时从两端合拢（Snappy），取消时快速退场
    val lock = remember { Animatable(if (selected) 1f else 0f) }
    val lockState = remember(lock) { lock.asState() }
    LaunchedEffect(selected) {
        if (!animOn) {
            lock.snapTo(if (selected) 1f else 0f)
            return@LaunchedEffect
        }
        if (selected) {
            lock.snapTo(0f)
            lock.animateTo(1f, MbdiSpring.Snappy)
        } else {
            lock.animateTo(0f, MbdiSpring.Snappy)
        }
    }
    // 记录光点脉动
    val pulseState = rememberInfiniteTransition(label = "recPulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(620, easing = LinearEasing), RepeatMode.Reverse),
        label = "recA"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(shape)
            .background(if (selected) c.copy(alpha = 0.12f) else colorFromHex(t.bg))
            .border(1.dp, colorFromHex(t.brd), shape)
            .lockFrame(c, lockState)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = MbdiSpace.m, vertical = MbdiSpace.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(t.gold),
                modifier = Modifier
                    .border(1.dp, colorFromHex(t.goldDim), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            Spacer(Modifier.width(MbdiSpace.s))
            Text(
                text,
                fontSize = MbdiType.Body, lineHeight = 21.sp,
                color = colorFromHex(if (selected) t.text else t.text2)
            )
        }
        if (recording) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 7.dp)
                    .size(7.dp)
                    .alpha(pulseState.value)
            ) {
                Box(Modifier.matchParentSize().clip(CircleShape).background(c.copy(alpha = 0.9f)))
            }
        }
    }
}

/**
 * 锁定框（画在内容之上）：四条边从两端向中间合拢成闭合框，最后点亮左侧色条（方案 5.3）。
 * [lock] 用 State 传入，绘制期读值 —— 合拢动画不触发重组。
 */
private fun Modifier.lockFrame(color: Color, lock: State<Float>): Modifier = this.drawWithContent {
    drawContent()
    val p = lock.value.coerceIn(0f, 1f)
    if (p <= 0.01f) return@drawWithContent
    val w = size.width
    val h = size.height
    val i = 0.8.dp.toPx()
    val sw = 1.6.dp.toPx()
    val hs = (w - i * 2f) / 2f * p
    val vs = (h - i * 2f) / 2f * p
    drawLine(color, Offset(i, i), Offset(i + hs, i), sw, StrokeCap.Round)
    drawLine(color, Offset(w - i, i), Offset(w - i - hs, i), sw, StrokeCap.Round)
    drawLine(color, Offset(i, h - i), Offset(i + hs, h - i), sw, StrokeCap.Round)
    drawLine(color, Offset(w - i, h - i), Offset(w - i - hs, h - i), sw, StrokeCap.Round)
    drawLine(color, Offset(i, i), Offset(i, i + vs), sw, StrokeCap.Round)
    drawLine(color, Offset(i, h - i), Offset(i, h - i - vs), sw, StrokeCap.Round)
    drawLine(color, Offset(w - i, i), Offset(w - i, i + vs), sw, StrokeCap.Round)
    drawLine(color, Offset(w - i, h - i), Offset(w - i, h - i - vs), sw, StrokeCap.Round)
    if (p > 0.7f) {
        val a = ((p - 0.7f) / 0.3f).coerceIn(0f, 1f)
        drawRoundRect(
            color = color.copy(alpha = 0.85f * a),
            topLeft = Offset(0f, i * 2f),
            size = Size(3.dp.toPx(), h - i * 4f),
            cornerRadius = CornerRadius(1.5.dp.toPx())
        )
    }
}

/**
 * 答题「能量条」（方案 5.3）：细轨 + 渐变能量 + 尾端光点（scan 色）+ 流动高光；
 * [resumed] = 进入本页时已经答过题（切 Tab / 后台回来）→ 播放一次「续接」闪光。
 */
@Composable
private fun EnergyBar(progress: Float, resumed: Boolean, modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val animOn = rememberAnimationsOn()
    val gold = colorFromHex(t.gold)
    val purple = colorFromHex(t.purple)
    val trackC = colorFromHex(t.brd)
    val scanC = t.scan
    val rimC = t.rim

    // 流动高光相位（绘制期读值，不产生每帧重组）
    val flowState = rememberInfiniteTransition(label = "energy").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "flow"
    )
    // 「续接」闪光：一次白色扫亮
    val flash = remember { Animatable(0f) }
    LaunchedEffect(resumed) {
        if (resumed && animOn) {
            flash.snapTo(1f)
            flash.animateTo(0f, tween(880, easing = FastOutSlowInEasing))
        }
    }

    Canvas(modifier.height(10.dp)) {
        val h = 7.dp.toPx()
        val cy = size.height / 2f
        val r = CornerRadius(h / 2f)
        val w = size.width
        drawRoundRect(trackC, topLeft = Offset(0f, cy - h / 2f), size = Size(w, h), cornerRadius = r)
        val fw = (w * progress.coerceIn(0f, 1f))
        if (fw <= 0.5f) return@Canvas
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(gold, purple), startX = 0f, endX = w),
            topLeft = Offset(0f, cy - h / 2f), size = Size(fw, h), cornerRadius = r
        )
        if (!quiet) {
            val x = fw * ((flowState.value * 1.4f) % 1f)
            val span = 26.dp.toPx()
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.42f), Color.Transparent),
                    startX = x - span, endX = x + span
                ),
                topLeft = Offset(0f, cy - h / 2f), size = Size(fw, h), cornerRadius = r
            )
        }
        // 尾端光点
        drawCircle(scanC.copy(alpha = 0.35f), radius = 5.dp.toPx(), center = Offset(fw, cy))
        drawCircle(rimC, radius = 2.2.dp.toPx(), center = Offset(fw, cy))
        // 续接闪光
        if (flash.value > 0.02f) {
            drawRoundRect(
                Color.White.copy(alpha = 0.35f * flash.value),
                topLeft = Offset(0f, cy - h / 2f), size = Size(fw, h), cornerRadius = r
            )
        }
    }
}

// ===== 流程逻辑（对齐 HTML startTest/selectAlphaOpt/prevQ/nextQ/showResult/resetTest） =====

private fun startTest(st: TestState, tier: Int) {
    st.tier = tier
    AppStorage.data.lastTier = tier      // 档位卡用它标「上次选的是这个」
    AppStorage.save()
    st.currentQ = 0
    st.playerStats = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    st.selectedAnswers.clear()
    st.mbccResult = null
    st.transitioning = false
    st.stage = TestStage.QUESTION
}

fun resetToHome(st: TestState) {
    st.stage = TestStage.HOME
    st.revealDone = false
    st.currentQ = 0
    st.playerStats = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    st.selectedAnswers.clear()
    st.mbccResult = null
    st.resAnswers.clear()
    st.resScores.clear()
    st.resResult = null
}

private fun selectOption(st: TestState, optStats: List<Double>, _label: String) {
    // 减掉旧答案
    st.selectedAnswers[st.currentQ]?.let { prev ->
        st.playerStats = List(7) { i -> st.playerStats[i] - prev[i] }
    }
    st.selectedAnswers[st.currentQ] = optStats
    st.playerStats = List(7) { i -> st.playerStats[i] + optStats[i] }
}

/** 结算 MBCC 结果（对齐 HTML showResult L1659-1686） */
fun showMbccResult(st: TestState) {
    val tier = st.tier
    // ★ 七维水平测量制：某维得分 = 该维在所选选项上的**平均值**（0–10）。
    //   字典里每题每维的 4 个选项和恒为 20，所以"随机作答"每维正好 5 分，七个维度基准齐平。
    val answered = st.selectedAnswers.size.coerceAtLeast(1)
    val pScaled = List(7) { i -> st.playerStats[i] / answered }
    // ★ v1.5.2 收集补全：娱乐档（12 / 20 题，tier 1/2）把「本地已解锁角色」传进匹配；
    //   严格档（30 / 40 / 50 题，tier 3/4/5）保持纯人格匹配，不掺任何收集因素。
    val ownedIds = AppStorage.data.unlockedChars.toSet()
    val ranked = if (tier >= 3 || ownedIds.isEmpty()) {
        DcpmEngine.scores(pScaled, tier)
    } else {
        DcpmEngine.scores(
            pScaled, tier,
            owned = ownedIds,
            // 快速档（16 题）玩票性质，补全更狠；深度档温和一些
            ownedPenalty = if (tier == 1) 0.35 else 0.55,
            freshBonus = if (tier == 1) 1.20 else 1.10
        )
    }
    val topProb = ranked[0].prob
    val resId = ranked[0].id
    val secondId = ranked.getOrNull(1)?.id
    val thirdId = ranked.getOrNull(2)?.id
    val char = CHARACTERS.firstOrNull { it.id == resId } ?: CHARACTERS[0]
    val char2 = secondId?.let { CHARACTERS.firstOrNull { c -> c.id == it } }
    val char3 = thirdId?.let { CHARACTERS.firstOrNull { c -> c.id == it } }
    // 阵营判定（吃原始累加值，对齐 HTML matchFaction(playerStats)）
    val (fid, _) = DcpmEngine.matchFaction(st.playerStats)
    // 解锁角色 + 计数 + 记录（对齐 L1683-1686；原版 MBCC 不发放成就）
    if (!AppStorage.data.unlockedChars.contains(resId)) {
        AppStorage.data.unlockedChars.add(resId)
    }
    AppStorage.data.testCount = (AppStorage.data.testCount ?: 0) + 1
    // ★ v1.5.0「局长档案」：完成任意一档鉴定后，把玩家自己的七维画像独立存下来
    //   （最新一次覆盖；图鉴 → 局长详情页展示，独立于内置角色数据，不参与任何匹配）
    val cal = java.util.Calendar.getInstance()
    AppStorage.data.juzhangStats = pScaled.toMutableList()
    AppStorage.data.juzhangTier = tier
    AppStorage.data.juzhangDate = "%04d.%02d.%02d".format(
        cal.get(java.util.Calendar.YEAR),
        cal.get(java.util.Calendar.MONTH) + 1,
        cal.get(java.util.Calendar.DAY_OF_MONTH)
    )
    AppStorage.save()
    AppStorage.saveRecord(
        "人格鉴定[Lv.$tier]", "${char.name} · ${char.code}", char.desc, char.id, tier
    )
    st.mbccResult = MbccResult(ranked, char, char2, char3, pScaled, fid, topProb, tier)
    st.revealDone = false      // 每次都重播揭晓动画
    st.stage = TestStage.RESULT
}
