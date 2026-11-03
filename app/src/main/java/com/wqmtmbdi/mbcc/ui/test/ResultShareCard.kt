package com.wqmtmbdi.mbcc.ui.test

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.wqmtmbdi.mbcc.ThemeColors
import com.wqmtmbdi.mbcc.data.FACTIONS_INFO
import com.wqmtmbdi.mbcc.data.STATS_LABELS7
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 结果铭牌 → 分享图（方案 5.4 ⑤「保存结果图」，2026-10-01 用户拍板「做」）。
 *
 * 全部用平台绘制 API 离屏绘制（不引入第三方库、不依赖 Compose 渲染管线），
 * 保存走 MediaStore（minSdk 29 起无需任何权限），分享直接把相册 Uri 交给系统分享面板。
 */
object ResultShareCard {
    private const val W = 1080
    /** ★ 用户要求：保存图附上雷达图 —— 海报从 1620 加高到 2140，容纳米牌 + 七维对比雷达 */
    private const val H = 2140

    /** 相册里的文件名（不含扩展名） */
    fun fileName(charName: String): String {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "无期人格鉴定_${charName}_$ts"
    }

    /** 离屏绘制整张分享图（1080×1620，白纸 + 铭牌 + 四字钢印 + 百科简介） */
    fun render(r: MbccResult, t: ThemeColors, playerName: String?): Bitmap {
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val char = r.char
        val bg = argb(t.bg)
        val cardC = argb(t.card)
        val gold = argb(t.gold)
        val purple = argb(t.purple)
        val textC = argb(t.text)
        val text2 = argb(t.text2)
        val text3 = argb(t.text3)
        val brd = argb(t.brd)

        // —— 纸面底 + 两团氛围光（与 App 环境层同一语言）——
        c.drawColor(bg)
        c.drawRect(
            0f, 0f, W.toFloat(), H.toFloat(),
            shaded(
                RadialGradient(
                    W * 0.12f, H * 0.04f, W * 0.85f,
                    withAlpha(gold, 0.10f), android.graphics.Color.TRANSPARENT, Shader.TileMode.CLAMP
                )
            )
        )
        c.drawRect(
            0f, 0f, W.toFloat(), H.toFloat(),
            shaded(
                RadialGradient(
                    W * 0.95f, H * 0.38f, W * 0.80f,
                    withAlpha(purple, 0.08f), android.graphics.Color.TRANSPARENT, Shader.TileMode.CLAMP
                )
            )
        )

        // —— 页眉 ——
        c.drawText(
            "无期：人格鉴定 · 人格档案", W / 2f, 128f,
            text(gold, 42f, bold = true).apply {
                textAlign = Paint.Align.CENTER
                letterSpacing = 0.16f
            }
        )
        c.drawRect(
            W / 2f - 130f, 172f, W / 2f + 130f, 175f, fill(withAlpha(gold, 0.5f))
        )

        // —— 铭牌主卡 ——
        val left = 64f
        val top = 250f
        val right = W - 64f
        val bottom = H - 210f
        val cardRect = RectF(left, top, right, bottom)
        c.drawRoundRect(cardRect, 56f, 56f, fill(cardC))
        c.drawRoundRect(cardRect, 56f, 56f, stroke(withAlpha(gold, 0.30f), 3f))
        c.drawRoundRect(RectF(left + 7f, top + 7f, right - 7f, bottom - 7f), 50f, 50f, stroke(withAlpha(brd, 0.95f), 2f))

        val x0 = left + 72f
        val x1 = right - 72f

        // 结果行（左：档位；右：玩家昵称）
        c.drawText(
            "鉴定结果 · ${getTierLabel(r.tier)}", x0, 356f,
            text(text3, 30f).apply { letterSpacing = 0.06f }
        )
        if (playerName != null) {
            c.drawText(playerName, x1, 356f, text(text3, 30f).apply { textAlign = Paint.Align.RIGHT })
        }

        // 匹配度环
        val rcx = x0 + 152f
        val rcy = 566f
        val rRad = 122f
        val arcRect = RectF(rcx - rRad, rcy - rRad, rcx + rRad, rcy + rRad)
        c.drawArc(arcRect, 0f, 360f, false, stroke(brd, 26f))
        val prob = r.topProb.coerceIn(0.0, 1.0).toFloat()
        c.drawArc(arcRect, -90f, 360f * prob, false, stroke(gold, 26f))
        c.drawText(
            "${(r.topProb * 100).roundToInt()}", rcx, rcy + 34f,
            text(gold, 92f, bold = true).apply { textAlign = Paint.Align.CENTER }
        )
        c.drawText("匹配度", rcx, rcy + 88f, text(text3, 26f).apply { textAlign = Paint.Align.CENTER })

        // 角色名 + 狂厄性质
        c.drawText(
            char.name, x0 + 340f, 540f,
            text(gold, 96f, bold = true).apply { letterSpacing = 0.05f }
        )
        r.faction?.let { fid -> FACTIONS_INFO[fid]?.name }?.let { fname ->
            val chipText = "狂厄性质 · $fname"
            val chipPaint = text(purple, 30f, bold = true)
            val tw = chipPaint.measureText(chipText)
            val chipRect = RectF(x0 + 344f, 572f, x0 + 344f + tw + 60f, 572f + 62f)
            c.drawRoundRect(chipRect, 31f, 31f, fill(withAlpha(purple, 0.14f)))
            c.drawText(chipText, chipRect.left + 30f, chipRect.top + 42f, chipPaint)
        }

        // 分隔线
        c.drawRect(x0, 726f, x1, 728f, fill(withAlpha(brd, 0.95f)))

        // 四字钢印（代码 + 释义）
        val boxTop = 764f
        val boxH = 112f
        val gap = 24f
        val boxW = (x1 - x0 - gap * 3f) / 4f
        char.meanings.take(4).forEachIndexed { i, m ->
            val bx = x0 + i * (boxW + gap)
            val box = RectF(bx, boxTop, bx + boxW, boxTop + boxH)
            c.drawRoundRect(box, 18f, 18f, fill(withAlpha(gold, 0.07f)))
            c.drawRoundRect(box, 18f, 18f, stroke(withAlpha(gold, 0.35f), 2f))
            c.drawText(
                m.l, box.centerX(), box.centerY() + 20f,
                text(gold, 56f, bold = true).apply {
                    textAlign = Paint.Align.CENTER
                    letterSpacing = 0.08f
                }
            )
            c.drawText(
                m.m, box.centerX(), boxTop + boxH + 42f,
                text(text2, 26f).apply { textAlign = Paint.Align.CENTER }
            )
        }

        // 对比结论（一行；★ 用户要求：去掉「差 X.X」数字）
        val diffs = List(7) { i -> r.pScaled.getOrElse(i) { 0.0 } - char.stats.getOrElse(i) { 0 }.toDouble() }
        val closest = diffs.indices.minByOrNull { abs(diffs[it]) } ?: 0
        val farthest = diffs.indices.maxByOrNull { abs(diffs[it]) } ?: 0
        c.drawText(
            "最像维度：${STATS_LABELS7[closest]}　差异最大：${STATS_LABELS7[farthest]}",
            x0, 1000f, text(text2, 30f)
        )

        // 百科简介（最多 3 行；为下方的雷达图腾出空间）
        val descPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textC
            textSize = 34f
        }
        val layout = StaticLayout.Builder
            .obtain(char.desc, 0, char.desc.length, descPaint, (x1 - x0).toInt())
            .setLineSpacing(12f, 1.0f)
            .setMaxLines(3)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
        c.save()
        c.translate(x0, 1040f)
        layout.draw(c)
        c.restore()

