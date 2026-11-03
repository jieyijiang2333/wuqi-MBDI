package com.wqmtmbdi.mbcc.ui.test

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.CompareRadar
import com.wqmtmbdi.mbcc.MbdiSurfaceKind
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.isLight
import com.wqmtmbdi.mbcc.data.GameChar
import com.wqmtmbdi.mbcc.data.STATS_LABELS7
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.mbdiSurface
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.DimCompareBar
import com.wqmtmbdi.mbcc.ui.components.LegendDot
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.render.erraticField
import com.wqmtmbdi.mbcc.ui.resonance.AvatarCircle
import com.wqmtmbdi.mbcc.ui.theme.MbdiMotion
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * ★ MBCC 人格鉴定结果页（2026-09-28 重做；2026-10-01 按《人格鉴定-界面深度设计方案.md》5.4 深度升级）
 *
 * 本轮（第 1 批）做了什么：
 *  - 揭晓仪式 2.0：静默 → 洗档 → 锁定 → 契合 四幕（可跳过；安静模式直接出结果）；
 *  - 主卡升级铭牌材质（Plaque + 渐变描边 + ~3s 一轮扫光），匹配度环加 10 格刻度、数字等宽滚动；
 *  - 雷达图 2.0：纱质感填充（渐变 + 噪点）、差异最大的轴画「光桥 + 差值」、选中轴流光点亮、中央浮出数值卡；
 *  - Top3 卡：mini 雷达缩略 + 匹配度光环 + 横向吸附；
 *  - 新增「保存结果图 / 分享」（离屏 Canvas 绘制 → 相册 → 系统分享面板）。
 */
