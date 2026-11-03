/*
 * Lucide 图标集（内置）—— 图标数据来自 https://lucide.dev （ISC License）。
 *
 * ★ 全 App 唯一的图标来源：界面里不允许再出现 emoji / 几何符号当图标用。
 * ★ 本文件由 D:\AND\.workbuddy\tmp\svg2kotlin.py 自动生成（47 个图标），
 *   要加新图标：把 svg 放进 .workbuddy\tmp\lucide\ 并跑脚本，不要手改本文件。
 *
 * 用法：Icon(Lucide.Bell, contentDescription = null, tint = 主题色)
 */

package com.wqmtmbdi.mbcc.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Lucide 规格：24 视窗 / 线宽 2 / 圆头圆角（描边色交给上层 tint） */
private fun lucideIcon(name: String, vararg paths: String): ImageVector =
    ImageVector.Builder(
        name = "lucide-" + name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        paths.forEach { d ->
            addPath(
                pathData = addPathNodes(d),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

object Lucide {

    /** bell */
    val Bell: ImageVector by lazy { lucideIcon("bell", "M10.268 21a2 2 0 0 0 3.464 0", "M3.262 15.326A1 1 0 0 0 4 17h16a1 1 0 0 0 .74-1.673C19.41 13.956 18 12.499 18 8A6 6 0 0 0 6 8c0 4.499-1.411 5.956-2.738 7.326") }
    /** x */
    val X: ImageVector by lazy { lucideIcon("x", "M18 6 6 18", "m6 6 12 12") }
    /** chevron-right */
    val ChevronRight: ImageVector by lazy { lucideIcon("chevron-right", "m9 18 6-6-6-6") }
    /** arrow-left */
    val ArrowLeft: ImageVector by lazy { lucideIcon("arrow-left", "m12 19-7-7 7-7", "M19 12H5") }
    /** arrow-right */
    val ArrowRight: ImageVector by lazy { lucideIcon("arrow-right", "M5 12h14", "m12 5 7 7-7 7") }
    /** clipboard-list */
    val ClipboardList: ImageVector by lazy { lucideIcon("clipboard-list", "M 9,2 H 15 A 1,1 0 0 1 16,3 V 5 A 1,1 0 0 1 15,6 H 9 A 1,1 0 0 1 8,5 V 3 A 1,1 0 0 1 9,2 Z", "M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2", "M12 11h4", "M12 16h4", "M8 11h.01", "M8 16h.01") }
    /** book-open */
    val BookOpen: ImageVector by lazy { lucideIcon("book-open", "M12 5v16", "M20.001 19A2 2 0 0022 17V5a2 2 0 00-1.999-2L16 3.002A5 5 0 0012 5a5 5 0 00-4-2H4a2 2 0 00-2 2v12a2 2 0 001.999 2H8a5 5 0 014 2 5 5 0 014-2z") }
    /** user-round */
    val UserRound: ImageVector by lazy { lucideIcon("user-round", "M 7,8 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0", "M20 21a8 8 0 0 0-16 0") }
    /** sparkle */
    val Sparkle: ImageVector by lazy { lucideIcon("sparkle", "M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z") }
    /** inbox */
    val Inbox: ImageVector by lazy { lucideIcon("inbox", "M 22,12 L 16,12 L 14,15 L 10,15 L 8,12 L 2,12", "M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z") }
    /** check */
    val Check: ImageVector by lazy { lucideIcon("check", "M20 6 9 17l-5-5") }
    /** palette */
    val Palette: ImageVector by lazy { lucideIcon("palette", "M12 22a1 1 0 0 1 0-20 10 9 0 0 1 10 9 5 5 0 0 1-5 5h-2.25a1.75 1.75 0 0 0-1.4 2.8l.3.4a1.75 1.75 0 0 1-1.4 2.8z", "M 13,6.5 a 0.5,0.5 0 1,0 1,0 a 0.5,0.5 0 1,0 -1,0", "M 17,10.5 a 0.5,0.5 0 1,0 1,0 a 0.5,0.5 0 1,0 -1,0", "M 6,12.5 a 0.5,0.5 0 1,0 1,0 a 0.5,0.5 0 1,0 -1,0", "M 8,7.5 a 0.5,0.5 0 1,0 1,0 a 0.5,0.5 0 1,0 -1,0") }
    /** info */
    val Info: ImageVector by lazy { lucideIcon("info", "M 2,12 a 10,10 0 1,0 20,0 a 10,10 0 1,0 -20,0", "M12 16v-4", "M12 8h.01") }
    /** shield-check */
    val ShieldCheck: ImageVector by lazy { lucideIcon("shield-check", "M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z", "m9 12 2 2 4-4") }
    /** circle-help */
    val CircleHelp: ImageVector by lazy { lucideIcon("circle-help", "M 2,12 a 10,10 0 1,0 20,0 a 10,10 0 1,0 -20,0", "M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3", "M12 17h.01") }
    /** history */
    val History: ImageVector by lazy { lucideIcon("history", "M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8", "M3 3v5h5", "M12 7v5l4 2") }
    /** file-text */
    val FileText: ImageVector by lazy { lucideIcon("file-text", "M6 22a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h8a2.4 2.4 0 0 1 1.704.706l3.588 3.588A2.4 2.4 0 0 1 20 8v12a2 2 0 0 1-2 2z", "M14 2v5a1 1 0 0 0 1 1h5", "M10 9H8", "M16 13H8", "M16 17H8") }
    /** image */
    val Image: ImageVector by lazy { lucideIcon("image", "M 5,3 H 19 A 2,2 0 0 1 21,5 V 19 A 2,2 0 0 1 19,21 H 5 A 2,2 0 0 1 3,19 V 5 A 2,2 0 0 1 5,3 Z", "M 7,9 a 2,2 0 1,0 4,0 a 2,2 0 1,0 -4,0", "m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21") }
    /** radar */
    val Radar: ImageVector by lazy { lucideIcon("radar", "M19.07 4.93A10 10 0 0 0 6.99 3.34", "M4 6h.01", "M2.29 9.62A10 10 0 1 0 21.31 8.35", "M16.24 7.76A6 6 0 1 0 8.23 16.67", "M12 18h.01", "M17.99 11.66A6 6 0 0 1 15.77 16.67", "M 10,12 a 2,2 0 1,0 4,0 a 2,2 0 1,0 -4,0", "m13.41 10.59 5.66-5.66") }
    /** activity */
    val Activity: ImageVector by lazy { lucideIcon("activity", "M22 12h-2.48a2 2 0 0 0-1.93 1.46l-2.35 8.36a.25.25 0 0 1-.48 0L9.24 2.18a.25.25 0 0 0-.48 0l-2.35 8.36A2 2 0 0 1 4.49 12H2") }
    /** link */
    val Link: ImageVector by lazy { lucideIcon("link", "M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71", "M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71") }
    /** scan-line */
    val ScanLine: ImageVector by lazy { lucideIcon("scan-line", "M3 7V5a2 2 0 0 1 2-2h2", "M17 3h2a2 2 0 0 1 2 2v2", "M21 17v2a2 2 0 0 1-2 2h-2", "M7 21H5a2 2 0 0 1-2-2v-2", "M7 12h10") }
    /** clipboard-check */
    val ClipboardCheck: ImageVector by lazy { lucideIcon("clipboard-check", "M 9,2 H 15 A 1,1 0 0 1 16,3 V 5 A 1,1 0 0 1 15,6 H 9 A 1,1 0 0 1 8,5 V 3 A 1,1 0 0 1 9,2 Z", "M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2", "m9 14 2 2 4-4") }
    /** target */
    val Target: ImageVector by lazy { lucideIcon("target", "M 2,12 a 10,10 0 1,0 20,0 a 10,10 0 1,0 -20,0", "M 6,12 a 6,6 0 1,0 12,0 a 6,6 0 1,0 -12,0", "M 10,12 a 2,2 0 1,0 4,0 a 2,2 0 1,0 -4,0") }
    /** microscope */
    val Microscope: ImageVector by lazy { lucideIcon("microscope", "M6 18h8", "M3 22h18", "M14 22a7 7 0 1 0 0-14h-1", "M9 14h2", "M9 12a2 2 0 0 1-2-2V6h6v4a2 2 0 0 1-2 2Z", "M12 6V3a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v3") }
    /** waves */
    val Waves: ImageVector by lazy { lucideIcon("waves", "M2 12q2.5 2 5 0t5 0 5 0 5 0", "M2 19q2.5 2 5 0t5 0 5 0 5 0", "M2 5q2.5 2 5 0t5 0 5 0 5 0") }
    /** flame */
    val Flame: ImageVector by lazy { lucideIcon("flame", "M12 3q1 4 4 6.5t3 5.5a1 1 0 0 1-14 0 5 5 0 0 1 1-3 1 1 0 0 0 5 0c0-2-1.5-3-1.5-5q0-2 2.5-4") }
    /** grid-3x3 */
    val Grid3x3: ImageVector by lazy { lucideIcon("grid-3x3", "M 5,3 H 19 A 2,2 0 0 1 21,5 V 19 A 2,2 0 0 1 19,21 H 5 A 2,2 0 0 1 3,19 V 5 A 2,2 0 0 1 5,3 Z", "M3 9h18", "M3 15h18", "M9 3v18", "M15 3v18") }
    /** swords */
    val Swords: ImageVector by lazy { lucideIcon("swords", "m13 19 6-6", "M14.5 17.5 3.586 6.586A2 2 0 013 5.172V3h2.172a2 2 0 011.414.586L17.5 14.5", "m14.828 6.172 2.586-2.586A2 2 0 0118.828 3H21v2.172a2 2 0 01-.586 1.414l-2.586 2.586", "m16 16 4 4", "m19 21 2-2", "m5 14 4 4", "m5 21-2-2", "M7.5 16.5 4 20") }
    /** cat */
    val Cat: ImageVector by lazy { lucideIcon("cat", "M12 5c.67 0 1.35.09 2 .26 1.78-2 5.03-2.84 6.42-2.26 1.4.58-.42 7-.42 7 .57 1.07 1 2.24 1 3.44C21 17.9 16.97 21 12 21s-9-3-9-7.56c0-1.25.5-2.4 1-3.44 0 0-1.89-6.42-.5-7 1.39-.58 4.72.23 6.5 2.23A9.04 9.04 0 0 1 12 5Z", "M8 14v.5", "M16 14v.5", "M11.25 16.25h1.5L12 17l-.75-.75Z") }
    /** moon */
    val Moon: ImageVector by lazy { lucideIcon("moon", "M20.985 12.486a9 9 0 1 1-9.473-9.472c.405-.022.617.46.402.803a6 6 0 0 0 8.268 8.268c.344-.215.825-.004.803.401") }
    /** venetian-mask */
    val VenetianMask: ImageVector by lazy { lucideIcon("venetian-mask", "M18 11c-1.5 0-2.5.5-3 2", "M4 6a2 2 0 0 0-2 2v4a5 5 0 0 0 5 5 8 8 0 0 1 5 2 8 8 0 0 1 5-2 5 5 0 0 0 5-5V8a2 2 0 0 0-2-2h-3a8 8 0 0 0-5 2 8 8 0 0 0-5-2z", "M6 11c1.5 0 2.5.5 3 2") }
    /** crown */
    val Crown: ImageVector by lazy { lucideIcon("crown", "M11.562 3.266a.5.5 0 0 1 .876 0L15.39 8.87a1 1 0 0 0 1.516.294L21.183 5.5a.5.5 0 0 1 .798.519l-2.834 10.246a1 1 0 0 1-.956.734H5.81a1 1 0 0 1-.957-.734L2.02 6.02a.5.5 0 0 1 .798-.519l4.276 3.664a1 1 0 0 0 1.516-.294z", "M5 21h14") }
    /** network */
    val Network: ImageVector by lazy { lucideIcon("network", "M 17,16 H 21 A 1,1 0 0 1 22,17 V 21 A 1,1 0 0 1 21,22 H 17 A 1,1 0 0 1 16,21 V 17 A 1,1 0 0 1 17,16 Z", "M 3,16 H 7 A 1,1 0 0 1 8,17 V 21 A 1,1 0 0 1 7,22 H 3 A 1,1 0 0 1 2,21 V 17 A 1,1 0 0 1 3,16 Z", "M 10,2 H 14 A 1,1 0 0 1 15,3 V 7 A 1,1 0 0 1 14,8 H 10 A 1,1 0 0 1 9,7 V 3 A 1,1 0 0 1 10,2 Z", "M5 16v-3a1 1 0 0 1 1-1h12a1 1 0 0 1 1 1v3", "M12 12V8") }
    /** graduation-cap */
    val GraduationCap: ImageVector by lazy { lucideIcon("graduation-cap", "M21.42 10.922a1 1 0 0 0-.019-1.838L12.83 5.18a2 2 0 0 0-1.66 0L2.6 9.08a1 1 0 0 0 0 1.832l8.57 3.908a2 2 0 0 0 1.66 0z", "M22 10v6", "M6 12.5V16a6 3 0 0 0 12 0v-3.5") }
    /** trophy */
    val Trophy: ImageVector by lazy { lucideIcon("trophy", "M10 14.66V17a1 1 0 0 1-1 1 2 2 0 0 0-2 2v2", "M14 14.66V17a1 1 0 0 0 1 1 2 2 0 0 1 2 2v2", "M17.916 10H19.5A2.5 2.5 0 0 0 22 7.5V5a1 1 0 0 0-1-1h-3", "M4 22h16", "M6 9a6 6 0 0 0 12 0V3a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1z", "M6.084 10H4.5A2.5 2.5 0 0 1 2 7.5V5a1 1 0 0 1 1-1h3") }
    /** eye */
    val Eye: ImageVector by lazy { lucideIcon("eye", "M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0", "M 9,12 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0") }
    /** radio */
    val Radio: ImageVector by lazy { lucideIcon("radio", "M16.247 7.761a6 6 0 0 1 0 8.478", "M19.075 4.933a10 10 0 0 1 0 14.134", "M4.925 19.067a10 10 0 0 1 0-14.134", "M7.753 16.239a6 6 0 0 1 0-8.478", "M 10,12 a 2,2 0 1,0 4,0 a 2,2 0 1,0 -4,0") }
    /** infinity */
    val Infinity: ImageVector by lazy { lucideIcon("infinity", "M6 16c5 0 7-8 12-8a4 4 0 0 1 0 8c-5 0-7-8-12-8a4 4 0 1 0 0 8") }
    /** brush */
    val Brush: ImageVector by lazy { lucideIcon("brush", "m11 10 3 3", "M6.5 21A3.5 3.5 0 1 0 3 17.5a2.62 2.62 0 0 1-.708 1.792A1 1 0 0 0 3 21z", "M9.969 17.031 21.378 5.624a1 1 0 0 0-3.002-3.002L6.967 14.031") }
    /** zap */
    val Zap: ImageVector by lazy { lucideIcon("zap", "M15.914 4a1.5 1.5 0 00-2.474-1.561l-9 9A1.5 1.5 0 005.5 14h4.002a.5.5 0 01.471.666L8.086 20a1.5 1.5 0 002.475 1.56l9-9A1.5 1.5 0 0018.5 10h-3.997a.5.5 0 01-.472-.667z") }
    /** download */
    val Download: ImageVector by lazy { lucideIcon("download", "M12 15V3", "M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4", "m7 10 5 5 5-5") }
    /** share-2 */
    val Share2: ImageVector by lazy { lucideIcon("share-2", "M 15,5 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0", "M 3,12 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0", "M 15,19 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0", "M 8.59,13.51 L 15.42,17.49", "M 15.41,6.51 L 8.59,10.49") }
    /** triangle-alert */
    val TriangleAlert: ImageVector by lazy { lucideIcon("triangle-alert", "m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3", "M12 9v4", "M12 17h.01") }
    /** camera */
    val Camera: ImageVector by lazy { lucideIcon("camera", "M13.997 4a2 2 0 0 1 1.76 1.05l.486.9A2 2 0 0 0 18.003 7H20a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h1.997a2 2 0 0 0 1.759-1.048l.489-.904A2 2 0 0 1 10.004 4z", "M 9,13 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0") }
    /** settings */
    val Settings: ImageVector by lazy { lucideIcon("settings", "M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z", "M 9,12 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0") }
    /** package */
    val Package: ImageVector by lazy { lucideIcon("package", "M11 21.73a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73z", "M12 22V12", "m3.3 7 7.703 4.734a2 2 0 0 0 1.994 0L20.7 7", "m7.5 4.27 9 5.15") }

    /** 名字 → 图标（成就等数据里存的是图标名）；找不到时回落到 Sparkle */
    private val MAP: Map<String, ImageVector> by lazy {
        mapOf(
            "bell" to Bell,
            "x" to X,
            "chevron-right" to ChevronRight,
            "arrow-left" to ArrowLeft,
            "arrow-right" to ArrowRight,
            "clipboard-list" to ClipboardList,
            "book-open" to BookOpen,
            "user-round" to UserRound,
            "sparkle" to Sparkle,
            "inbox" to Inbox,
            "check" to Check,
            "palette" to Palette,
            "info" to Info,
            "shield-check" to ShieldCheck,
            "circle-help" to CircleHelp,
            "history" to History,
            "file-text" to FileText,
            "image" to Image,
            "radar" to Radar,
            "activity" to Activity,
            "link" to Link,
            "scan-line" to ScanLine,
            "clipboard-check" to ClipboardCheck,
            "target" to Target,
            "microscope" to Microscope,
            "waves" to Waves,
            "flame" to Flame,
            "grid-3x3" to Grid3x3,
            "swords" to Swords,
            "cat" to Cat,
            "moon" to Moon,
            "venetian-mask" to VenetianMask,
            "crown" to Crown,
            "network" to Network,
            "graduation-cap" to GraduationCap,
            "trophy" to Trophy,
            "eye" to Eye,
            "radio" to Radio,
            "infinity" to Infinity,
            "brush" to Brush,
            "zap" to Zap,
            "download" to Download,
            "share-2" to Share2,
            "triangle-alert" to TriangleAlert,
            "camera" to Camera,
            "settings" to Settings,
            "package" to Package,
        )
    }

    fun of(name: String): ImageVector = MAP[name] ?: Sparkle
}
