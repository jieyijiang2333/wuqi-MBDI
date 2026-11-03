package com.wqmtmbdi.mbcc.ui.resonance

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.AvatarStore
import com.wqmtmbdi.mbcc.HexRadar
import com.wqmtmbdi.mbcc.MbdiSurfaceKind
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.RESONANCE_MAX_SCORE
import com.wqmtmbdi.mbcc.data.RESONANCE_TYPES
import com.wqmtmbdi.mbcc.data.RES_DIMS
import com.wqmtmbdi.mbcc.data.RES_DIM_NAMES
import com.wqmtmbdi.mbcc.data.crossoverMsg
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSurface
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.render.drawBeam
import com.wqmtmbdi.mbcc.ui.test.TestStage
import com.wqmtmbdi.mbcc.ui.test.TestState
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch

/** 狂厄共鸣结果数据（对齐 HTML showResonanceResult） */
data class ResonanceResult(
    val typeId: String,
    val dimPct: Map<String, Int>,
    val charCode: String?
)

/** 启动狂厄共鸣测试（对齐 HTML startResonanceTest L2235-2247） */
fun startResonanceTest(st: TestState) {
    st.currentQ = 0
    st.resAnswers.clear()
    st.resScores.clear()
    RES_DIMS.forEach { st.resScores[it] = 0 }
    st.mbccResult = null
    st.resResult = null
    st.transitioning = false
    st.stage = TestStage.QUESTION
}

/** 选择共鸣选项（对齐 HTML selectResonanceOpt L2270-2288；自动跳转由调用方处理） */
fun selectResonanceOpt(st: TestState, scores: Map<String, Int>, _label: String) {
    st.resAnswers[st.currentQ]?.forEach { (k, v) ->
        if (st.resScores[k] != null) st.resScores[k] = (st.resScores[k] ?: 0) - v
    }
    st.resAnswers[st.currentQ] = scores
    scores.forEach { (k, v) ->
        if (st.resScores[k] != null) st.resScores[k] = (st.resScores[k] ?: 0) + v
    }
}

/** 结算狂厄共鸣结果（对齐 HTML 最终覆写版 showResonanceResult L2646-2692） */
fun showResonanceResult(st: TestState) {
    var maxVal = -1
    var resId = RES_DIMS[0]
    RES_DIMS.forEach { d ->
        val v = st.resScores[d] ?: 0
        if (v > maxVal) { maxVal = v; resId = d }
    }
    val tied = RES_DIMS.filter { (st.resScores[it] ?: 0) == maxVal }
    if (tied.size > 1) resId = tied[0]   // 并列取维度顺序首个
    val type = RESONANCE_TYPES[resId]!!
    val dimPct = RES_DIMS.associateWith { ((st.resScores[it] ?: 0).toDouble() / RESONANCE_MAX_SCORE * 100).roundToInt() }
    // 解锁 + 计数 + 成就（对齐 L2654-2667）
    if (!AppStorage.data.unlockedResonance.contains(resId)) AppStorage.data.unlockedResonance.add(resId)
    AppStorage.data.testCount = (AppStorage.data.testCount ?: 0) + 1
    AppStorage.grantAchievement("first_res")
    val resCount = AppStorage.data.unlockedResonance.size
    if (resCount >= 6) AppStorage.grantAchievement("master_res")
    if ((AppStorage.data.testCount ?: 0) >= 3) AppStorage.grantAchievement("scholar")
    val allC = AppStorage.data.unlockedChars.size >= 8
    val allR = resCount >= 6
    if (allC && allR) AppStorage.grantAchievement("omniscient")
    if ((AppStorage.data.testCount ?: 0) >= 10) AppStorage.grantAchievement("test_frenzy")
    AppStorage.save()
    AppStorage.saveRecord("狂厄共鸣", "${type.name} · ${type.code}", type.trait, null, null)
    // 联动：取第一个已解锁角色
    var charCode: String? = null
    val fc = AppStorage.data.unlockedChars.firstOrNull()?.let { id -> CHARACTERS.firstOrNull { c -> c.id == id } }
    if (fc != null) charCode = fc.code
    st.resResult = ResonanceResult(resId, dimPct, charCode)
    st.resRevealed = false   // 重新播一次「频率接收」仪式（方案 5.6）
    st.stage = TestStage.RESULT
}

/**
 * 「频率接收」仪式（方案 5.6）：
 * 结果出现时一圈波纹从中心扩散 + 一次中触感，六维色点依次入位；
 * 点任意处可跳过；安静模式（系统关动画 / 低端机）直接出结果。
 */
