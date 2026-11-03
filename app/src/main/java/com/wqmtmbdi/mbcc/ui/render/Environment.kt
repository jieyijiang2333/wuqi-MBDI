package com.wqmtmbdi.mbcc.ui.render

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.wqmtmbdi.mbcc.ParticlesBackground
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode

/**
 * 环境层（L0，方案 4.1）：氛围光 + 粒子 + 纸纹底噪 —— 全 App 唯一的背景入口。
 * 安静模式：氛围光不呼吸（走静态值），其余不变；粒子降级参数由调用方给。
 */
@Composable
fun EnvironmentLayer(
    modifier: Modifier = Modifier,
    particleCount: Int = 66,
    accent: Color,
    accent2: Color,
    /** ★ 方案 5.10：当前页面序号（Tab 0~2）——氛围光随页面切换平滑迁移（同色系内，Gentle 弹簧） */
    pageIndex: Int = 0
) {
    val quiet = rememberQuietMode()
    // 呼吸：6 秒一轮的极缓明暗（安静模式走静态值）
    val transition = rememberInfiniteTransition(label = "ambient")
    val breathe by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val glow = if (quiet) 0.85f else breathe
    // 氛围光随页面迁移：金斑左右游走、紫斑反向让位（同色系内，不换色）
    val shift by animateFloatAsState(
        targetValue = pageIndex.coerceIn(0, 2) / 2f,
        animationSpec = if (quiet) tween(0) else MbdiSpring.Gentle,
        label = "ambientShift"
    )

    Box(modifier) {
        // 氛围光：左上偏金、右中偏紫，两团极淡的径向光斑（只做「温感」，不抢内容）
        Box(
            Modifier.fillMaxSize().drawBehind {
                val k = shift
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.055f * glow), Color.Transparent),
                        center = Offset(size.width * (0.14f + 0.30f * k), size.height * (0.05f + 0.10f * k)),
                        radius = size.width * 0.95f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(accent2.copy(alpha = 0.045f * glow), Color.Transparent),
                        center = Offset(size.width * (0.92f - 0.34f * k), size.height * (0.42f - 0.08f * k)),
                        radius = size.width * 0.85f
                    )
                )
            }
        )
        // 粒子（沿用现有体系：内含越界修复与降级参数）
        ParticlesBackground(Modifier.fillMaxSize(), count = particleCount)
        // 纸纹底噪（静态、恒定，极低透明度）
        Box(Modifier.fillMaxSize().paperNoise(0.18f))
    }
}