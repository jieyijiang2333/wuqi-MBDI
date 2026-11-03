package com.wqmtmbdi.mbcc.ui.render

import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 光效原语库（设计系统 2.0 · 方案 4.2）——
 * 全 App 的光只用这里的一组原语拼出来，不要在业务代码里手写渐变。
 * 纪律（方案第八章反面清单）：光从数据来、克制使用、一屏一个主角。
 * ★ 第 9 批按用户要求：已删除「径向泛光（radialGlow）」与「扫光（sweepLight）」两个原语。
 */

/** ③ 扫描线：横线 + 上下柔和拖影，progress（0..1）从上往下 —— 未解锁卡 / 启动时刻 */
fun Modifier.scanline(color: Color, progress: Float, thickness: Dp = 22.dp): Modifier =
    this.drawWithContent {
        drawContent()
        val h = size.height
        if (h <= 0f) return@drawWithContent
        val bandPx = thickness.toPx()
        val y = (h + bandPx * 2f) * progress - bandPx
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, color, Color.Transparent),
                startY = y,
                endY = y + bandPx
            ),
            topLeft = Offset(0f, y),
            size = Size(size.width, bandPx)
        )
    }

/**
 * ③b 扫描线（**绘制期读值版**）：progress 以 State 传入，值变化只触发重绘、不触发重组 ——
 * 图鉴里多张「未解锁」卡同时缓慢扫描时用这个，避免每帧重组（方案 5.5 渲染要点）。
 */
fun Modifier.scanline(color: Color, progress: State<Float>, thickness: Dp = 22.dp): Modifier =
    this.drawWithContent {
        drawContent()
        val p = progress.value
        val h = size.height
        if (h <= 0f) return@drawWithContent
        val bandPx = thickness.toPx()
        val y = (h + bandPx * 2f) * p - bandPx
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, color, Color.Transparent),
                startY = y,
                endY = y + bandPx
            ),
            topLeft = Offset(0f, y),
            size = Size(size.width, bandPx)
        )
    }

/** ④ 光桥：两点之间一条中间亮、两端淡的连线 —— 雷达「差异轴」（结果页用） */
fun DrawScope.drawBeam(from: Offset, to: Offset, color: Color, width: Float = 2.dp.toPx()) {
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, color, Color.Transparent),
            start = from,
            end = to
        ),
        start = from,
        end = to,
        strokeWidth = width
    )
}

/**
 * 真模糊（方案 4.3）：API 31+ 走 RenderEffect；低版本自动退回「无模糊」（上层用半透明磨砂兜底）。
 * 安静模式由调用方先判断（rememberQuietMode）。
 */
fun Modifier.mbdiBlur(radius: Dp): Modifier =
    if (Build.VERSION.SDK_INT >= 31) this.blur(radius, BlurredEdgeTreatment.Unbounded) else this