@Composable
fun MbccResultView(
    r: MbccResult,
    revealDone: Boolean,
    onRestart: () -> Unit,
    onBackHome: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val char = r.char
    val animOn = rememberAnimationsOn()
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<Int?>(null) }
    var replayKey by remember { mutableIntStateOf(0) }

    // ---- 保存结果图 / 分享（2026-10-01 用户拍板：随第 1 批做）----
    var busy by remember { mutableStateOf(false) }
    var savedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    fun export(share: Boolean) {
        if (busy) return
        busy = true
        scope.launch {
            val playerName = AppStorage.data.customProfile?.name?.takeIf { it.isNotBlank() }
            val uri = withContext(Dispatchers.IO) {
                savedUri ?: run {
                    val bmp = ResultShareCard.render(r, t, playerName)
                    val u = ResultShareCard.save(context, bmp, ResultShareCard.fileName(char.name))
                    bmp.recycle()
                    u
                }
            }
            if (uri != null) savedUri = uri
            busy = false
            when {
                uri == null -> com.wqmtmbdi.mbcc.ui.components.TipBus.show("保存失败，请重试")
                share -> ResultShareCard.share(context, uri)
                else -> com.wqmtmbdi.mbcc.ui.components.TipBus.show("已保存到相册（Pictures/无期人格鉴定）")
            }
        }
    }

    // ★ 用户要求：已删除「重新测试」落定微脉冲（原 pulse / pulsed 动画块）

    Column(
        // 结果页没有底部导航，这里要自己避让手势条 / 导航栏
        Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = MbdiSpace.m),
        verticalArrangement = Arrangement.spacedBy(MbdiSpace.m)
    ) {
        ArchiveCard(r)

        // ---- 七维对比 ----
        if (char.id != "juzhang") {
            val diffs = List(7) { i -> r.pScaled[i] - char.stats.getOrElse(i) { 0 }.toDouble() }
            val closest = diffs.indices.minByOrNull { abs(diffs[it]) } ?: 0
            val farthest = diffs.indices.maxByOrNull { abs(diffs[it]) } ?: 0
            SectionCard(
                title = "七维画像对比",
                subtitle = "点雷达图或下面的维度，看这一维的具体差距"
            ) {
                // 图例
                Row(horizontalArrangement = Arrangement.spacedBy(MbdiSpace.l)) {
                    LegendDot(colorFromHex(char.avatarColor), char.name)
                    LegendDot(colorFromHex(t.gold), "你", dashed = true)
                }
                // 选中数值浮卡：真值在退出动画期间也要有内容，用 shownAxis 兜底
                var shownAxis by remember { mutableIntStateOf(0) }
                selected?.let { shownAxis = it }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CompareRadar(
                        playerStats = r.pScaled,
                        charStats = char.stats,
                        color = char.avatarColor,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(280.dp)
                            .padding(top = MbdiSpace.s),
                        selectedAxis = selected,
                        onAxisChanged = { sel ->
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selected = sel
                        },
                        replayKey = replayKey
                    )
                    // 选中数值浮卡：Soft 弹簧浮出、退出更快（方案 3.2「来隆重、走干脆」）
                    val cardAlpha by animateFloatAsState(
                        targetValue = if (selected != null) 1f else 0f,
                        animationSpec = tween(if (selected != null) 160 else 120),
                        label = "axisCardAlpha"
                    )
                    val cardScale by animateFloatAsState(
                        targetValue = if (selected != null) 1f else 0.92f,
                        animationSpec = MbdiSpring.Soft,
                        label = "axisCardScale"
                    )
                    if (cardAlpha > 0.01f) {
                        val pv = r.pScaled.getOrElse(shownAxis) { 0.0 }
                        val cv = char.stats.getOrElse(shownAxis) { 0 }.toDouble()
                        Row(
                            Modifier
                                .align(Alignment.Center)
                                .graphicsLayer(alpha = cardAlpha, scaleX = cardScale, scaleY = cardScale)
                                // 点卡片收起（对应方案「点空白还原」，同时避免必须再点同一根轴）
                                .clickable { selected = null }
                                .mbdiSolid(RoundedCornerShape(MbdiShape.small))
                                .padding(horizontal = MbdiSpace.m, vertical = MbdiSpace.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                STATS_LABELS7[shownAxis],
                                fontSize = MbdiType.Label,
                                fontWeight = FontWeight.Bold,
                                color = colorFromHex(t.gold)
                            )
                            // ★ 用户要求：删掉「差 X.X」数字（只保留你 / TA 的各自数值）
                            Text(
                                "　你 ${"%.1f".format(pv)} ｜ ${char.name} ${"%.1f".format(cv)}",
                                style = TextStyle(
                                    fontSize = MbdiType.Label,
                                    fontWeight = FontWeight.Bold,
                                    color = colorFromHex(t.text2),
                                    fontFeatureSettings = "tnum"
                                )
                            )
                        }
                    }
                }
                // 结论（★ 用户要求：删掉「差 X.X」数字）
                Text(
                    "你和 ${char.name} 最像的是「${STATS_LABELS7[closest]}」，" +
                        "差最多的是「${STATS_LABELS7[farthest]}」（你 ${"%.1f".format(r.pScaled.getOrElse(farthest) { 0.0 })} / " +
                        "TA ${"%.1f".format(char.stats.getOrElse(farthest) { 0 }.toDouble())}）",
                    fontSize = MbdiType.Label,
                    lineHeight = 20.sp,
                    color = colorFromHex(t.text2),
                    modifier = Modifier.padding(top = MbdiSpace.s)
                )
                Spacer(Modifier.height(MbdiSpace.s))
                // 七维明细（点一行 = 高亮雷达对应轴）
                STATS_LABELS7.forEachIndexed { i, lb ->
                    DimCompareBar(
                        label = lb,
                        playerValue = r.pScaled.getOrElse(i) { 0.0 },
                        charValue = char.stats.getOrElse(i) { 0 }.toDouble(),
                        charColor = colorFromHex(char.avatarColor),
                        selected = selected == i,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selected = if (selected == i) null else i
                        }
                    )
                }
                Spacer(Modifier.height(MbdiSpace.s))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MbdiButton(
                        "重播动画",
                        onClick = { replayKey++ },
                        style = BtnStyle.Text
                    )
                }
            }
        }

        // ---- 其他共鸣档案（横向吸附滑动）----
        val subs = listOfNotNull(r.char2, r.char3)
        if (subs.isNotEmpty()) {
            SectionCard(title = "其他共鸣档案", subtitle = "与你相似度次高的禁闭者 · 左右滑动查看") {
                val listState = rememberLazyListState()
                LazyRow(
                    Modifier.fillMaxWidth(),
                    state = listState,
                    flingBehavior = rememberSnapFlingBehavior(listState),
                    horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    itemsIndexed(subs) { i, sc ->
                        val pct = ((r.ranked.getOrNull(i + 1)?.sim ?: 0.0) * 100).roundToInt()
                        OtherCharCard(sc, pct)
                    }
                }
            }
        }

        // ---- 导出（保存结果图 / 分享）----
        Row(
            Modifier.fillMaxWidth().padding(top = MbdiSpace.xs),
            horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)
        ) {
            MbdiButton(
                "保存结果图", { export(false) },
                modifier = Modifier.weight(1f),
                style = BtnStyle.Secondary,
                enabled = !busy,
                leading = Lucide.Download
            )
            MbdiButton(
                "分享", { export(true) },
                modifier = Modifier.weight(1f),
                style = BtnStyle.Secondary,
                enabled = !busy,
                leading = Lucide.Share2
            )
        }

        // ---- 参考说明（用户要求：说明角色数值相近、本软件为娱乐性项目）----
        Text(
            "鉴定结果仅供参考：角色数值存在相近区间，建议隔一段时间多测几次，取反复出现的角色更可靠。",
            fontSize = MbdiType.Caption, lineHeight = 17.sp, color = colorFromHex(t.text3),
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )

        // ---- 底部操作 ----
        // 结果页把底部导航藏起来了，所以这里必须给出「回去」的路
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)
        ) {
            MbdiButton(
                "返回主页面", onBackHome,
                modifier = Modifier.weight(1f),
                style = BtnStyle.Secondary
            )
            // ★ 用户要求：已删除「重新测试」按钮的落定泛光脉冲
            MbdiButton(
                "重新测试", onRestart,
                modifier = Modifier.weight(1f),
                style = BtnStyle.Primary
            )
        }
    }
}

