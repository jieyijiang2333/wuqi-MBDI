package com.wqmtmbdi.mbcc

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** 十六进制颜色字符串转 Compose Color */
fun colorFromHex(hex: String): Color = Color(android.graphics.Color.parseColor(hex))

/** 单个主题的完整配色（对齐 HTML :root 与 [data-theme] 的 CSS 变量） */
data class ThemeColors(
    val id: String,
    val name: String,
    val desc: String,
    val bg: String,        // --bg
    val card: String,      // --card（rgba 已转 ARGB hex）
    val gold: String,      // --gold（主强调色，各配色主题里不一定是金色）
    val goldDim: String,   // --gold-dim
    val purple: String,    // --purple（辅助色）
    val purpleDim: String, // --purple-dim
    val text: String,      // --text
    val text2: String,     // --text2
    val text3: String,     // --text3
    val brd: String,       // --brd（rgba 已转 ARGB hex）
    val swatches: List<String>
)

/**
 * 主题表（v1.5.1：6 套配色 × 浅色 / 深色两版）。
 *
 * ★ 设计口径（改配色前先看）：
 *  - 每套配色 = 一个 **色相家族**（[Family]），家族内 light / dark 两版同色相、不同明度；
 *  - 字段名沿用历史：`gold` = 主强调色、`purple` = 辅助色 —— 不一定是金色 / 紫色；
 *  - 深色版：底近黑、面板比底亮一阶、文字浅、靠描边分层（仪器面板）；
 *    浅色版：底米白、面板纯白、文字深、描边浅灰（纸面档案）；
 *  - 对比度：正文 ≥ 7:1、次要 ≥ 4.5:1、辅助 ≥ 4:1；界面里**不许写死颜色**，
 *    一律从 `t.bg / t.card / t.gold / t.purple / t.text* / t.brd` 取。
 *  - 老存档里的旧主题 id（zero / default / ivory…）由 [resolve] 自动回落到第一套。
 */
object MBDIThemes {

    /** 一套配色（浅色 + 深色两版） */
    data class Family(
        val id: String,
        val name: String,
        val desc: String,
        val light: ThemeColors,
        val dark: ThemeColors
    )

    private fun theme(
        id: String, name: String, desc: String,
        bg: String, card: String,
        accent: String, accentDim: String,
        aux: String, auxDim: String,
        text: String, text2: String, text3: String, brd: String
    ) = ThemeColors(
        id = id, name = name, desc = desc,
        bg = bg, card = card,
        gold = accent, goldDim = accentDim,
        purple = aux, purpleDim = auxDim,
        text = text, text2 = text2, text3 = text3, brd = brd,
        swatches = listOf(accent, aux, bg, text)
    )

