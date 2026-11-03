package com.wqmtmbdi.mbcc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.wqmtmbdi.mbcc.danger
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.onAccent

/**
 * MBDI 通用组件（见《人格鉴定-UI重做方案.md》3.4）
 * 新写界面一律用这些零件，不要再自己拼 `Column + mbdiCard + padding`。
 */

/** 卡片底料（内部用）：半透明卡底 + 主题描边 */
private fun Modifier.cardBase(shape: androidx.compose.ui.graphics.Shape) =
    this.mbdiCard(shape)

/**
 * 带标题的标准区块卡片。
 *
 * @param title    区块标题（16sp），传 null 表示不要标题
 * @param subtitle 副标题（12sp，弱色）
 * @param trailing 标题右侧的小动作（如「查看全部 ›」）
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    padding: androidx.compose.ui.unit.Dp = MbdiSpace.l,
    content: @Composable ColumnScope.() -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    Column(
        modifier.fillMaxWidth().cardBase(RoundedCornerShape(MbdiShape.card)).padding(padding)
    ) {
        if (title != null) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = MbdiType.Heading,
                        fontWeight = FontWeight.Bold,
                        color = colorFromHex(t.text)
                    )
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            fontSize = MbdiType.Label,
                            color = colorFromHex(t.text3),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                trailing?.invoke()
            }
            Spacer(Modifier.height(MbdiSpace.m))
        }
        content()
    }
}

enum class BtnStyle { Primary, Secondary, Text, Danger }

/**
 * 标准按钮：主 / 次 / 文字 / 危险 四态，**热区一律 ≥ 44dp**。
 */
@Composable
fun MbdiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: BtnStyle = BtnStyle.Primary,
    enabled: Boolean = true,
    leading: ImageVector? = null,
    trailing: ImageVector? = null
) {
    val t = AppStorage.currentMBDITheme()
    val gold = colorFromHex(t.gold)
    val dangerC = colorFromHex(t.danger)
    val bg = colorFromHex(t.bg)
    val textColor = when {
        !enabled -> colorFromHex(t.text3).copy(alpha = 0.45f)
        style == BtnStyle.Primary -> colorFromHex(t.onAccent)
        style == BtnStyle.Danger -> dangerC
        style == BtnStyle.Secondary -> gold
        else -> colorFromHex(t.text2)
    }
    val shape = RoundedCornerShape(MbdiShape.pill)

    // ★ 设计系统 2.0：按压 = Snappy 弹簧缩放 + 轻触感（方案 3.3）；第 9 批按用户要求删掉按钮泛光
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = MbdiSpring.Snappy,
        label = "pressScale"
    )
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(interaction) {
        interaction.interactions.collect { ev ->
            if (ev is androidx.compose.foundation.interaction.PressInteraction.Press) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    Box(
        modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .heightIn(min = 48.dp)
            .clip(shape)
            .then(
                when {
                    !enabled -> Modifier.background(colorFromHex(t.card))
                    style == BtnStyle.Primary -> Modifier.background(
                        Brush.horizontalGradient(listOf(gold, colorFromHex(t.goldDim)))
                    )
                    style == BtnStyle.Secondary -> Modifier
                        .background(colorFromHex(t.card))
                        .border(1.dp, gold.copy(alpha = 0.55f), shape)
                    style == BtnStyle.Danger -> Modifier.border(1.dp, dangerC.copy(alpha = 0.7f), shape)
                    else -> Modifier
                }
            )
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = MbdiSpace.xl, vertical = MbdiSpace.m),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (leading != null) {
                Icon(leading, contentDescription = null, modifier = Modifier.size(17.dp), tint = textColor)
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text,
                fontSize = MbdiType.Body,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            if (trailing != null) {
                Spacer(Modifier.width(6.dp))
                Icon(trailing, contentDescription = null, modifier = Modifier.size(17.dp), tint = textColor)
            }
        }
    }
}