// ===================== 档案卡（铭牌材质） =====================

@Composable
private fun ArchiveCard(r: MbccResult) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val char = r.char
    val cp = AppStorage.data.customProfile
    val pName = cp?.name?.takeIf { it.isNotBlank() }
    val pSig = cp?.sig?.takeIf { it.isNotBlank() }
    val gold = colorFromHex(t.gold)
    val purple = colorFromHex(t.purple)

    // ★ 第 8 批：删掉了铭牌上的循环扫光（用户要求全局扫光效果一律去掉）
    val shape = RoundedCornerShape(MbdiShape.card)
    Column(
        Modifier
            .fillMaxWidth()
            .mbdiSurface(MbdiSurfaceKind.Plaque, shape)
            // 渐变描边（金 → 灰紫），压在 Panel 的常规描边之上
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(gold.copy(alpha = 0.45f), purple.copy(alpha = 0.30f), gold.copy(alpha = 0.18f))
                ),
                shape
            )
            .padding(MbdiSpace.l)
    ) {
        // 玩家信息缩到顶部一行（不再占主视觉）
        if (pName != null || pSig != null || cp?.avatar != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                cp?.avatar?.let { AvatarCircle(key = it, size = 30.dp, color = t.goldDim) }
                Column(Modifier.padding(start = if (cp?.avatar != null) MbdiSpace.s else 0.dp)) {
                    if (pName != null) {
                        Text(pName, fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
                    }
                    if (pSig != null) {
                        Text(pSig, fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
                    }
                }
            }
            Spacer(Modifier.height(MbdiSpace.m))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "鉴定结果 · ${getTierLabel(r.tier)}",
                fontSize = MbdiType.Label,
                color = colorFromHex(t.text3),
                letterSpacing = 1.sp
            )
            // ★ v1.5.2：娱乐档（tier 1/2）明确标出「含收集补全」，严格档（tier 3+）不标
            if (r.tier < 3) {
                Spacer(Modifier.width(8.dp))
                MbdiChip("含收集补全", colorFromHex(t.purple), filled = true)
            }
        }
        Spacer(Modifier.height(MbdiSpace.m))

        Row(verticalAlignment = Alignment.CenterVertically) {
            MatchRing(
                prob = r.topProb.toFloat(),
                color = gold,
                size = 104.dp,
                strokeWidth = 8.dp
            ) { prog ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(r.topProb * prog * 100).roundToInt()}",
                        style = TextStyle(
                            fontSize = MbdiType.Number,
                            fontWeight = FontWeight.Black,
                            color = gold,
                            fontFeatureSettings = "tnum"
                        )
                    )
                    Text("匹配度", fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
                }
            }
            Column(Modifier.weight(1f).padding(start = MbdiSpace.l)) {
                Text(
                    char.name,
                    fontSize = MbdiType.Display,
                    fontWeight = FontWeight.Bold,
                    color = gold,
                    letterSpacing = 1.sp
                )
                r.faction?.let { fid ->
                    com.wqmtmbdi.mbcc.data.FACTIONS_INFO[fid]?.let { fi ->
                        Box(Modifier.padding(top = MbdiSpace.xs)) {
                            MbdiChip("狂厄性质 · ${fi.name}", colorFromHex(t.purple), filled = true)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(MbdiSpace.m))
        // 四字代码（钢印质感：纵向微渐变 + 金描边 + 顶缘高光）
        Row(horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
            char.meanings.forEach { m ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(MbdiShape.small))
                            .background(
                                Brush.verticalGradient(listOf(gold.copy(alpha = 0.13f), gold.copy(alpha = 0.04f)))
                            )
                            .border(1.dp, gold.copy(alpha = 0.35f), RoundedCornerShape(MbdiShape.small))
                            .stampRim()
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            m.l,
                            fontSize = MbdiType.Code,
                            fontWeight = FontWeight.Black,
                            color = gold,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        m.m,
                        fontSize = MbdiType.Caption,
                        color = colorFromHex(t.text2),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(MbdiSpace.m))
        Text(
            char.desc,
            fontSize = MbdiType.Body,
            lineHeight = 24.sp,
            color = colorFromHex(t.text2)
        )
    }
}

/** 钢印顶缘高光（1px，两端内收） */
private fun Modifier.stampRim(): Modifier = this.drawWithContent {
    drawContent()
    val inset = 6.dp.toPx()
    if (size.width <= inset * 2f) return@drawWithContent
    val light = AppStorage.currentMBDITheme().isLight()
    drawLine(
        if (light) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.55f),
        start = Offset(inset, 0.5.dp.toPx()),
        end = Offset(size.width - inset, 0.5.dp.toPx()),
        strokeWidth = 1.dp.toPx()
    )
}

