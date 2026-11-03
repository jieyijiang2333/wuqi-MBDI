package com.wqmtmbdi.mbcc.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import kotlin.math.roundToInt

/** 三环仪表的一环数据 */
data class TriRingData(
    val key: String,
    val label: String,
    val have: Int,
    val total: Int,
    val color: String
)

/**
 * 三环仪表（设计系统 2.0 · 方案 5.1/5.5）：禁闭者 / 狂厄特化 / 成就三个并排小环。
 *
 *  - 环内数字等宽（tnum）且入场时从 0 滚到当前值，环弧随数字一起生长；
 *  - 环上一条短「流光弧」缓慢转动（安静模式不转）；
 *  - 点任一环 = 进入对应子页（把「进度」变成入口，图鉴页在用）。
 */
@Composable
fun TriRingGauge(
    rings: List<TriRingData>,
    modifier: Modifier = Modifier,
    onSelect: ((String) -> Unit)? = null
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        rings.forEach { r ->
            RingGauge(r, Modifier.weight(1f)) { onSelect?.invoke(r.key) }
        }
    }
}

@Composable
private fun RingGauge(data: TriRingData, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val t = AppStorage.currentMBDITheme()
    val c = colorFromHex(data.color)
    val animOn = rememberAnimationsOn()
    val quiet = rememberQuietMode()

    // 数字滚动（900ms；系统关动画直接给终值）
    val num = remember { Animatable(if (animOn) 0f else data.have.toFloat()) }
    LaunchedEffect(data.have) {
        if (!animOn) {
            num.snapTo(data.have.toFloat())
            return@LaunchedEffect
        }
        num.snapTo(0f)
        num.animateTo(data.have.toFloat(), tween(900, easing = FastOutSlowInEasing))
    }
    // 流光弧：只读到绘制期，不产生每帧重组
    val spin = rememberInfiniteTransition(label = "ringSpin").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4600, easing = LinearEasing), RepeatMode.Restart),
        label = "spin"
    )
    val track = colorFromHex(t.brd)

    Column(
        modifier.clickable(enabled = onClick != null) { onClick?.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(66.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val sw = 5.dp.toPx()
                val d = size.minDimension - sw
                val tl = Offset((size.width - d) / 2f, (size.height - d) / 2f)
                drawArc(track, 0f, 360f, false, style = Stroke(sw), topLeft = tl, size = Size(d, d))
                val frac = if (data.total > 0) data.have.toFloat() / data.total else 1f
                val grow = if (data.have > 0) (num.value / data.have).coerceIn(0f, 1f) else 1f
                drawArc(
                    color = c, startAngle = -90f, sweepAngle = 360f * frac * grow, useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round), topLeft = tl, size = Size(d, d)
                )
                if (!quiet) {
                    drawArc(
                        color = c.copy(alpha = 0.55f), startAngle = spin.value - 90f, sweepAngle = 44f,
                        useCenter = false, style = Stroke(sw * 0.8f, cap = StrokeCap.Round),
                        topLeft = tl, size = Size(d, d)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${num.value.roundToInt()}",
                    style = TextStyle(
                        fontSize = MbdiType.Heading,
                        fontWeight = FontWeight.Black,
                        color = c,
                        fontFeatureSettings = "tnum"
                    )
                )
                Text("/${data.total}", fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(data.label, fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
    }
}