package com.wqmtmbdi.mbcc

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 头像裁剪独立页面：
 * 调用方传入待裁剪图片文件路径（extra "src"）与头像 key（extra "key"），
 * 双指平移/缩放后按圆形裁剪，写入头像存储并返回 RESULT_OK。
 */
class AvatarCropActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppStorage.init(applicationContext)   // 幂等；防止本页被系统单独重建时读不到主题
        edgeToEdgeTransparent(darkIcons = AppStorage.currentMBDITheme().isLight())
        val srcPath = intent.getStringExtra("src")
        val key = intent.getStringExtra("key") ?: AvatarStore.PROFILE_KEY
        val src = if (srcPath != null) BitmapFactory.decodeFile(srcPath) else null
        if (src == null) { finish(); return }
        setContent {
            CropPage(src = src, key = key)
        }
    }
}

@Composable
private fun CropPage(src: Bitmap, key: String) {
    val theme = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var scale by remember(src) { mutableFloatStateOf(1f) }
    var offset by remember(src) { mutableStateOf(Offset.Zero) }
    var vw by remember { mutableIntStateOf(0) }
    var vh by remember { mutableIntStateOf(0) }

    fun clamp(nx: Float, ny: Float, s: Float): Offset {
        if (vw == 0 || vh == 0) return Offset(nx, ny)
        val cover = max(vw.toFloat() / src.width, vh.toFloat() / src.height)
        val dw = src.width * cover * s
        val dh = src.height * cover * s
        val tx = max(0f, (dw - vw) / 2f)
        val ty = max(0f, (dh - vh) / 2f)
        return Offset(nx.coerceIn(-tx, tx), ny.coerceIn(-ty, ty))
    }

    Box(Modifier.fillMaxSize().background(colorFromHex(theme.bg))) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)
        ) {
            Text("裁剪头像", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorFromHex(theme.gold))
            Text("双指拖动、缩放，调整到合适位置后点击「完成」", fontSize = 12.sp, color = colorFromHex(theme.text3))
            Spacer(Modifier.padding(top = 12.dp))

            // 裁剪区（正方形）
            Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
                // 图片层：圆形取景内可平移缩放
                Box(
                    Modifier
                        .matchParentSize()
                        .onSizeChanged { vw = it.width; vh = it.height }
                        .pointerInput(src) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val ns = (scale * zoom).coerceIn(1f, 8f)
                                scale = ns
                                offset = clamp(offset.x + pan.x, offset.y + pan.y, ns)
                            }
                        }
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        val cover = max(size.width / src.width, size.height / src.height) * scale
                        val dw = src.width * cover
                        val dh = src.height * cover
                        val left = (size.width - dw) / 2f + offset.x
                        val top = (size.height - dh) / 2f + offset.y
                        drawImage(
                            image = src.asImageBitmap(),
                            dstOffset = IntOffset(left.roundToInt(), top.roundToInt()),
                            dstSize = IntSize(dw.roundToInt(), dh.roundToInt())
                        )
                    }
                }
                // 遮罩层：圆外变暗 + 剪切环（EvenOdd 填充，圆内不暗）
                Canvas(Modifier.matchParentSize()) {
                    val mask = Path().apply {
                        addRect(Rect(Offset.Zero, size))
                        addOval(Rect(center = Offset(size.width / 2f, size.height / 2f), radius = size.minDimension / 2f))
                        fillType = PathFillType.EvenOdd
                    }
                    drawPath(mask, Color.Black.copy(alpha = 0.5f))
                    drawCircle(colorFromHex(theme.gold), radius = size.minDimension / 2f - 1f, style = Stroke(width = 3f))
                    drawCircle(colorFromHex(theme.goldDim).copy(alpha = 0.7f), radius = size.minDimension / 2f, style = Stroke(width = 1f))
                }
            }

            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth().navigationBarsPadding()) {
                TextButton(
                    onClick = { (context as Activity).finish() },
                    modifier = Modifier.weight(1f).mbdiCard(shape = RoundedCornerShape(22.dp))
                ) { Text("取消", color = colorFromHex(theme.text3), fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                TextButton(
                    onClick = {
                        val bmp = cropCircle(src, vw, vh, scale, offset)
                        AvatarStore.saveAvatar(context, key, bmp)
                        (context as Activity).setResult(Activity.RESULT_OK)
                        (context as Activity).finish()
                    },
                    modifier = Modifier.weight(1f).background(
                        Brush.horizontalGradient(listOf(colorFromHex(theme.gold), colorFromHex(theme.purple))),
                        RoundedCornerShape(22.dp)
                    )
                ) { Text("完成", color = colorFromHex(theme.onAccent), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

/** 以显示区中心为圆心裁剪圆形（导出 256x256 透明底 PNG） */
private fun cropCircle(src: Bitmap, vw: Int, vh: Int, scale: Float, offset: Offset): Bitmap {
    val outSize = 256
    val out = Bitmap.createBitmap(outSize, outSize, Bitmap.Config.ARGB_8888)
    val c = Canvas(out)
    val cover = max(vw.toFloat() / src.width, vh.toFloat() / src.height)
    val dw = src.width * cover * scale
    val dh = src.height * cover * scale
    val imgLeft = (vw - dw) / 2f + offset.x
    val imgTop = (vh - dh) / 2f + offset.y
    val ratioW = src.width.toFloat() / dw
    val ratioH = src.height.toFloat() / dh
    val cx = (vw / 2f - imgLeft) * ratioW
    val cy = (vh / 2f - imgTop) * ratioH
    val r = (vw / 2f) * ratioW
    val srcRect = android.graphics.RectF(
        max(0f, cx - r), max(0f, cy - r),
        min(src.width.toFloat(), cx + r), min(src.height.toFloat(), cy + r)
    )
    val clipPath = android.graphics.Path().apply {
        addCircle(outSize / 2f, outSize / 2f, outSize / 2f, android.graphics.Path.Direction.CW)
    }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    c.save()
    c.clipPath(clipPath)
    c.drawBitmap(
        src,
        android.graphics.Rect(
            max(0, Math.floor(srcRect.left.toDouble()).toInt()),
            max(0, Math.floor(srcRect.top.toDouble()).toInt()),
            min(src.width, Math.ceil(srcRect.right.toDouble()).toInt()),
            min(src.height, Math.ceil(srcRect.bottom.toDouble()).toInt())
        ),
        android.graphics.Rect(0, 0, outSize, outSize),
        paint
    )
    c.restore()
    return out
}