/**
 * 匹配度环形进度（0..1）
 *
 * @param track    环底色（默认主题描边色）
 * @param animate  false 表示外部逐帧给值（揭晓幕四用），不再自播动画
 * @param center   中心内容，参数为当前进度（数字可做等宽滚动）
 */
@Composable
private fun MatchRing(
    prob: Float,
    color: Color,
    size: Dp,
    strokeWidth: Dp = 7.dp,
    track: Color? = null,
    tickColor: Color? = null,
    animate: Boolean = true,
    center: @Composable (Float) -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val animOn = rememberAnimationsOn()
    val trackC = track ?: colorFromHex(t.brd)
    val tickC = tickColor ?: colorFromHex(t.text3).copy(alpha = 0.30f)
    val p = remember { Animatable(if (animOn && animate) 0f else prob) }
    LaunchedEffect(prob, animOn, animate) {
        if (!animOn || !animate) {
            p.snapTo(prob)
            return@LaunchedEffect
        }
        p.snapTo(0f)
        p.animateTo(prob.coerceIn(0f, 1f), tween(900, easing = FastOutSlowInEasing))
    }
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val inner = Size(this.size.width - sw, this.size.height - sw)
            val tl = Offset(sw / 2f, sw / 2f)
            // 环底色上的 10 格刻度点（仪器感）；被进度弧覆盖的部分自然隐去
            val cc = Offset(this.size.width / 2f, this.size.height / 2f)
            val ringR = (this.size.minDimension - sw) / 2f
            for (i in 0 until 10) {
                val a = -PI.toFloat() / 2f + i * 2f * PI.toFloat() / 10f
                drawCircle(
                    tickC,
                    radius = 1.5.dp.toPx(),
                    center = Offset(cc.x + ringR * cos(a), cc.y + ringR * sin(a))
                )
            }
            drawArc(
                color = trackC,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                style = Stroke(sw), topLeft = tl, size = inner
            )
            drawArc(
                color = color,
                startAngle = -90f, sweepAngle = 360f * p.value.coerceIn(0f, 1f), useCenter = false,
                style = Stroke(sw, cap = StrokeCap.Round),
                topLeft = tl, size = inner
            )
        }
        center(p.value)
    }
}