    val FAMILIES: List<Family> = listOf(
        // ① 零镜仪器（默认）：暖金 + 雾紫
        Family(
            "zero", "零镜仪器", "暖金 · 雾紫",
            light = theme(
                "zero_light", "零镜仪器", "暖金 · 雾紫",
                bg = "#F4F2EC", card = "#FFFFFF",
                accent = "#8A6520", accentDim = "#6E5018",
                aux = "#5E5799", auxDim = "#7A73AD",
                text = "#1B1D22", text2 = "#4A505C", text3 = "#767D8A", brd = "#E0DDD4"
            ),
            dark = theme(
                "zero_dark", "零镜仪器", "暖金 · 雾紫",
                bg = "#0D1015", card = "#161A21",
                accent = "#D9A93F", accentDim = "#A07C24",
                aux = "#8E86C8", auxDim = "#6A6499",
                text = "#ECEEF3", text2 = "#B4BBC7", text3 = "#7E8795", brd = "#262C36"
            )
        ),
        // ② 白夜档案：钢青 + 冷白
        Family(
            "baiye", "白夜档案", "钢青 · 冷白",
            light = theme(
                "baiye_light", "白夜档案", "钢青 · 冷白",
                bg = "#F2F5F7", card = "#FFFFFF",
                accent = "#2E6B7A", accentDim = "#1F4E5A",
                aux = "#5A6E8C", auxDim = "#7A8CA6",
                text = "#121A20", text2 = "#3E4A54", text3 = "#6E7A85", brd = "#DCE3E8"
            ),
            dark = theme(
                "baiye_dark", "白夜档案", "钢青 · 冷白",
                bg = "#0A1216", card = "#121C22",
                accent = "#5FB6C9", accentDim = "#3F8A9A",
                aux = "#8CA6C4", auxDim = "#647E96",
                text = "#E8F0F4", text2 = "#AEBEC8", text3 = "#76868F", brd = "#1E2C33"
            )
        ),
        // ③ 赤棘：赤陶红 + 铜金
        Family(
            "chiji", "赤棘", "赤陶 · 铜金",
            light = theme(
                "chiji_light", "赤棘", "赤陶 · 铜金",
                bg = "#F7F2F0", card = "#FFFFFF",
                accent = "#9A3324", accentDim = "#7A2619",
                aux = "#8A5A2A", auxDim = "#A87A4A",
                text = "#1E1412", text2 = "#4E3A34", text3 = "#85706A", brd = "#E6DAD4"
            ),
            dark = theme(
                "chiji_dark", "赤棘", "赤陶 · 铜金",
                bg = "#140C0A", card = "#1E1411",
                accent = "#E0705A", accentDim = "#A64A38",
                aux = "#C99A5E", auxDim = "#96703F",
                text = "#F4E9E4", text2 = "#C4B0A8", text3 = "#8A756D", brd = "#2E201B"
            )
        ),
        // ④ 苔庭：苔绿 + 米金
        Family(
            "taitin", "苔庭", "苔绿 · 米金",
            light = theme(
                "taitin_light", "苔庭", "苔绿 · 米金",
                bg = "#F3F5EF", card = "#FFFFFF",
                accent = "#4E6B32", accentDim = "#3A5224",
                aux = "#8A7A3A", auxDim = "#A89A5E",
                text = "#161C10", text2 = "#404A36", text3 = "#767E6E", brd = "#DEE3D6"
            ),
            dark = theme(
                "taitin_dark", "苔庭", "苔绿 · 米金",
                bg = "#0B120C", card = "#131C14",
                accent = "#8FB860", accentDim = "#6A8F42",
                aux = "#C9B86E", auxDim = "#9A8C4E",
                text = "#ECF2E4", text2 = "#B2C0A6", text3 = "#7C8872", brd = "#202B21"
            )
        ),
        // ⑤ 霜原：钴蓝 + 银蓝
        Family(
            "shuangyuan", "霜原", "钴蓝 · 银白",
            light = theme(
                "shuangyuan_light", "霜原", "钴蓝 · 银白",
                bg = "#F1F5FA", card = "#FFFFFF",
                accent = "#2F5E9A", accentDim = "#234878",
                aux = "#6A7FA8", auxDim = "#8A9CBE",
                text = "#0F1620", text2 = "#3A4656", text3 = "#6E7A8A", brd = "#D9E1EC"
            ),
            dark = theme(
                "shuangyuan_dark", "霜原", "钴蓝 · 银白",
                bg = "#080E16", card = "#101A26",
                accent = "#6FA8E0", accentDim = "#4A7EB0",
                aux = "#A8BADC", auxDim = "#7A8CB0",
                text = "#E8F0FA", text2 = "#ACBACD", text3 = "#748296", brd = "#1B2836"
            )
        ),
        // ⑥ 薄暮：暮紫 + 玫瑰金
        Family(
            "bomu", "薄暮", "暮紫 · 玫瑰金",
            light = theme(
                "bomu_light", "薄暮", "暮紫 · 玫瑰金",
                bg = "#F6F2F6", card = "#FFFFFF",
                accent = "#7A3E70", accentDim = "#5E2C56",
                aux = "#A06A4A", auxDim = "#BE8A66",
                text = "#1C121C", text2 = "#4A3A4A", text3 = "#7E6E7E", brd = "#E6DAE6"
            ),
            dark = theme(
                "bomu_dark", "薄暮", "暮紫 · 玫瑰金",
                bg = "#120A12", card = "#1C121C",
                accent = "#C88BBE", accentDim = "#9A6292",
                aux = "#D8A882", auxDim = "#A87C5A",
                text = "#F4E8F2", text2 = "#C6B4C4", text3 = "#8C7A8A", brd = "#2C1E2C"
            )
        )
    )

    /** 全部 12 套（6 配色 × 2 明暗），按家族顺序展开 */
    val ALL: List<ThemeColors> = FAMILIES.flatMap { listOf(it.light, it.dark) }

    fun byId(id: String): ThemeColors = ALL.firstOrNull { it.id == id } ?: ALL[0]

    /** 按「配色家族 + 是否深色」取主题（存档存家族 id 与深色开关） */
    fun resolve(familyId: String?, dark: Boolean): ThemeColors {
        val f = FAMILIES.firstOrNull { it.id == familyId } ?: FAMILIES[0]
        return if (dark) f.dark else f.light
    }