@Composable
fun ResonanceReveal(onDone: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val animOn = rememberAnimationsOn()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val quick = quiet || !animOn
    val totalMs = 1700
    val p = remember { Animatable(if (quick) 1f else 0f) }
    val exit = remember { Animatable(1f) }
    var finishing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (quick) { onDone(); return@LaunchedEffect }
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)   // 中触感 = 接收到频率
        p.animateTo(1f, tween(totalMs, easing = FastOutSlowInEasing))
        if (!finishing) {
            finishing = true
            exit.animateTo(0f, tween(220))
            onDone()
        }
    }
    val onSkip: () -> Unit = {
        if (!finishing) {
            finishing = true
            scope.launch {
                exit.animateTo(0f, tween(180))
                onDone()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = exit.value }
            .semantics { contentDescription = "正在接收狂厄频率，点任意处跳过" }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkip
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val s = p.value * (totalMs / 1000f)
            val scrimIn = (s / 0.35f).coerceIn(0f, 1f)
            val gold = colorFromHex(t.gold)
            drawRect(colorFromHex(t.bg).copy(alpha = 0.94f * scrimIn))
            val cx = size.width / 2f
            val cy = size.height / 2f
            val base = min(size.width, size.height)
            // 三圈波纹（错峰扩散）
            repeat(3) { i ->
                val k = ((s - 0.10f - i * 0.26f) / 1.15f).coerceIn(0f, 1f)
                if (k > 0f && k < 1f) {
                    drawCircle(
                        gold.copy(alpha = 0.45f * (1f - k) * scrimIn),
                        radius = base * (0.08f + 0.40f * k),
                        center = Offset(cx, cy),
                        style = Stroke((2.2f - 1.2f * k).dp.toPx())
                    )
                }
            }
            // 中心微光
            val g = (s / 0.8f).coerceIn(0f, 1f)
            val glowR = base * 0.24f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(gold.copy(alpha = 0.28f * g), Color.Transparent),
                    center = Offset(cx, cy), radius = glowR
                ),
                radius = glowR, center = Offset(cx, cy)
            )
            // 六维色点环：颜色与频率图谱一一对应，依次入位
            RES_DIMS.forEachIndexed { i, d ->
                val k = ((s - 0.35f - i * 0.07f) / 0.55f).coerceIn(0f, 1f)
                if (k > 0f) {
                    val a = -PI.toFloat() / 2f + i * 2f * PI.toFloat() / RES_DIMS.size
                    val r = base * 0.20f * k
                    drawCircle(
                        colorFromHex(typeColorOf(d)).copy(alpha = 0.85f * k),
                        radius = 2.8.dp.toPx(),
                        center = Offset(cx + cos(a) * r, cy + sin(a) * r)
                    )
                }
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = ((p.value * (totalMs / 1000f)) / 0.5f).coerceIn(0f, 1f) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Lucide.Radio, contentDescription = null,
                modifier = Modifier.size(32.dp), tint = colorFromHex(t.gold)
            )
            Text(
                "正在接收狂厄频率…",
                fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.text2),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Text(
            "点任意处跳过",
            fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 56.dp)
        )
    }
}

/**
 * 六维频率环（方案 5.6）：六段弧按各维百分比取长，颜色与图谱一一对应；
 * 中心一圈缓慢转动的扫描弧（安静模式不转）。
 */
@Composable
private fun FreqRing(dimPct: Map<String, Int>, sizeDp: Dp, modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val animOn = rememberAnimationsOn()
    val grow = remember { Animatable(if (animOn) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animOn) {
            grow.snapTo(0f)
            grow.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
        }
    }
    val spin = rememberInfiniteTransition(label = "freqRing").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "freqSpin"
    )
    Canvas(modifier.size(sizeDp)) {
        val sw = 5.dp.toPx()
        val d = this.size.minDimension - sw - 2.dp.toPx()
        val tl = Offset((this.size.width - d) / 2f, (this.size.height - d) / 2f)
        val sz = Size(d, d)
        drawArc(colorFromHex(t.brd), 0f, 360f, false, style = Stroke(sw), topLeft = tl, size = sz)
        RES_DIMS.forEachIndexed { i, dim ->
            val frac = ((dimPct[dim] ?: 0) / 100f) * grow.value
            if (frac > 0f) {
                drawArc(
                    color = colorFromHex(typeColorOf(dim)).copy(alpha = 0.9f),
                    startAngle = -90f + i * 60f + 3f,
                    sweepAngle = 54f * frac,
                    useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round),
                    topLeft = tl, size = sz
                )
            }
        }
        if (!quiet) {
            drawArc(
                color = colorFromHex(t.gold).copy(alpha = 0.38f),
                startAngle = spin.value - 90f,
                sweepAngle = 30f,
                useCenter = false,
                style = Stroke(sw * 0.7f, cap = StrokeCap.Round),
                topLeft = tl, size = sz
            )
        }
        val glowR = d * 0.34f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(colorFromHex(t.gold).copy(alpha = 0.16f), Color.Transparent),
                center = center, radius = glowR
            ),
            radius = glowR, center = center
        )
    }
}

