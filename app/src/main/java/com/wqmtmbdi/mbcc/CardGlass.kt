package com.wqmtmbdi.mbcc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wqmtmbdi.mbcc.ui.render.paperNoise

/**
 * 材质 2.0（2026-10-01 设计系统地基，见《人格鉴定-界面深度设计方案.md》2.2）——
 * 四种材质：Panel 面板 / Plaque 铭牌 / Paper 纸条 / Glass 玻璃。
 * mbdiCard / mbdiSolid 是两个兼容入口：**现有页面一行不用改，质感自动升级**。
 */
enum class MbdiSurfaceKind { Panel, Plaque, Paper, Glass }

/** 面板（绝大多数卡片）：磨砂底 + 纸纹 + 顶缘高光 + 中性软阴影 */
fun Modifier.mbdiCard(shape: Shape = RoundedCornerShape(14.dp), tint: String? = null): Modifier =
    mbdiSurface(MbdiSurfaceKind.Panel, shape, tint)

/** 玻璃（弹窗 / 浮层实底）：亮底 + 更强顶缘高光 + 大阴影 */
fun Modifier.mbdiSolid(shape: Shape = RoundedCornerShape(20.dp), tint: String? = null): Modifier =
    mbdiSurface(MbdiSurfaceKind.Glass, shape, tint)

private data class SurfaceSpec(
    val elev: Dp,
    val amb: Color,
    val spot: Color,
    val bgHex: String,
    val strokeHex: String,
    val noise: Float,
    val rim: Float,
    val goldGlow: Boolean
)

/**
 * 统一材质入口。
 *  - Panel：日常卡片；
 *  - Plaque：铭牌（结果页主卡等）——多一层主题色「光晕阴影」；
 *  - Paper：纸条（记录 / 简介）——纸纹更重、阴影最轻；
 *  - Glass：玻璃（弹窗实底）——亮底、最强顶缘高光、大阴影。
 */
fun Modifier.mbdiSurface(
    kind: MbdiSurfaceKind,
    shape: Shape = RoundedCornerShape(16.dp),
    tint: String? = null
): Modifier {
    val t = AppStorage.currentMBDITheme()
    val spec = when (kind) {
        // ★ 第 8 批：深色仪器面板——阴影用更深的黑（深底上要压得住），噪点为白砂面（见 Paper.kt）
        MbdiSurfaceKind.Panel -> SurfaceSpec(
            5.dp, Color(0x30000000), Color(0x48000000),
            tint ?: t.card, t.brd, 0.30f, 0.65f, false
        )
        MbdiSurfaceKind.Plaque -> SurfaceSpec(
            8.dp, Color(0x38000000), Color(0x50000000),
            tint ?: t.card, t.brd, 0.30f, 0.85f, true
        )
        MbdiSurfaceKind.Paper -> SurfaceSpec(
            3.dp, Color(0x28000000), Color(0x3C000000),
            tint ?: t.card, t.brd, 0.45f, 0.45f, false
        )
        MbdiSurfaceKind.Glass -> SurfaceSpec(
            12.dp, Color(0x70000000), Color(0x70000000),
            tint ?: t.card, t.brd, 0.20f, 0.80f, false
        )
    }
    val shadowed = this.shadow(
        elevation = spec.elev,
        shape = shape,
        ambientColor = spec.amb,
        spotColor = spec.spot
    )
    val withGlow = if (spec.goldGlow) {
        // 铭牌的第二层阴影：不是黑，而是主题色「光晕」（方案 2.2 的发光做法）
        shadowed.shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = colorFromHex(t.gold).copy(alpha = 0.16f),
            spotColor = colorFromHex(t.gold).copy(alpha = 0.20f)
        )
    } else shadowed
    return withGlow
        .clip(shape)
        .background(colorFromHex(spec.bgHex))
        .paperNoise(spec.noise)
        .rimLight(
            if (AppStorage.currentMBDITheme().isLight()) Color.Black.copy(alpha = spec.rim * 0.14f)
            else Color.White.copy(alpha = spec.rim)
        )
        .border(1.dp, colorFromHex(spec.strokeHex), shape)
}

/** 顶缘 1px 高光（两端淡出）——「仪器面板」的受光边（画在圆角之内，不越界） */
private fun Modifier.rimLight(color: Color): Modifier = this.drawWithContent {
    drawContent()
    val inset = 14.dp.toPx()
    val w = size.width
    if (w <= inset * 2f) return@drawWithContent
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, color, Color.Transparent),
            startX = inset,
            endX = w - inset
        ),
        start = Offset(inset, 0.5.dp.toPx()),
        end = Offset(w - inset, 0.5.dp.toPx()),
        strokeWidth = 1.dp.toPx()
    )
}