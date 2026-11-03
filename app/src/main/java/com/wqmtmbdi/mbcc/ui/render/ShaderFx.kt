package com.wqmtmbdi.mbcc.ui.render

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex

/**
 * AGSL 着色器增强（设计系统 2.0 · 方案 4.4，2026-10-01 用户拍板「做」）。
 *
 * 纪律：**只在 API 33+（RuntimeShader）开启**；低版本 / 安静模式由调用方传 intensity=0，
 * 本文件不做任何降级绘制——降级画面全部由 Canvas 渐变承担（方案第四章：所有高级必须可降级）。
 */
object ShaderFx {
    /** 平台是否支持 AGSL（RuntimeShader 自 API 33 起） */
    val supported: Boolean get() = Build.VERSION.SDK_INT >= 33
}

/**
 * 揭晓时刻的「狂厄场」：整屏缓慢滚动的能量干扰纹（金色，低透明度，叠在幕布之上、文字之下）。
 *
 * @param timeSec   演出时间（秒），驱动噪声滚动
 * @param intensity 0 表示关闭（安静模式 / 低版本 / 演出之外）
 */
@Composable
fun Modifier.erraticField(timeSec: Float, intensity: Float): Modifier {
    if (!ShaderFx.supported) return this
    val shader = remember { RuntimeShader(ERRATIC_FIELD_AGSL) }
    val paint = remember { android.graphics.Paint() }
    val gold = colorFromHex(AppStorage.currentMBDITheme().gold)
    return this.drawBehind {
        if (intensity <= 0.002f) return@drawBehind
        shader.setFloatUniform("uSize", size.width, size.height)
        shader.setFloatUniform("uTime", timeSec)
        shader.setFloatUniform("uIntensity", intensity)
        shader.setFloatUniform("uColor", gold.red, gold.green, gold.blue)
        paint.shader = shader
        drawIntoCanvas { it.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint) }
    }
}

/** 三层正弦叠加的干扰场：亮脊 + 纵向扫描调制，颜色由 uColor 从主题金派生 */
private const val ERRATIC_FIELD_AGSL = """
uniform float2 uSize;
uniform float uTime;
uniform float uIntensity;
uniform float3 uColor;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / max(uSize.x, 1.0);
    float t = uTime;
    float n = 0.0;
    n += 0.55 * sin(uv.x * 36.0 + t * 1.6);
    n += 0.35 * sin(uv.y * 44.0 - t * 1.25 + uv.x * 10.0);
    n += 0.30 * sin((uv.x + uv.y) * 24.0 + t * 2.2);
    float v = n * 0.5 + 0.5;
    float band = smoothstep(0.68, 1.0, v);
    float scan = 0.55 + 0.45 * sin(fragCoord.y * 0.05 + t * 1.8);
    float a = band * scan * uIntensity;
    return half4(half3(uColor) * half(a), half(a));
}
"""