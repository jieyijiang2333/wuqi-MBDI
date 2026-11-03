package com.wqmtmbdi.mbcc.ui.render

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.isLight
import kotlin.random.Random

/**
 * 纸纹噪声（设计系统 2.0 · 方案 2.2）：全 App 只生成一次 128×128 稀疏噪点，平铺复用。
 * ★ 第 8 批：深浅两版——浅色主题用黑噪点（纸感），深色主题用白噪点（仪器面板的砂面感），
 *   由当前主题自动选择；位图仍然全 App 只生成一次。
 */
object PaperNoise {
    private const val N = 128

    private fun makeNoise(white: Boolean): ImageBitmap {
        val pixels = IntArray(N * N) {
            // 稀疏噪点：约 18% 的像素带 0~25 的 alpha（整体再用 alpha 参数压一档）
            val a = if (Random.nextInt(100) < 18) Random.nextInt(26) else 0
            if (white) (a shl 24) or 0x00FFFFFF else (a shl 24)
        }
        val bmp = Bitmap.createBitmap(N, N, Bitmap.Config.ARGB_8888)
        bmp.setPixels(pixels, 0, N, 0, 0, N, N)
        return bmp.asImageBitmap()
    }

    private val inkBrush: ShaderBrush by lazy {
        ShaderBrush(ImageShader(makeNoise(white = false), TileMode.Repeated, TileMode.Repeated))
    }
    private val sandBrush: ShaderBrush by lazy {
        ShaderBrush(ImageShader(makeNoise(white = true), TileMode.Repeated, TileMode.Repeated))
    }

    /** 当前主题下的噪点质感（深色主题 = 白砂面，浅色主题 = 黑纸纹） */
    internal val brush: ShaderBrush
        get() = if (AppStorage.currentMBDITheme().isLight()) inkBrush else sandBrush
}

/** 给任意元素叠一层纸纹（画在内容之上，膜感均匀） */
fun Modifier.paperNoise(alpha: Float): Modifier = this.drawWithContent {
    drawContent()
    if (alpha > 0f) drawRect(brush = PaperNoise.brush, alpha = alpha)
}