/** 雷达图 / 图表图例：色块 + 文字 */
@Composable
fun LegendDot(color: Color, label: String, dashed: Boolean = false) {
    val t = AppStorage.currentMBDITheme()
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (dashed) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) {
                    Box(Modifier.size(width = 5.dp, height = 3.dp).clip(RoundedCornerShape(1.dp)).background(color))
                }
            }
        } else {
            Box(Modifier.size(width = 14.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(color))
        }
        Text(
            label,
            fontSize = MbdiType.Caption,
            color = colorFromHex(t.text3),
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

/**
 * 七维对比条（结果页 / 记录详情用）：
 * 上行 = 角色的该维度，下行 = 你的该维度；右侧给两个数值。
 *
 * @param playerValue 0–10
 * @param charValue   0–10
 */
@Composable
fun DimCompareBar(
    label: String,
    playerValue: Double,
    charValue: Double,
    charColor: Color,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val t = AppStorage.currentMBDITheme()
    val gold = colorFromHex(t.gold)
    val track = colorFromHex(t.brd)
    val shape = RoundedCornerShape(MbdiShape.small)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .then(if (selected) Modifier.background(gold.copy(alpha = 0.10f)) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = MbdiSpace.s, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = MbdiType.Label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) gold else colorFromHex(t.text2),
            modifier = Modifier.width(52.dp)
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            // 角色
            Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(track)) {
                Box(
                    Modifier
                        .fillMaxWidth((charValue / 10.0).toFloat().coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(charColor)
                )
            }
            // 你
            Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(track)) {
                Box(
                    Modifier
                        .fillMaxWidth((playerValue / 10.0).toFloat().coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(gold)
                )
            }
        }
        Column(Modifier.padding(start = MbdiSpace.s), horizontalAlignment = Alignment.End) {
            Text("%.1f".format(charValue), fontSize = MbdiType.Caption, color = charColor)
            Text("%.1f".format(playerValue), fontSize = MbdiType.Caption, color = gold)
        }
    }
}

/** 小标签（阵营、解锁状态…） */
@Composable
fun MbdiChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    val shape = RoundedCornerShape(MbdiShape.pill)
    Box(
        modifier
            .clip(shape)
            .then(if (filled) Modifier.background(color.copy(alpha = 0.18f)) else Modifier.border(1.dp, color.copy(alpha = 0.6f), shape))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold, color = color)
    }
}

/** 空状态：一句话 + 可选引导按钮 */
@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    icon: ImageVector = Lucide.Inbox
) {
    val t = AppStorage.currentMBDITheme()
    Column(
        modifier.fillMaxWidth().padding(vertical = MbdiSpace.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(30.dp), tint = colorFromHex(t.text3))
        Text(
            text,
            fontSize = MbdiType.Body,
            color = colorFromHex(t.text3),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MbdiSpace.s)
        )
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(MbdiSpace.l))
            MbdiButton(actionText, onAction, style = BtnStyle.Secondary)
        }
    }
}

/**
 * 统一弹窗外壳（标题栏 + 可滚动内容 + 可选底部按钮区）。
 *
 * ★ 2026-09-28 第 4 批新增：全 App 的弹窗一律走它。原来每个弹窗各写各的，
 * 结果问题五花八门 —— 有的**没有 ✕**（玩法介绍）、有的高度被 `fillMaxSize/weight` 撑满
 * （文字下面一大片空白）、关闭按钮热区只有 20dp、标题色硬编码不跟主题。
 *
 * 约定：
 *  - 高度**内容自适应**，最高 [maxHeight]（默认 560dp），超了才滚动；
 *  - 右上角恒有 ✕（36dp 热区），点遮罩也关；
 *  - 标题统一用主题色，不写死颜色。
 *
 * ★ 2026-10-02 第 5 批 · 弹窗 2.0（方案 5.8）：所有浮层像从同一台仪器里「升起」——
 *  - 进入：面板 Soft 弹簧上滑 + 淡入，标题图标光晕渐显；
 *  - 退出：更快反演（70% 时长，tween 200ms）；
 *  - 三关（✕ / 遮罩 / 返回键）行为不变：走同一个 close()，出动画播完才真正关闭；
 *  - 安静模式（系统关动画 / 低端机）跳过动画直接呈现。
 */