    /** 当前主题属于哪一套配色 */
    fun familyOf(colors: ThemeColors): Family =
        FAMILIES.firstOrNull { it.light.id == colors.id || it.dark.id == colors.id } ?: FAMILIES[0]
}

/** 明暗判断：id 以 _light 结尾即浅色（用于状态栏图标、纸纹噪点、语义色等） */
fun ThemeColors.isLight(): Boolean = id.endsWith("_light")

/**
 * ★ 语义色（不改四套主题的数据，只按明暗派生）—— 见《人格鉴定-UI重做方案.md》3.3。
 * 业务代码里不要再写 "#XXXXXX"，从这里取。
 */

/** 危险操作（清空 / 重置 / 退出确认）：浅色主题用深红、深色主题用亮红 */
val ThemeColors.danger: String
    get() = if (isLight()) "#C0392B" else "#E05A4A"

/** 危险操作的弱化版（描边、icon） */
val ThemeColors.dangerDim: String
    get() = if (isLight()) "#E2A9A2" else "#8C3A30"

/** 主色（金色）按钮上应该用的文字色 */
val ThemeColors.onAccent: String
    get() = if (isLight()) "#FFFFFF" else bg

/**
 * ★ 光色阶梯（2026-10-01 设计系统 2.0，见《人格鉴定-界面深度设计方案.md》2.1）：
 * 光效一律从主题色派生（alpha 阶梯），业务代码按阶梯取，不要自己写 alpha。
 * 规矩：数据色（data1~8）只管图表；光色只管氛围与状态 —— 两者不混用。
 */
val ThemeColors.glowGold06: Color get() = colorFromHex(gold).copy(alpha = 0.06f)
val ThemeColors.glowGold12: Color get() = colorFromHex(gold).copy(alpha = 0.12f)
val ThemeColors.glowGold24: Color get() = colorFromHex(gold).copy(alpha = 0.24f)
val ThemeColors.glowGold48: Color get() = colorFromHex(gold).copy(alpha = 0.48f)

val ThemeColors.glowPurple08: Color get() = colorFromHex(purple).copy(alpha = 0.08f)
val ThemeColors.glowPurple16: Color get() = colorFromHex(purple).copy(alpha = 0.16f)
val ThemeColors.glowPurple32: Color get() = colorFromHex(purple).copy(alpha = 0.32f)

/** 扫描线 / 进度尖端：金 60% */
val ThemeColors.scan: Color get() = colorFromHex(gold).copy(alpha = 0.60f)

/** 描边高光（卡片顶缘 1px）——深色面板用白高光，浅色纸面改用极淡的墨色 */
val ThemeColors.rim: Color
    get() = if (isLight()) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.40f)

/**
 * 全局主题。
 *
 * 字体：**全 App 只用系统默认字体**（不声明 fontFamily 即 FontFamily.Default），
 * 与「无期：记账本」「局长终端」保持一致。原先的 Orbitron / Rajdhani 自定义字体
 * 已全部移除（含 res\font 下的 ttf），**不要再加回来**。
 */
@Composable
fun MBDITheme(content: @Composable () -> Unit) {
    // ★ v1.5.2：把系统深色状态同步给 AppStorage（「跟随系统」开着时用它决定明暗）
    val sysDark = androidx.compose.foundation.isSystemInDarkTheme()
    androidx.compose.runtime.SideEffect { AppStorage.updateSystemDark(sysDark) }
    val t = AppStorage.currentMBDITheme()
    val typography = Typography(
        headlineMedium = TextStyle(
            fontWeight = FontWeight.Black,
            fontSize = 22.sp, color = colorFromHex(t.gold)
        ),
        headlineSmall = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp, color = colorFromHex(t.gold)
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp, color = colorFromHex(t.gold)
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp, color = colorFromHex(t.text)
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp, color = colorFromHex(t.text2)
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp, color = colorFromHex(t.text2)
        ),
        bodySmall = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 12.sp, color = colorFromHex(t.text3)
        ),
        // ★ 字号下限 11sp（方案 3.1）：原来这里是 10sp，是全 App 唯一低于下限的
        labelSmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp, color = colorFromHex(t.text3)
        )
    )
    MaterialTheme(
        // 现在是浅色配色，Material 的默认槽位也要跟着走 light（否则 TextField 等会按深色渲染）
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = colorFromHex(t.gold),
            secondary = colorFromHex(t.purple),
            surface = colorFromHex(t.bg),
            onSurface = colorFromHex(t.text),
            background = colorFromHex(t.bg),
            onBackground = colorFromHex(t.text)
        ),
        typography = typography,
        content = content
    )
}