/** 其他共鸣档案卡片（mini 雷达缩略 + 匹配度光环） */
@Composable
private fun OtherCharCard(c: GameChar, pct: Int) {
    val t = AppStorage.currentMBDITheme()
    // ★ v1.5.0：角色标签由「狂厄性质阵营」改为「角色等级」（狂厄级 / 高危级 / 普管级 / 特殊）
    val rank = com.wqmtmbdi.mbcc.data.rankOf(c.id)
    val cc = colorFromHex(c.avatarColor)
    Column(
        Modifier
            .width(148.dp)
            .mbdiSurface(MbdiSurfaceKind.Panel, RoundedCornerShape(MbdiShape.card))
            .padding(MbdiSpace.m),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像 + 匹配度光环
        Box(Modifier.size(66.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val sw = 2.dp.toPx()
                val d = this.size.minDimension - sw
                val tl = Offset((this.size.width - d) / 2f, (this.size.height - d) / 2f)
                drawArc(
                    color = colorFromHex(t.brd), startAngle = 0f, sweepAngle = 360f,
                    useCenter = false, style = Stroke(sw), topLeft = tl, size = Size(d, d)
                )
                drawArc(
                    color = cc, startAngle = -90f, sweepAngle = 360f * pct / 100f,
                    useCenter = false, style = Stroke(sw, cap = StrokeCap.Round), topLeft = tl, size = Size(d, d)
                )
            }
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(50)).background(cc),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    c.code.first().toString(),
                    fontSize = MbdiType.Body,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
        Text(
            c.name,
            fontSize = MbdiType.Label,
            fontWeight = FontWeight.Bold,
            color = colorFromHex(t.text),
            modifier = Modifier.padding(top = MbdiSpace.xs)
        )
        Text(
            "${rank.ifEmpty { "禁闭者" }} · $pct%",
            fontSize = MbdiType.Caption,
            color = colorFromHex(t.text3),
            textAlign = TextAlign.Center
        )
        // mini 雷达缩略
        MiniRadarThumb(c.stats, cc, Modifier.size(84.dp).padding(top = MbdiSpace.s))
        Text(
            c.code,
            style = TextStyle(
                fontSize = MbdiType.Label,
                fontWeight = FontWeight.Black,
                color = colorFromHex(t.purple),
                letterSpacing = 1.sp
            )
        )
    }
}

/** 七维 mini 雷达缩略（无标签，只做卡面识别） */
@Composable
private fun MiniRadarThumb(stats: List<Number>, color: Color, modifier: Modifier) {
    val t = AppStorage.currentMBDITheme()
    Canvas(modifier) {
        val n = 7
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val R = min(cx, cy) - 2.dp.toPx()
        val angles = FloatArray(n) { -PI.toFloat() / 2f + it * 2f * PI.toFloat() / n }
        // 网格：两圈
        for (ring in 1..2) {
            val rr = R * ring / 2f
            val path = Path()
            for (i in 0 until n) {
                val x = cx + rr * cos(angles[i])
                val y = cy + rr * sin(angles[i])
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, colorFromHex(t.brd).copy(alpha = 0.8f), style = Stroke(0.7.dp.toPx()))
        }
        // 数据多边形
        val path = Path()
        for (i in 0 until n) {
            val v = (stats.getOrElse(i) { 0 }.toDouble() / 10.0).coerceIn(0.0, 1.0).toFloat()
            val x = cx + R * v * cos(angles[i])
            val y = cy + R * v * sin(angles[i])
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color.copy(alpha = 0.22f))
        drawPath(path, color, style = Stroke(1.6.dp.toPx()))
    }
}

// ===================== 揭晓仪式 2.0（四幕） =====================

/**
 * 结果揭晓动画（方案 5.4 ①，总 [MbdiMotion.REVEAL_TOTAL] 毫秒，任意位置点击可跳过）。
 *
 * 四幕：一·静默（扫描环 + 粒子向中心汇聚）→ 二·洗档（20 个代号沿弧形轨道划过，带残影）
 * → 三·锁定（代号从模糊到清晰 + 一次光爆 + 重触感）→ 四·契合（匹配度环 + 七维雷达同幕生长）。
 * API 33+ 时叠加 AGSL「狂厄场」；安静模式（系统关动画 / 低端机）直接出结果。
 */