@Composable
fun MbdiSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 560.dp,
    titleIcon: ImageVector? = null,
    /**
     * ★ 2026-10-02 修复（崩溃日志）：内容是否由 MbdiSheet 统一套 verticalScroll。
     *  - true（默认）：普通内容（文字 / 卡片）由这里滚动；
     *  - false：**内容里含 LazyColumn / LazyVerticalGrid 时必须传 false** ——
     *    否则 Lazy 会在「无限高度约束」下被测量，直接抛
     *    IllegalStateException: Vertically scrollable component was measured with an infinity
     *    maximum height constraints（用户 1.4.0 实测闪退：鉴定记录 / 禁闭者形象两个弹窗）。
     *    此时内容槽改为「有界高度 + 不强制填满」，由内容里的 Lazy 自己滚动。
     */
    scrollable: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val quiet = com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode()
    // 面板进场进度：0 = 收起（下方 34dp + 透明），1 = 落定
    var shown by remember { mutableStateOf(false) }
    var leaving by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val enter by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = if (quiet) tween(0)
        else if (shown) MbdiSpring.Soft
        else tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "sheetEnter"
    )
    // 出动画播完 → 才真正回调关闭（保证「三关」都能看到一致的退出）
    LaunchedEffect(leaving, enter) {
        if (leaving && enter <= 0.01f) onDismiss()
    }
    fun close() {
        if (!leaving) { leaving = true; shown = false }
    }
    Dialog(onDismissRequest = { close() }) {
        DialogScale {
        Column(
            modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = maxHeight)
                .graphicsLayer {
                    alpha = enter
                    translationY = (1f - enter) * 34.dp.toPx()
                }
                .mbdiSolid(shape = RoundedCornerShape(MbdiShape.panel))
                .padding(MbdiSpace.l)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (titleIcon != null) {
                    // 标题图标点亮（方案 5.8）：随面板升起渐显（第 9 批按用户要求删掉泛光光圈）
                    Icon(
                        titleIcon, contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colorFromHex(t.gold).copy(alpha = 0.55f + 0.45f * enter)
                    )
                    Spacer(Modifier.width(7.dp))
                }
                Text(
                    title,
                    fontSize = MbdiType.Heading,
                    fontWeight = FontWeight.Bold,
                    color = colorFromHex(t.gold),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .size(44.dp)          // 热区 ≥44dp（验收清单）
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = { close() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Lucide.X, contentDescription = "关闭", modifier = Modifier.size(18.dp), tint = colorFromHex(t.text3))
                }
            }
            Spacer(Modifier.height(MbdiSpace.s))
            if (scrollable) {
                // ★ 修复（公告按钮被挤出可视区）：内容区必须吃「有界剩余高度」——
                //   以前只套 verticalScroll，长内容会把 Column 顶满、把底部按钮挤出被裁剪的窗口外，
                //   用户就看不到「我知道了」。weight(fill = false) + verticalScroll 才是正确组合。
                Column(
                    Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    content = content
                )
            } else {
                // ★ 修复：不套滚动时给「有界高度（不强制填满）」——Lazy 列表在这里自己滚
                Column(
                    Modifier.fillMaxWidth().weight(1f, fill = false),
                    content = content
                )
            }
            if (footer != null) {
                Spacer(Modifier.height(MbdiSpace.m))
                footer()
            }
        }
        }   // ← DialogScale 结束
    }
}

/**
 * 弹窗字号缩放（第 7 批用户要求：弹窗里的字体一律 +10%）。
 * 只缩放 sp（字体），不动 dp（间距 / 图标 / 热区）—— 覆盖 LocalDensity 的 fontScale 即可。
 * 所有 Dialog（MbdiSheet、隐私全屏、各类确认框）都要包一层。
 */
@Composable
fun DialogScale(content: @Composable () -> Unit) {
    val d = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(d.density, d.fontScale * 1.1f),
        content = content
    )
}

/**
 * 全局轻提示总线（方案第六章）：「气泡条」取代 Toast ——
 * 从底部导航上方滑入、2 秒自动消退；**失败用它、成功不打扰**。
 * 用法：任意位置 `TipBus.show("这张图读不出来")`；宿主由 AppRoot 里的 [TipBarHost] 提供。
 */
object TipBus {
    var text by mutableStateOf<String?>(null)
    fun show(msg: String) { text = msg }
}

/** 气泡条宿主（AppRoot 挂一次）：底部导航上方滑入 / 滑出 */
@Composable
fun TipBarHost(modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    val msg = TipBus.text
    // 记住最后一条文案：退出动画期间内容不塌成空
    var lastMsg by remember { mutableStateOf("") }
    LaunchedEffect(msg) {
        if (msg != null) {
            lastMsg = msg
            kotlinx.coroutines.delay(2000)
            if (TipBus.text == msg) TipBus.text = null
        }
    }
    AnimatedVisibility(
        visible = msg != null,
        enter = androidx.compose.animation.slideInVertically { it / 2 } + androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.slideOutVertically { it / 2 } + androidx.compose.animation.fadeOut(),
        modifier = modifier
    ) {
        Box(
            Modifier
                .mbdiSolid(shape = RoundedCornerShape(MbdiShape.pill))
                .padding(horizontal = MbdiSpace.l, vertical = 10.dp)
        ) {
            Text(lastMsg, fontSize = MbdiType.Label, color = colorFromHex(t.text2))
        }
    }
}


/**
 * 二级整页顶栏（v1.5.1 新增）：返回箭头 + 标题 + 可选右侧操作。
 *
 * 供「全部设置 / 关于 / 开源组件」三个新页面共用；
 * 早期整页（星辰计划 / 反馈中心）各自带顶栏，暂不迁移。
 */
@Composable
fun MbdiPageTopBar(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    val t = AppStorage.currentMBDITheme()
    Row(
        Modifier.fillMaxWidth().padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(50)).clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Lucide.ArrowLeft, contentDescription = "返回",
                modifier = Modifier.size(20.dp), tint = colorFromHex(t.gold)
            )
        }
        Text(
            title, fontSize = MbdiType.Title, fontWeight = FontWeight.Bold,
            color = colorFromHex(t.gold), letterSpacing = 1.sp
        )
        Spacer(Modifier.weight(1f))
        trailing?.invoke()
    }
}
