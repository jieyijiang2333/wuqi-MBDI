package com.wqmtmbdi.mbcc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.wqmtmbdi.mbcc.data.RES_DIM_NAMES
import com.wqmtmbdi.mbcc.data.RES_DIMS
import com.wqmtmbdi.mbcc.data.STATS_LABELS7
import com.wqmtmbdi.mbcc.ui.render.PaperNoise
import com.wqmtmbdi.mbcc.ui.render.drawBeam
import com.wqmtmbdi.mbcc.ui.theme.MbdiMotion
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ★ 雷达图（2026-09-28 重写，见《人格鉴定-UI重做方案.md》第五章）
 *
 * 相比旧版的三个根本性修正：
 *  1. **同一把尺子**：所有曲线一律 `值 / maxValue`（七维 = 10，六维 = 100）。
 *     旧版把玩家曲线按「自己的最大值」归一 → 玩家最强项永远顶满圈，对比结论是错的。
 *  2. **px / dp 分离**：旧版 `R = W/2 - 34f`、`textSize = 10f`、`Stroke(0.5f)` 全是像素值，
 *     在 3 倍屏上标签只有 ≈3.3sp、网格线只有 ≈0.17dp。现在全部 `dp.toPx()`，
 *     文字交给 Compose 的 TextMeasurer（自动跟随 sp 与系统字体缩放）。
 *  3. **可读 + 可动**：加刻度、维度名与数值双行标签、外圈加粗；
 *     进页面时「网格淡入 → 角色曲线生长 → 玩家曲线生长 → 顶点点亮」，点/拖可选中某一维看差异。
 */
private class Series(
    val values: List<Double>,
    val color: Color,
    val fillAlpha: Float,
    val strokeDp: Float,
    val dashed: Boolean,
    val dotDp: Float,
    /** 生长动画在总进度里的起止（0..1），两条曲线靠这个错开 */
    val from: Float,
    val to: Float
)

/**
 * 标签留白 = 画布的 14%（自适应）。
 * 旧版是固定 34 **像素**，在 3 倍屏上只有 11dp，标签必被裁；改成比例后
 * 从 170dp 的小图到全屏大图都不会裁到标签。
 */
private fun radarPadPx(w: Float, h: Float): Float = min(w, h) * 0.14f

/** 读屏数值格式：整数不带小数，否则一位小数 */
private fun fmtAxisValue(v: Double): String =
    if (v == v.toInt().toDouble()) "${v.toInt()}" else "%.1f".format(v)

/**
 * ★ 方案 5.6：把多边形每条边换成一段正弦波纹（顶点不变、中段起伏）——
 * 狂厄共鸣的「六维频率图谱」用，呼应「接收频率」的波纹语言。
 * ★ 第 6 批性能复查：Path 由调用方复用（rewind 重填），避免每帧新建对象。
 */