@Composable
fun ResultReveal(r: MbccResult, onDone: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val animOn = rememberAnimationsOn()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val char = r.char
    val gold = colorFromHex(t.gold)
    val ink = colorFromHex(t.bg)   // ★ 第 8 批：幕布改用主题底色（深色主题下才是「暗下」）
    val charC = colorFromHex(char.avatarColor)
    val quick = quiet || !animOn
    // ★ 浅色主题下幕布是浅色，白字/白网格要换成墨色（否则整幕看不见）
    val onScrim = if (t.isLight()) colorFromHex(t.text) else Color.White
    val totalSec = MbdiMotion.REVEAL_TOTAL / 1000f

    val p = remember { Animatable(if (quick) 1f else 0f) }
    val exit = remember { Animatable(1f) }
    var finishing by remember { mutableStateOf(false) }
    val particles = remember {
        val rnd = Random(11)
        List(40) {
            val a = it * (2f * PI.toFloat() / 40f) + rnd.nextFloat() * 0.35f
            Triple(cos(a), sin(a), 0.45f + rnd.nextFloat() * 0.55f)
        }
    }
    val codeLayouts = remember(char) {
        CHARACTERS.map { c ->
            measurer.measure(
                c.code,
                TextStyle(fontSize = MbdiType.Label, fontWeight = FontWeight.Black, color = gold.copy(alpha = 0.55f))
            )
        }
    }

    LaunchedEffect(Unit) {
        if (quick) {
            onDone()
            return@LaunchedEffect
        }
        p.animateTo(1f, tween(MbdiMotion.REVEAL_TOTAL, easing = LinearEasing))
        if (!finishing) {
            finishing = true
            exit.animateTo(0f, tween(240))
            onDone()
        }
    }
    // 第三幕「锁定」：一次重触感（跳过时不补发）
    LaunchedEffect(Unit) {
        if (quick) return@LaunchedEffect
        snapshotFlow { p.value }.first { it >= 2.4f / totalSec }
        if (!finishing) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val s = if (quick) totalSec else p.value * totalSec
    val lockK = ((s - 2.4f) / 0.8f).coerceIn(0f, 1f)
    val joinK = ((s - 3.2f) / 1.3f).coerceIn(0f, 1f)
    val radarK = ((s - 3.4f) / 1.1f).coerceIn(0f, 1f)

    val onSkip: () -> Unit = {
        if (!finishing) {
            finishing = true
            scope.launch {
                exit.animateTo(0f, tween(220))
                onDone()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .alpha(exit.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkip
            )
    ) {
        // —— 幕布 + 画布演出（粒子 / 弧轨代号 / 光爆）——
        Canvas(Modifier.fillMaxSize()) {
            val scrimIn = (s / 0.45f).coerceIn(0f, 1f)
            drawRect(ink.copy(alpha = 0.965f * scrimIn))
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val base = min(w, h)

            // 幕一 · 静默：粒子向中心汇聚
            if (s < 1.25f) {
                val k = (s / 1.05f).coerceIn(0f, 1f)
                val ease = k * k
                particles.forEach { (dx, dy, rr) ->
                    val r0 = base * 0.52f * rr
                    drawCircle(
                        gold.copy(alpha = 0.5f * (1f - k * 0.75f) * scrimIn),
                        radius = 1.6.dp.toPx(),
                        center = Offset(cx + dx * r0 * (1f - ease), cy + dy * r0 * (1f - ease))
                    )
                }
            }
            // 幕二 · 洗档：代号沿弧形轨道划过（三重残影）
            if (s > 0.9f && s < 2.55f) {
                val k = ((s - 0.9f) / 1.65f).coerceIn(0f, 1f)
                val fade = (k / 0.10f).coerceIn(0f, 1f) * (((1f - k) / 0.14f).coerceIn(0f, 1f))
                val spin = (k * k * 6.5f + k * 1.5f) * PI.toFloat()
                val orbitR = base * 0.30f
                for (i in CHARACTERS.indices) {
                    val ang = spin + i * 2f * PI.toFloat() / CHARACTERS.size
                    for (tr in 0..2) {
                        val a = ang - tr * 0.16f
                        val alpha = fade * if (tr == 0) 0.75f else 0.30f / tr
                        val lr = codeLayouts[i]
                        drawText(
                            lr,
                            topLeft = Offset(
                                cx + cos(a) * orbitR - lr.size.width / 2f,
                                cy + sin(a) * orbitR - lr.size.height / 2f
                            ),
                            alpha = alpha
                        )
                    }
                }
            }
            // 幕三 · 锁定：一次光爆（径向光晕 + 两圈扩散波）
            if (s > 2.35f) {
                val b = ((s - 2.4f) / 1.25f).coerceIn(0f, 1f)
                if (b < 1f) {
                    val burstR = base * (0.18f + 0.55f * b)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(gold.copy(alpha = 0.55f * (1f - b)), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = burstR
                        ),
                        radius = burstR,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        gold.copy(alpha = 0.65f * (1f - b)),
                        radius = base * (0.10f + 0.50f * b),
                        center = Offset(cx, cy),
                        style = Stroke(1.5.dp.toPx())
                    )
                    val b2 = ((s - 2.62f) / 1.25f).coerceIn(0f, 1f)
                    if (b2 > 0f && b2 < 1f) {
                        drawCircle(
                            gold.copy(alpha = 0.40f * (1f - b2)),
                            radius = base * (0.10f + 0.50f * b2),
                            center = Offset(cx, cy),
                            style = Stroke(1.dp.toPx())
                        )
                    }
                }
            }
        }

        // —— AGSL 狂厄场（API 33+；低版本与安静模式自动为 0 = 不绘制）——
        Box(
            Modifier.fillMaxSize().erraticField(
                timeSec = s,
                intensity = if (quick) 0f
                else ((s - 0.15f) / 0.6f).coerceIn(0f, 1f) * (1f - ((s - 2.4f) / 1.0f).coerceIn(0f, 1f)) * 0.5f
            )
        )

        // —— 幕一 / 二内容：扫描环 + 比对文案 ——
        val searchAlpha = (s / 0.25f).coerceIn(0f, 1f) * (1f - ((s - 2.35f) / 0.25f).coerceIn(0f, 1f))
        if (searchAlpha > 0.01f) {
            Column(
                Modifier.align(Alignment.Center).alpha(searchAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(Modifier.size(132.dp)) {
                    val sw = 3.dp.toPx()
                    val inner = Size(size.width - sw, size.height - sw)
                    val tl = Offset(sw / 2f, sw / 2f)
                    drawArc(
                        gold.copy(alpha = 0.18f), 0f, 360f, false,
                        style = Stroke(sw), topLeft = tl, size = inner
                    )
                    val rot = s * 260f
                    drawArc(
                        gold, rot, 96f, false,
                        style = Stroke(sw, cap = StrokeCap.Round), topLeft = tl, size = inner
                    )
                    drawArc(
                        gold.copy(alpha = 0.35f), rot + 150f, 40f, false,
                        style = Stroke(sw * 0.7f, cap = StrokeCap.Round), topLeft = tl, size = inner
                    )
                }
                Spacer(Modifier.height(MbdiSpace.l))
                Text(
                    if (s < 1.0f) "正在比对 ${CHARACTERS.size} 份档案…" else "正在锁定共鸣对象…",
                    fontSize = MbdiType.Body,
                    color = onScrim.copy(alpha = 0.86f)
                )
            }
        }

        // —— 幕三 / 四内容：锁定代号 + 契合（环 + 雷达）——
        val foundAlpha = ((s - 2.45f) / 0.45f).coerceIn(0f, 1f)
        if (foundAlpha > 0.01f) {
            Column(
                Modifier.align(Alignment.Center).alpha(foundAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val lockScale = when {
                    lockK <= 0f -> 0.86f
                    lockK < 0.6f -> 0.86f + (1.14f - 0.86f) * (lockK / 0.6f)
                    else -> 1.14f - 0.14f * ((lockK - 0.6f) / 0.4f)
                }
                val blurDp = if (Build.VERSION.SDK_INT >= 31 && !quick) (1f - lockK) * 9f else 0f
                Text(
                    char.code,
                    style = TextStyle(
                        fontSize = MbdiType.Code,
                        fontWeight = FontWeight.Black,
                        color = gold,
                        letterSpacing = 6.sp
                    ),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = lockScale
                            scaleY = lockScale
                        }
                        .then(
                            if (blurDp > 0.1f) Modifier.blur(blurDp.dp, BlurredEdgeTreatment.Unbounded)
                            else Modifier
                        )
                )
                Spacer(Modifier.height(MbdiSpace.s))
                Text(
                    char.name,
                    style = TextStyle(
                        fontSize = MbdiType.Display,
                        fontWeight = FontWeight.Bold,
                        color = onScrim,
                        letterSpacing = 1.sp
                    )
                )
                if (joinK > 0.01f) {
                    Spacer(Modifier.height(MbdiSpace.l))
                    Box(
                        Modifier.fillMaxWidth().height(236.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RevealRadar(
                            playerStats = r.pScaled,
                            charStats = char.stats,
                            charColor = charC,
                            growth = radarK,
                            modifier = Modifier.size(228.dp)
                        )
                        MatchRing(
                            prob = r.topProb.toFloat() * joinK,
                            color = gold,
                            size = 80.dp,
                            strokeWidth = 6.dp,
                            track = gold.copy(alpha = 0.20f),
                            tickColor = gold.copy(alpha = 0.40f),
                            animate = false
                        ) { prog ->
                            Text(
                                "${(r.topProb * prog * 100).roundToInt()}",
                                style = TextStyle(
                                    fontSize = MbdiType.Number,
                                    fontWeight = FontWeight.Black,
                                    color = gold,
                                    fontFeatureSettings = "tnum"
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(MbdiSpace.xs))
                    Text(
                        "七维画像已生成",
                        fontSize = MbdiType.Caption,
                        color = onScrim.copy(alpha = 0.55f)
                    )
                }
            }
        }

        Text(
            "点击任意位置跳过",
            fontSize = MbdiType.Caption,
            color = onScrim.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
        )
    }
}

/** 揭晓幕四的七维雷达（无标签，网格 + 角色曲线 + 你的虚线曲线，随 growth 生长） */
@Composable
private fun RevealRadar(
    playerStats: List<Double>,
    charStats: List<Number>,
    charColor: Color,
    growth: Float,
    modifier: Modifier = Modifier
) {
    val gold = colorFromHex(AppStorage.currentMBDITheme().gold)
    val gridC = if (AppStorage.currentMBDITheme().isLight()) Color.Black else Color.White
    Canvas(modifier) {
        val n = 7
        val cx = size.width / 2f
        val cy = size.height / 2f
        val R = min(cx, cy)
        val angles = FloatArray(n) { -PI.toFloat() / 2f + it * 2f * PI.toFloat() / n }
        val gridA = (growth / 0.35f).coerceIn(0f, 1f)
        for (ring in 1..3) {
            val rr = R * ring / 3f
            val path = Path()
            for (i in 0 until n) {
                val x = cx + rr * cos(angles[i])
                val y = cy + rr * sin(angles[i])
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, gridC.copy(alpha = 0.10f * gridA), style = Stroke(0.8.dp.toPx()))
        }
        for (i in 0 until n) {
            drawLine(
                gridC.copy(alpha = 0.08f * gridA),
                start = Offset(cx, cy),
                end = Offset(cx + R * cos(angles[i]), cy + R * sin(angles[i])),
                strokeWidth = 0.7.dp.toPx()
            )
        }
        fun poly(values: List<Float>, g: Float): Path {
            val path = Path()
            for (i in 0 until n) {
                val v = values.getOrElse(i) { 0f } * g
                val x = cx + R * v * cos(angles[i])
                val y = cy + R * v * sin(angles[i])
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return path
        }
        val charV = charStats.map { (it.toDouble() / 10.0).coerceIn(0.0, 1.0).toFloat() }
        val playerV = playerStats.map { (it / 10.0).coerceIn(0.0, 1.0).toFloat() }
        val cg = ((growth - 0.10f) / 0.55f).coerceIn(0f, 1f)
        if (cg > 0.01f) {
            val path = poly(charV, cg)
            drawPath(path, charColor.copy(alpha = 0.20f * cg))
            drawPath(path, charColor.copy(alpha = 0.95f * cg), style = Stroke(2.2.dp.toPx()))
        }
        val pg = ((growth - 0.35f) / 0.55f).coerceIn(0f, 1f)
        if (pg > 0.01f) {
            val path = poly(playerV, pg)
            drawPath(path, gold.copy(alpha = 0.10f * pg))
            drawPath(
                path,
                gold.copy(alpha = 0.95f * pg),
                style = Stroke(
                    width = 2.0.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                )
            )
        }
    }
}

/** 档位标签（v1.5.2 五档：娱乐 12/20，严格 30/40/50） */
fun getTierLabel(tier: Int): String = when (tier) {
    1 -> "娱乐 · 快速"
    2 -> "娱乐 · 标准"
    3 -> "严格 · 标准"
    4 -> "严格 · 加强"
    else -> "严格 · 完整"
}