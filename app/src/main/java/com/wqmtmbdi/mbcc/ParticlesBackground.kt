package com.wqmtmbdi.mbcc

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.withFrameNanos
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private data class Palette(val base: String, val blend: Float)

/** 深色主题用的粒子配色（金 / 紫 / 蓝）—— 目前没有深色配色，留着备用 */
private val PALETTES_DARK = listOf(
    Palette("212,175,55", 0.9f),
    Palette("107,47,160", 0.9f),
    Palette("90,140,220", 0.5f)
)

/** 纯白配色用：深金 / 灰紫 / 中灰（白底上再画浅金粒子等于看不见） */
private val PALETTES_LIGHT = listOf(
    Palette("184,137,43", 0.85f),
    Palette("91,91,138", 0.85f),
    Palette("120,120,132", 0.6f)
)

private fun parseBase(s: String): Triple<Int, Int, Int> {
    val p = s.split(",")
    return Triple(p[0].toInt(), p[1].toInt(), p[2].toInt())
}

/** 粒子（对齐 HTML Pt 类：60 普通 + 6 大光晕，呼吸透明度，多形状） */
private class Pt(val big: Boolean) {
    var x = 0f; var y = 0f
    var s = 0f; var sx = 0f; var sy = 0f
    var o = 0f; var ca = 0f
    var type = 0
    var rot = 0f; var rs = 0f
    var ph = 0f; var ps = 0f
    var base = ""; var blend = 0f
    var w = 0f; var h = 0f

    fun reset(w: Float, h: Float, palettes: List<Palette>) {
        this.w = w; this.h = h
        x = Random.nextFloat() * w; y = Random.nextFloat() * h
        if (big) {
            s = 3f + Random.nextFloat() * 4f
            sx = (Random.nextFloat() - .5f) * .12f
            sy = (Random.nextFloat() - .5f) * .12f
            o = .10f + Random.nextFloat() * .12f
            type = 0
        } else {
            s = Random.nextFloat() * 1.8f + .5f
            sx = (Random.nextFloat() - .5f) * .3f
            sy = (Random.nextFloat() - .5f) * .3f
            o = .25f + Random.nextFloat() * .35f
            type = Random.nextInt(3)
        }
        rot = Random.nextFloat() * (2 * PI).toFloat()
        rs = (Random.nextFloat() - .5f) * .03f
        ph = Random.nextFloat() * (2 * PI).toFloat()
        ps = .006f + Random.nextFloat() * .016f
        val p = palettes[Random.nextInt(palettes.size)]
        base = p.base; blend = p.blend
    }

    fun update() {
        x += sx; y += sy; ph += ps; rot += rs
        ca = o * (0.5f + 0.5f * sin(ph)) * blend
        if (x < -12f) x = w + 12f; if (x > w + 12f) x = -12f
        if (y < -12f) y = h + 12f; if (y > h + 12f) y = -12f
    }
}

/**
 * 粒子背景（对齐 HTML pCanvas）：60 普通粒子 + 6 光晕粒子，粒子间连线。
 * 层级：置于所有页面底层（z-index 0），指针事件穿透。
 */
@Composable
/**
 * @param count 粒子数量。★ 答题页 / 结果页传 24（默认 66）—— 那两页有内容动画在跑，
 *              粒子降级把 GPU 让出来，低端机才不掉帧。
 */
fun ParticlesBackground(modifier: Modifier = Modifier, count: Int = 66) {
    val theme = AppStorage.currentMBDITheme()
    val isLight = theme.isLight()
    val palettes = if (isLight) PALETTES_LIGHT else PALETTES_DARK
    val linkRgb = if (isLight) floatArrayOf(140f / 255f, 120f / 255f, 80f / 255f)
    else floatArrayOf(212f / 255f, 175f / 255f, 55f / 255f)
    val linkAlpha = if (isLight) 0.10f else 0.05f
    // 主题或数量变化时重建粒子（颜色跟着换）
    val particles = remember(theme.id, count) { MutableList(count) { Pt(it >= count - 6) } }
    // 普通粒子数 = 总数 - 6 个光晕（粒子降级到 24 时 = 18）
    val linkCount = (count - 6).coerceAtLeast(0)
    val tick = remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { tick.value = it }
        }
    }
    // 订阅 tick 触发每帧重绘（仅重绘，不重建 UI）
    tick.value
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        particles.forEach { if (it.w != w || it.h != h) it.reset(w, h, palettes) }
        // 更新
        particles.forEach { it.update() }
        // 连线（仅普通粒子之间，距离90）
        // ★ 原来这里写死 60：粒子降级到 24 时 particles[i] 越界闪退（1.4.0「点开始就闪退」的根因）
        for (i in 0 until linkCount) {
            for (j in i + 1 until linkCount) {
                val a = particles[i]; val b = particles[j]
                val dx = a.x - b.x; val dy = a.y - b.y
                val d = sqrt(dx * dx + dy * dy)
                if (d < 90f) {
                    val alpha = (1f - d / 90f) * linkAlpha
                    drawLine(
                        color = Color(linkRgb[0], linkRgb[1], linkRgb[2], alpha),
                        start = Offset(a.x, a.y),
                        end = Offset(b.x, b.y),
                        strokeWidth = 0.4f
                    )
                }
            }
        }
        // 绘制粒子
        particles.forEach { p ->
            val (r, g, b) = parseBase(p.base)
            val cs = p.s * (if (p.big) 1f else .75f + .35f * sin(p.ph * 1.7f))
            if (p.big) {
                // 光晕：外圈淡 + 内圈亮
                drawCircle(Color(r / 255f, g / 255f, b / 255f, p.ca * .22f), radius = cs * 3f, center = Offset(p.x, p.y))
                drawCircle(Color(r / 255f, g / 255f, b / 255f, p.ca), radius = cs * 1.4f, center = Offset(p.x, p.y))
            } else if (p.type == 0) {
                drawCircle(Color(r / 255f, g / 255f, b / 255f, p.ca), radius = cs, center = Offset(p.x, p.y))
            } else if (p.type == 1) {
                // 菱形
                val c = Offset(p.x, p.y)
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c.x, c.y - cs * 1.6f)
                    lineTo(c.x + cs * 1.6f, c.y)
                    lineTo(c.x, c.y + cs * 1.6f)
                    lineTo(c.x - cs * 1.6f, c.y)
                    close()
                }
                drawPath(path, Color(r / 255f, g / 255f, b / 255f, p.ca * .8f))
            } else {
                // 星芒十字
                val c = Offset(p.x, p.y)
                drawLine(Color(r / 255f, g / 255f, b / 255f, p.ca), Offset(c.x, c.y - cs * 2.4f), Offset(c.x, c.y + cs * 2.4f), strokeWidth = 0.7f)
                drawLine(Color(r / 255f, g / 255f, b / 255f, p.ca), Offset(c.x - cs * 2.4f, c.y), Offset(c.x + cs * 2.4f, c.y), strokeWidth = 0.7f)
                drawCircle(Color(r / 255f, g / 255f, b / 255f, p.ca), radius = cs * .45f, center = c)
            }
        }
    }
}