private fun wavyPolygon(path: Path, points: List<Offset>, phase: Float, amp: Float): Path {
    path.rewind()
    val n = points.size
    if (n < 3) return path
    for (i in 0 until n) {
        val a = points[i]
        val b = points[(i + 1) % n]
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / len
        val ny = dx / len
        val steps = 12
        for (s in 0..steps) {
            val f = s.toFloat() / steps
            val off = sin(f * 2f * PI.toFloat() * 2f + phase + i * 1.1f) * amp
            val x = a.x + dx * f + nx * off
            val y = a.y + dy * f + ny * off
            if (i == 0 && s == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
    }
    path.close()
    return path
}

/**
 * 命中判定：把点击位置换算成最近的轴。
 *
 * ★ 2026-09-28 收紧：以前"离哪根轴最近就算选中"，
 * 结果**手指从图上滑过去就会随手选中一维**（用户反馈的误触）。
 * 现在两个门槛都加上：
 *  ① 离圆心太近（< 30% 半径）不算；
 *  ② 与轴线的夹角必须小于"相邻轴夹角 × 40%"才算，否则什么都不做（不选中、也不清除已有选择）。
 */
private fun hitAxis(offset: Offset, w: Float, h: Float, padPx: Float, n: Int): Int? {
    val cx = w / 2f
    val cy = h / 2f
    val r = min(w, h) / 2f - padPx
    val dx = offset.x - cx
    val dy = offset.y - cy
    if (sqrt(dx * dx + dy * dy) < r * 0.30f) return null
    val a = atan2(dy, dx).toDouble()
    var best = 0
    var bestD = Double.MAX_VALUE
    for (i in 0 until n) {
        val ang = -PI / 2 + i * 2 * PI / n
        var d = abs(a - ang)
        while (d > PI) d -= 2 * PI
        d = abs(d)
        if (d < bestD) {
            bestD = d
            best = i
        }
    }
    val tolerance = (2 * PI / n) * 0.40
    return if (bestD <= tolerance) best else null
}

@Composable
private fun RadarCanvas(
    labels: List<String>,
    subLabels: List<String>?,
    labelColors: List<Color>,
    series: List<Series>,
    maxValue: Double,
    modifier: Modifier,
    showScale: Boolean = true,
    selectedAxis: Int? = null,
    onAxisChanged: ((Int?) -> Unit)? = null,
    /** 差异最大的一根轴：画「光桥」（两点之间流光 + 中点差值）；null 表示不画 */
    beamAxis: Int? = null,
    replayKey: Any = Unit,
    /** ★ 方案 5.6：顶点连线改为「波纹」（沿每条边正弦微扰），共鸣频率图谱用 */
    rippleEdges: Boolean = false,
    /** ★ 方案 5.6：外部时间线（与六维条同一条 master 进度，绘制期调用）；null = 用内部生长动画 */
    driveProgress: (() -> Float)? = null
) {
    val t = AppStorage.currentMBDITheme()
    val isLight = t.isLight()
    val animOn = rememberAnimationsOn()
    val quiet = rememberQuietMode()
    val measurer = rememberTextMeasurer()
    val n = labels.size

    val labelLayouts: List<TextLayoutResult> = remember(labels, labelColors) {
        labels.mapIndexed { i, s ->
            measurer.measure(
                s,
                TextStyle(
                    fontSize = MbdiType.Label,
                    fontWeight = FontWeight.Bold,
                    color = labelColors.getOrElse(i) { Color.White }
                )
            )
        }
    }
    val subColor = if (isLight) Color(0x99000000.toInt()) else Color(0xCCFFFFFF.toInt())
    val subLayouts: List<TextLayoutResult>? = remember(subLabels, isLight) {
        subLabels?.map { s ->
            measurer.measure(s, TextStyle(fontSize = MbdiType.Caption, color = subColor))
        }
    }

    val progress = remember { Animatable(if (animOn) 0f else 1f) }
    LaunchedEffect(replayKey, animOn, driveProgress) {
        if (driveProgress != null) {
            progress.snapTo(1f)   // 外部时间线驱动时，内部进度只作为「已完成」占位
            return@LaunchedEffect
        }
        if (!animOn) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(1f, tween(MbdiMotion.REVEAL, easing = FastOutSlowInEasing))
    }
    // 波纹相位：只在绘制期读值，不产生每帧重组（安静模式停在 0）
    val ripplePhase = rememberInfiniteTransition(label = "radarRipple").animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Restart),
        label = "ripplePhase"
    )
    // 波纹路径复用（第 6 批性能复查：不每帧新建 Path）
    val wavyPath = remember { Path() }
    // ★ 第 6 批无障碍（方案第六章）：雷达图给读屏的文字等价（维度名 + 数值）
    val a11yDesc = remember(labels, series) {
        labels.mapIndexed { i, l ->
            val vs = series.joinToString(" ") { s -> fmtAxisValue(s.values.getOrElse(i) { 0.0 }) }
            "$l $vs"
        }.joinToString("，")
    }

    val gridColor = if (isLight) 0x1A000000 else 0x1FFFFFFF
    val axisColor = if (isLight) 0x14000000 else 0x17FFFFFF
    val scaleColor = if (isLight) 0x80000000.toInt() else 0x8CFFFFFF.toInt()

    Canvas(
        modifier = modifier
            // ★ 只响应「点」：以前还接了拖动选中，导致手指在图上滑过就误触；
            //   去掉拖动后，雷达图不抢纵向滚动，页面上下滑动不受影响。
            .pointerInput(n, selectedAxis) {
                if (onAxisChanged == null) return@pointerInput
                detectTapGestures { off ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val hit = hitAxis(off, w, h, radarPadPx(w, h), n)
                    // 点中已选中的那一维 = 取消选择；点空处不做任何事（避免误触清空）
                    if (hit != null) onAxisChanged(if (hit == selectedAxis) null else hit)
                }
            }
            .semantics { contentDescription = a11yDesc }
    ) {
        // ★ 绘制期读值（外部时间线 / 内部进度都只在绘制时取），不触发每帧重组
        val p = driveProgress?.invoke() ?: progress.value
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val padPx = radarPadPx(w, h)
        val R = min(w, h) / 2f - padPx
        if (R <= 0f) return@Canvas
        val angles = FloatArray(n) { -PI.toFloat() / 2f + it * 2f * PI.toFloat() / n }

        // ---- 网格：5 环，外圈与奇数环加重，随进度淡入 ----
        val gridIn = (p / 0.22f).coerceIn(0f, 1f)
        for (ring in 1..5) {
            val rr = R * ring / 5f
            val path = Path()
            for (i in 0 until n) {
                val x = cx + rr * cos(angles[i])
                val y = cy + rr * sin(angles[i])
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            val isMajor = ring == 5 || ring % 2 == 1
            val mult = if (ring == 5) 1f else if (isMajor) 0.62f else 0.4f
            drawPath(
                path,
                color = Color(gridColor).copy(alpha = Color(gridColor).alpha * mult * gridIn),
                style = Stroke(width = (if (ring == 5) 1.4f else if (isMajor) 0.9f else 0.6f).dp.toPx())
            )
        }
        // ---- 轴线（选中那根加粗提亮 + 流光点亮）----
        val goldAccent = colorFromHex(t.gold)
        for (i in 0 until n) {
            val hl = selectedAxis == i
            val axisEnd = Offset(cx + R * cos(angles[i]), cy + R * sin(angles[i]))
            drawLine(
                color = Color(axisColor).copy(alpha = Color(axisColor).alpha * gridIn * (if (hl) 3.4f else 1f)),
                start = Offset(cx, cy),
                end = axisEnd,
                strokeWidth = (if (hl) 1.6f else 0.7f).dp.toPx()
            )
            if (hl) {
                // 选中轴的「流光」：中段亮、两端淡的一道光
                drawBeam(
                    from = Offset(cx, cy),
                    to = axisEnd,
                    color = goldAccent.copy(alpha = 0.60f * gridIn),
                    width = 2.dp.toPx()
                )
            }
        }
        // ---- 刻度：只标在正上方那条轴，避免七个方向堆数字 ----
        if (showScale) {
            val scaleStyle = TextStyle(fontSize = MbdiType.Caption, color = Color(scaleColor))
            for (ring in 1..5) {
                if (ring % 2 == 0) continue
                val v = maxValue * ring / 5.0
                val txt = if (v == v.toInt().toDouble()) "${v.toInt()}" else "%.1f".format(v)
                val lr = measurer.measure(txt, scaleStyle)
                drawText(
                    lr,
                    topLeft = Offset(cx + 4.dp.toPx(), cy - R * ring / 5f - lr.size.height / 2f),
                    alpha = gridIn
                )
            }
        }

        // ---- 数据层 ----
        for (s in series) {
            val growth = if (s.to <= s.from) 1f else ((p - s.from) / (s.to - s.from)).coerceIn(0f, 1f)
            if (growth <= 0.001f) continue
            val grown = List(n) { i ->
                val v = (s.values.getOrElse(i) { 0.0 } / maxValue).coerceIn(0.0, 1.0).toFloat()
                Offset(
                    cx + R * v * cos(angles[i]) * growth,
                    cy + R * v * sin(angles[i]) * growth
                )
            }
            val path = if (rippleEdges && !quiet) {
                // ★ 方案 5.6：顶点连线改为波纹（沿每条边正弦微扰，相位随时间流动）
                wavyPolygon(wavyPath, grown, ripplePhase.value, 1.6.dp.toPx())
            } else {
                val sp = Path()
                grown.forEachIndexed { i, o -> if (i == 0) sp.moveTo(o.x, o.y) else sp.lineTo(o.x, o.y) }
                sp.close()
                sp
            }
            // 填色 2.0「纱」质感（方案 5.4 ③）：径向渐变（中心实、外缘淡）+ 极淡纸纹噪点，不再是纯色块
            drawPath(
                path,
                brush = Brush.radialGradient(
                    colors = listOf(
                        s.color.copy(alpha = (s.fillAlpha * 1.25f).coerceAtMost(1f) * growth),
                        s.color.copy(alpha = s.fillAlpha * 0.45f * growth)
                    ),
                    center = Offset(cx, cy),
                    radius = R
                )
            )
            if (growth > 0.4f) {
                clipPath(path) {
                    drawRect(brush = PaperNoise.brush, alpha = 0.05f * ((growth - 0.4f) / 0.6f))
                }
            }
            drawPath(
                path,
                color = if (s.dashed) s.color.copy(alpha = 0.95f) else s.color,
                style = Stroke(
                    width = s.strokeDp.dp.toPx(),
                    pathEffect = if (s.dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 8f)) else null
                )
            )
            if (growth > 0.85f) {
                val dotA = ((growth - 0.85f) / 0.15f).coerceIn(0f, 1f)
                grown.forEach { o ->
                    drawCircle(s.color.copy(alpha = dotA), radius = s.dotDp.dp.toPx(), center = o)
                    drawCircle(
                        Color.White.copy(alpha = 0.5f * dotA),
                        radius = s.dotDp.dp.toPx(),
                        center = o,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
        }

        // ---- 差异连线（选中某轴时，把两条曲线在该轴上的点连起来）----
        if (selectedAxis != null && series.size >= 2) {
            val i = selectedAxis
            fun ptOf(s: Series): Offset {
                val v = (s.values.getOrElse(i) { 0.0 } / maxValue).coerceIn(0.0, 1.0).toFloat()
                return Offset(cx + R * v * cos(angles[i]), cy + R * v * sin(angles[i]))
            }
            val pa = ptOf(series[0])
            val pb = ptOf(series[1])
            drawLine(
                color = goldAccent.copy(alpha = 0.75f),
                start = pa, end = pb,
                strokeWidth = 1.4.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )
            drawCircle(goldAccent.copy(alpha = 0.95f), radius = 3.4.dp.toPx(), center = pa)
            drawCircle(goldAccent.copy(alpha = 0.95f), radius = 3.4.dp.toPx(), center = pb)
        }

        // ---- 光桥（方案 5.4 ③）：差异最大的一根轴上，两点之间一条流光 + 中点标注差值 ----
        if (beamAxis != null && series.size >= 2) {
            val beamA = ((p - 0.88f) / 0.12f).coerceIn(0f, 1f)
            if (beamA > 0.01f && (selectedAxis == null || selectedAxis == beamAxis)) {
                val i = beamAxis
                fun ptOf(s: Series): Offset {
                    val v = (s.values.getOrElse(i) { 0.0 } / maxValue).coerceIn(0.0, 1.0).toFloat()
                    return Offset(cx + R * v * cos(angles[i]), cy + R * v * sin(angles[i]))
                }
                val pa = ptOf(series[0])
                val pb = ptOf(series[1])
                val mid = Offset((pa.x + pb.x) / 2f, (pa.y + pb.y) / 2f)
                drawBeam(from = pa, to = pb, color = goldAccent.copy(alpha = 0.85f * beamA), width = 2.2.dp.toPx())
                drawCircle(goldAccent.copy(alpha = 0.16f * beamA), radius = 8.dp.toPx(), center = mid)
                drawCircle(goldAccent.copy(alpha = 0.90f * beamA), radius = 2.6.dp.toPx(), center = mid)
                // ★ 用户要求：删掉中点的「差 X.X」数字（只保留光桥本身，避免影响观感）
            }
        }

        // ---- 标签（维度名 + 数值），按左右位置自动对齐 ----
        val labelR = R + 14.dp.toPx()
        for (i in 0 until n) {
            val cosA = cos(angles[i])
            val lx = cx + labelR * cosA
            val ly = cy + labelR * sin(angles[i])
            val lr = labelLayouts[i]
            val sl = subLayouts?.getOrNull(i)
            val totalH = lr.size.height + (sl?.let { it.size.height } ?: 0)
            fun alignX(width: Int): Float = when {
                cosA > 0.34f -> lx
                cosA < -0.34f -> lx - width
                else -> lx - width / 2f
            }
            val ty = ly - totalH / 2f
            drawText(lr, topLeft = Offset(alignX(lr.size.width), ty), alpha = gridIn)
            if (sl != null) {
                drawText(sl, topLeft = Offset(alignX(sl.size.width), ty + lr.size.height), alpha = gridIn)
            }
        }
    }
}

/** 七维单层（图鉴角色详情、记录详情） */
@Composable
fun StatsRadar(
    stats: List<Number>,
    color: String,
    modifier: Modifier = Modifier
) {
    val c = colorFromHex(color)
    RadarCanvas(
        labels = STATS_LABELS7,
        subLabels = stats.map { "%.1f".format(it.toDouble()) },
        labelColors = List(STATS_LABELS7.size) { c },
        series = listOf(Series(stats.map { it.toDouble() }, c, 0.16f, 2.2f, false, 3.6f, 0f, 1f)),
        maxValue = 10.0,
        modifier = modifier
    )
}

/**
 * 七维对比（结果页）★
 *
 * **玩家与角色用同一个基准（都 /10）**，不再让玩家的最强项顶满圈 —— 这是本次最关键的一处修正。
 */
@Composable
fun CompareRadar(
    playerStats: List<Double>,
    charStats: List<Number>,
    color: String,
    modifier: Modifier = Modifier,
    selectedAxis: Int? = null,
    onAxisChanged: ((Int?) -> Unit)? = null,
    replayKey: Any = Unit
) {
    val t = AppStorage.currentMBDITheme()
    // 光桥画在「差异最大」的那根轴上（方案 5.4 ③）：把差距从文字变成视觉
    val diffs = List(STATS_LABELS7.size) { i ->
        playerStats.getOrElse(i) { 0.0 } - charStats.getOrElse(i) { 0 }.toDouble()
    }
    val beamAxis = diffs.indices.maxByOrNull { abs(diffs[it]) }?.takeIf { abs(diffs[it]) > 0.05 }
    RadarCanvas(
        labels = STATS_LABELS7,
        subLabels = null,
        labelColors = List(STATS_LABELS7.size) { colorFromHex(t.text2) },
        series = listOf(
            Series(charStats.map { it.toDouble() }, colorFromHex(color), 0.16f, 2.4f, false, 3.6f, 0.18f, 0.62f),
            Series(playerStats, colorFromHex(t.gold), 0.10f, 2.0f, true, 3.2f, 0.5f, 0.95f)
        ),
        maxValue = 10.0,
        selectedAxis = selectedAxis,
        onAxisChanged = onAxisChanged,
        beamAxis = beamAxis,
        replayKey = replayKey,
        modifier = modifier
    )
}

/** 六维频率图谱（狂厄共鸣结果） */
@Composable
fun HexRadar(
    dimPct: Map<String, Int>,
    highlightColor: String,
    modifier: Modifier = Modifier,
    replayKey: Any = Unit,
    /** ★ 方案 5.6：顶点连线改为波纹 */
    ripple: Boolean = true,
    /** ★ 方案 5.6：外部时间线（与六维条同一条 master 进度） */
    driveProgress: (() -> Float)? = null
) {
    val dimColors = mapOf(
        "enlighten" to "#4a8fe0", "anomaly" to "#c04ad0", "ferocious" to "#e05030",
        "tenacious" to "#4a9a5a", "precise" to "#d0b020", "mystic" to "#8a6aaa"
    )
    RadarCanvas(
        labels = RES_DIMS.map { RES_DIM_NAMES[it] ?: it },
        subLabels = RES_DIMS.map { "${dimPct[it] ?: 0}%" },
        labelColors = RES_DIMS.map { colorFromHex(dimColors[it] ?: highlightColor) },
        series = listOf(
            Series(
                RES_DIMS.map { (dimPct[it] ?: 0).toDouble() },
                colorFromHex(highlightColor), 0.22f, 2.6f, false, 4f, 0.18f, 0.9f
            )
        ),
        maxValue = 100.0,
        showScale = false,
        replayKey = replayKey,
        rippleEdges = ripple,
        driveProgress = driveProgress,
        modifier = modifier
    )
}