        // —— 七维对比雷达（★ 用户要求：保存图附上雷达图；与结果页雷达同一语言）——
        drawRadar(
            c = c, cx = W / 2f, cy = 1520f, radius = 240f,
            player = r.pScaled, charVals = char.stats,
            charColor = argb(char.avatarColor), gold = gold, brd = brd, text3 = text3
        )

        // 卡内页脚
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        c.drawText(
            "无期：人格鉴定 · $date", W / 2f, bottom - 56f,
            text(text3, 26f).apply { textAlign = Paint.Align.CENTER }
        )

        return bmp
    }

    /** 写入系统相册（Pictures/无期人格鉴定）；失败返回 null，不抛异常 */
    fun save(context: Context, bitmap: Bitmap, baseName: String): Uri? {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$baseName.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/无期人格鉴定")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = runCatching {
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }.getOrNull() ?: return null
        val ok = runCatching {
            resolver.openOutputStream(uri)?.use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) } ?: false
        }.getOrDefault(false)
        if (!ok) {
            runCatching { resolver.delete(uri, null, null) }
            return null
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        runCatching { resolver.update(uri, values, null, null) }
        return uri
    }

    /** 调起系统分享面板（把相册 Uri 直接给出去） */
    fun share(context: Context, uri: Uri) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { context.startActivity(Intent.createChooser(send, "分享人格档案")) }
    }

    /**
     * 七维对比雷达（保存图用）：5 环网格 + 轴线 + 角色曲线（实线，角色色）+ 你（金色虚线）
     * + 顶点亮点 + 维度标签；数值与界面同尺子（各维 0~10）。
     */
    private fun drawRadar(
        c: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        player: List<Double>,
        charVals: List<Number>,
        charColor: Int,
        gold: Int,
        brd: Int,
        text3: Int
    ) {
        val n = 7
        val angles = FloatArray(n) { (-Math.PI / 2.0 + it * 2.0 * Math.PI / n).toFloat() }
        fun pt(i: Int, v: Float): PointF =
            PointF(cx + radius * v * cos(angles[i]), cy + radius * v * sin(angles[i]))

        // 网格（5 环，外圈加重）
        for (ring in 1..5) {
            val p = Path()
            for (i in 0 until n) {
                val o = pt(i, ring / 5f)
                if (i == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
            }
            p.close()
            val mult = if (ring == 5) 0.95f else if (ring % 2 == 1) 0.55f else 0.30f
            c.drawPath(p, stroke(withAlpha(brd, mult), if (ring == 5) 3f else 2f))
        }
        // 轴线 + 维度标签
        val labelPaint = text(text3, 28f)
        for (i in 0 until n) {
            val end = pt(i, 1f)
            c.drawLine(cx, cy, end.x, end.y, stroke(withAlpha(brd, 0.55f), 2f))
            val lx = cx + (radius + 66f) * cos(angles[i])
            val ly = cy + (radius + 66f) * sin(angles[i])
            labelPaint.textAlign = when {
                cos(angles[i]) > 0.34f -> Paint.Align.LEFT
                cos(angles[i]) < -0.34f -> Paint.Align.RIGHT
                else -> Paint.Align.CENTER
            }
            c.drawText(STATS_LABELS7[i], lx, ly + 10f, labelPaint)
        }
        // 两条曲线（同一把尺子：值 / 10）
        fun poly(vals: List<Float>): Path {
            val p = Path()
            for (i in 0 until n) {
                val o = pt(i, vals.getOrElse(i) { 0f })
                if (i == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
            }
            p.close()
            return p
        }
        val charF = List(n) { i -> (charVals.getOrElse(i) { 0 }.toDouble() / 10.0).coerceIn(0.0, 1.0).toFloat() }
        val playerF = List(n) { i -> (player.getOrElse(i) { 0.0 } / 10.0).coerceIn(0.0, 1.0).toFloat() }
        val charPath = poly(charF)
        c.drawPath(charPath, fill(withAlpha(charColor, 0.18f)))
        c.drawPath(charPath, stroke(charColor, 4.5f))
        val playerPath = poly(playerF)
        c.drawPath(playerPath, fill(withAlpha(gold, 0.10f)))
        val dash = stroke(gold, 4f).apply { pathEffect = DashPathEffect(floatArrayOf(18f, 14f), 0f) }
        c.drawPath(playerPath, dash)
        for (i in 0 until n) {
            val o = pt(i, playerF[i])
            c.drawCircle(o.x, o.y, 6.5f, fill(gold))
        }
    }

    // —— 绘制小工具 ——

    private fun argb(hex: String): Int = android.graphics.Color.parseColor(hex)

    private fun withAlpha(color: Int, a: Float): Int =
        (color and 0x00FFFFFF) or ((a.coerceIn(0f, 1f) * 255).toInt() shl 24)

    private fun text(color: Int, size: Float, bold: Boolean = false): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

    private fun fill(color: Int): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }

    private fun shaded(shader: Shader): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            this.shader = shader
        }

    private fun stroke(color: Int, width: Float): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = width
            strokeCap = Paint.Cap.ROUND
        }
}