/** 狂厄共鸣结果页（对齐 HTML 覆写版 showResonanceResult 渲染） */
@Composable
fun ResonanceResultView(rr: ResonanceResult, onRestart: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val type = RESONANCE_TYPES[rr.typeId]!!
    val cp = AppStorage.data.customProfile
    val avatarKey = cp?.avatar
    val pName = cp?.name?.takeIf { it.isNotBlank() }
    val pSig = cp?.sig?.takeIf { it.isNotBlank() }
    // ★ 方案 5.6：六维条与频率图谱同一条时间线（一个 master 进度同时驱动两者）
    val animOn = rememberAnimationsOn()
    val grow = remember { Animatable(if (animOn) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animOn) {
            grow.snapTo(0f)
            grow.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
        }
    }
    // 结果页没有底部导航，这里要自己避让手势条 / 导航栏
    Column(
        Modifier.fillMaxWidth().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 玩家档案头
        Column(
            Modifier.fillMaxWidth().mbdiCard(shape = RoundedCornerShape(16.dp)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (avatarKey != null) {
                AvatarCircle(key = avatarKey, size = 48.dp, color = t.goldDim)
            } else {
                Box(
                    Modifier.width(48.dp).height(48.dp).clip(RoundedCornerShape(50))
                        .background(colorFromHex(t.goldDim)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pName != null) Text(pName.take(1), fontSize = 22.sp, color = colorFromHex(t.bg))
                    else Icon(Lucide.UserRound, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorFromHex(t.bg))
                }
            }
            if (pName != null) {
                Text(pName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorFromHex(t.text),
                    modifier = Modifier.padding(top = 4.dp))
            }
            if (pSig != null) {
                Text(pSig, fontSize = 11.sp, color = colorFromHex(t.text3))
            }
        }
        // 结果卡（★ 铭牌材质，与主鉴定结果页同一语言）
        Column(
            Modifier.fillMaxWidth().mbdiSurface(MbdiSurfaceKind.Plaque, RoundedCornerShape(16.dp)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.ScanLine, contentDescription = null, modifier = Modifier.size(14.dp), tint = colorFromHex(t.text3))
                Text("MBDI零镜归档报告", fontSize = 11.sp, color = colorFromHex(t.text3),
                    modifier = Modifier.padding(start = 5.dp))
            }
            // 中心「频率环」：六维色随波动的圆环（方案 5.6）
            FreqRing(
                dimPct = rr.dimPct, sizeDp = 132.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
            Text(type.name, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                color = colorFromHex(type.color), modifier = Modifier.padding(top = 8.dp))
            Text("归档编号：MBDI-${type.code}", fontSize = 11.sp,
                color = colorFromHex(t.text3), letterSpacing = 2.sp, modifier = Modifier.padding(top = 6.dp, bottom = 10.dp))
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Lucide.Sparkle, contentDescription = null, modifier = Modifier.size(13.dp), tint = colorFromHex(t.gold))
                    Text("特质判定", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorFromHex(t.gold),
                        modifier = Modifier.padding(start = 5.dp))
                }
                Text(type.trait, fontSize = 13.sp, lineHeight = 22.sp, color = colorFromHex(t.text2),
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Lucide.FileText, contentDescription = null, modifier = Modifier.size(13.dp), tint = colorFromHex(t.gold))
                    Text("零镜档案", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorFromHex(t.gold),
                        modifier = Modifier.padding(start = 5.dp))
                }
                Text(type.desc, fontSize = 13.sp, lineHeight = 22.sp, color = colorFromHex(t.text2),
                    modifier = Modifier.padding(top = 4.dp))
            }
        }
        // 频率图谱
        Column(
            Modifier.fillMaxWidth().mbdiCard(shape = RoundedCornerShape(16.dp)).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.Activity, contentDescription = null, modifier = Modifier.size(13.dp), tint = colorFromHex(t.text3))
                Text("狂厄频率图谱", fontSize = 11.sp, color = colorFromHex(t.text3),
                    letterSpacing = 2.sp, modifier = Modifier.padding(start = 5.dp))
            }
            // 尺寸跟着新雷达图放大（新版标签是 12sp，220dp 会裁到标签）
            HexRadar(
                rr.dimPct, type.color,
                Modifier.fillMaxWidth(0.92f).height(300.dp).padding(top = 10.dp),
                ripple = true,
                driveProgress = { grow.value }
            )
            // 维度条（与图谱同一条时间线，逐条错峰长出）
            RES_DIMS.forEachIndexed { i, d ->
                val pct = rr.dimPct[d] ?: 0
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(RES_DIM_NAMES[d] ?: d, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = colorFromHex(typeColorOf(d)), modifier = Modifier.width(52.dp))
                    Box(
                        Modifier.weight(1f).height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(colorFromHex(t.brd))
                    ) {
                        Box(
                            Modifier.fillMaxSize().drawBehind {
                                val k = ((grow.value - 0.10f - i * 0.06f) / 0.55f).coerceIn(0f, 1f)
                                val frac = (pct / 100f) * k
                                if (frac > 0f) {
                                    drawRect(
                                        color = colorFromHex(typeColorOf(d)),
                                        size = Size(size.width * frac, size.height)
                                    )
                                }
                            }
                        )
                    }
                    Text("$pct%", fontSize = 11.sp, color = colorFromHex(t.text3),
                        modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                }
            }
        }
        // 联动分析（★ 信号交错装饰：两条不同色光束交汇于一个节点）
        if (rr.charCode != null) {
            Box(Modifier.fillMaxWidth().mbdiCard(shape = RoundedCornerShape(12.dp)).padding(12.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Link, contentDescription = null, modifier = Modifier.size(13.dp), tint = colorFromHex(t.purple))
                        Text("联动分析", fontSize = MbdiType.Caption, color = colorFromHex(t.purple), letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 5.dp))
                    }
                    Canvas(Modifier.fillMaxWidth().height(22.dp).padding(top = 4.dp)) {
                        val y = size.height / 2f
                        val cxx = size.width / 2f
                        drawBeam(
                            from = Offset(0f, y - 7.dp.toPx()),
                            to = Offset(cxx, y),
                            color = colorFromHex(t.gold).copy(alpha = 0.65f),
                            width = 1.6.dp.toPx()
                        )
                        drawBeam(
                            from = Offset(size.width, y + 7.dp.toPx()),
                            to = Offset(cxx, y),
                            color = colorFromHex(t.purple).copy(alpha = 0.65f),
                            width = 1.6.dp.toPx()
                        )
                        drawCircle(colorFromHex(t.gold).copy(alpha = 0.18f), radius = 6.dp.toPx(), center = Offset(cxx, y))
                        drawCircle(colorFromHex(t.gold), radius = 2.2.dp.toPx(), center = Offset(cxx, y))
                    }
                    Text(crossoverMsg(rr.charCode, rr.typeId), fontSize = 13.sp, lineHeight = 21.sp,
                        color = colorFromHex(t.text2), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        // 重新测试
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("重新测试", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold),
                modifier = Modifier
                    .border(1.dp, colorFromHex(t.goldDim), RoundedCornerShape(4.dp))
                    .clickable(onClick = onRestart)
                    .padding(horizontal = 28.dp, vertical = 10.dp))
        }
    }
}

private fun typeColorOf(id: String): String = when (id) {
    "enlighten" -> "#4a8fe0"; "anomaly" -> "#c04ad0"; "ferocious" -> "#e05030"
    "tenacious" -> "#4a9a5a"; "precise" -> "#d0b020"; "mystic" -> "#8a6aaa"
    else -> "#d4af37"
}

/** 圆形头像（显示用） */
@Composable
fun AvatarCircle(key: String, size: Dp, color: String) {
    val context = LocalContext.current
    val bmp = remember(key) { AvatarStore.loadAvatar(context, key) }
    Box(
        Modifier.width(size).height(size).clip(RoundedCornerShape(50)).background(colorFromHex(color)),
        contentAlignment = Alignment.Center
    ) {
        bmp?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50))
            )
        } ?: Icon(Lucide.UserRound, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
    }
}