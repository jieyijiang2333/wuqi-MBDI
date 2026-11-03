package com.wqmtmbdi.mbcc.ui.gallery

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppChrome
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.AvatarStore
import com.wqmtmbdi.mbcc.StatsRadar
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.ACHIEVEMENTS
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.RESONANCE_TYPES
import com.wqmtmbdi.mbcc.data.RES_DIMS
import com.wqmtmbdi.mbcc.data.rankOf
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.scan
import androidx.compose.material3.Icon
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.TriRingData
import com.wqmtmbdi.mbcc.ui.components.TriRingGauge
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.render.scanline
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

/** 图鉴详情数据类型（详情页共用） */
private sealed interface DetailData {
    data class Char(val id: String) : DetailData
    data class Res(val id: String) : DetailData
    data class Ach(val id: String) : DetailData
}

/**
 * 图鉴页（禁闭者 / 狂厄特化 / 成就）。
 *
 * ★ 2026-09-28 第 3 批重做：总收集进度、「全部 / 已解锁 / 未解锁」筛选、代号剪影、详情改全屏页。
 * ★ 2026-10-01 第 3 批深度升级（《人格鉴定-界面深度设计方案.md》5.5）：
 *  - 顶部收集头升级为**三环仪表**（禁闭者 / 特化 / 成就，点任一环 = 切到对应子页）；
 *  - 子 Tab 与状态筛选改为**滑动胶囊指示器**；
 *  - 已解锁 = 微光卡（角色色细描边 + 头像光环）；未解锁 = 剪影 + 缓慢扫描线；
 *  - **新解锁**：卡片一次闪光 + NEW 角标（对比上次打开时存下的集合，只演示一次，状态存本地）；
 *  - 详情页进入时背景泛起该档案的主题色环境光，档案卡缩放浮现。
 */
@Composable
fun GalleryScreen(modifier: Modifier = Modifier, onGoTest: () -> Unit = {}) {
    val t = AppStorage.currentMBDITheme()
    var gTab by remember { mutableStateOf("chars") }
    // ★ v1.5.0：禁闭者筛选从「狂厄性质阵营」改为「角色等级」（狂厄级 / 高危级 / 普管级 / 特殊）
    var rankFilter by remember { mutableStateOf("all") }
    var stateFilter by remember { mutableStateOf("all") }   // all | unlocked | locked
    var detail by remember { mutableStateOf<DetailData?>(null) }

    // ---- 新解锁：与本地存下的「已见过集合」对比，只演示一次（方案 5.5）----
    val newIds = remember {
        val seen = AppStorage.seenUnlockSet()
        buildSet {
            AppStorage.data.unlockedChars.forEach { if ("c:$it" !in seen) add("c:$it") }
            AppStorage.data.unlockedResonance.forEach { if ("r:$it" !in seen) add("r:$it") }
            AppStorage.data.achievements.forEach { if ("a:$it" !in seen) add("a:$it") }
        }
    }
    var flashingNew by remember { mutableStateOf(newIds.isNotEmpty()) }
    LaunchedEffect(Unit) {
        if (newIds.isNotEmpty()) {
            delay(2800)
            flashingNew = false
            AppStorage.markUnlocksSeen()
        }
    }

    // ---- 详情：全屏页 ----
    val d = detail
    if (d != null) {
        // ★ 第 9 批：登记为「独立页」——底部导航自动隐藏，返回键先返回列表（不再退出 App）
        DisposableEffect(Unit) {
            AppChrome.fullscreen = true
            AppChrome.onBack = { detail = null; true }
            onDispose {
                AppChrome.fullscreen = false
                AppChrome.onBack = null
            }
        }
        DetailPage(d, onBack = { detail = null }, onGoTest = { detail = null; onGoTest() })
        return
    }

    Column(modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 6.dp)
        ) {
            Icon(
                Lucide.BookOpen, contentDescription = null,
                modifier = Modifier.size(22.dp), tint = colorFromHex(t.gold)
            )
            Text(
                "图鉴",
                fontWeight = FontWeight.Bold, fontSize = MbdiType.Title,
                color = colorFromHex(t.gold), letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // ---- 三环仪表（替代原来那条单进度条；点任一环 = 进入对应子页）----
        TriRingGauge(
            rings = listOf(
                TriRingData("chars", "禁闭者", AppStorage.data.unlockedChars.size, CHARACTERS.size, t.gold),
                TriRingData("resonance", "狂厄特化", AppStorage.data.unlockedResonance.size, RES_DIMS.size, t.purple),
                TriRingData("achieve", "成就", AppStorage.data.achievements.size, ACHIEVEMENTS.size, t.goldDim)
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) { key -> gTab = key }

        Spacer(Modifier.height(MbdiSpace.m))

        // ---- 子 Tab：滑动胶囊指示器 ----
        SlidingCapsuleRow(
            items = listOf("chars" to "禁闭者", "resonance" to "狂厄特化", "achieve" to "成就"),
            selected = gTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .mbdiCard(shape = RoundedCornerShape(MbdiShape.small))
                .padding(4.dp),
            height = 36.dp
        ) { gTab = it }

        when (gTab) {
            "chars" -> {
                SlidingCapsuleRow(
                    items = listOf("all" to "全部", "unlocked" to "已解锁", "locked" to "未解锁"),
                    selected = stateFilter,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = MbdiSpace.s),
                    height = 32.dp
                ) { stateFilter = it }
                // ★ v1.5.0：等级筛选（替代原「阵营」筛选，对齐记账本角色池口径）
                val ranks = listOf(
                    "all" to "全部等级",
                    "狂厄级" to "狂厄级",
                    "高危级" to "高危级",
                    "普管级" to "普管级",
                    "特殊" to "特殊"
                )
                ChipRow(items = ranks, selected = rankFilter) { rankFilter = it }

                val list = CHARACTERS.filter { c ->
                    val unlocked = AppStorage.data.unlockedChars.contains(c.id)
                    val okState = when (stateFilter) {
                        "unlocked" -> unlocked
                        "locked" -> !unlocked
                        else -> true
                    }
                    val okRank = rankFilter == "all" || rankOf(c.id) == rankFilter
                    okState && okRank
                }.sortedByDescending { AppStorage.data.unlockedChars.contains(it.id) }

                if (list.isEmpty()) {
                    EmptyHint("这个筛选下还没有角色，去完成一次鉴定试试")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 110.dp),
                        horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
                        verticalArrangement = Arrangement.spacedBy(MbdiSpace.s)
                    ) {
                        items(list) { c ->
                            GalleryCharCard(c.id, "c:${c.id}" in newIds && flashingNew) { detail = DetailData.Char(c.id) }
                        }
                    }
                }
            }
            "resonance" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
                    verticalArrangement = Arrangement.spacedBy(MbdiSpace.s)
                ) {
                    items(RES_DIMS) { id ->
                        GalleryResCard(id, "r:$id" in newIds && flashingNew) { detail = DetailData.Res(id) }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
                    verticalArrangement = Arrangement.spacedBy(MbdiSpace.s)
                ) {
                    items(ACHIEVEMENTS) { a ->
                        GalleryAchCard(a.id, "a:${a.id}" in newIds && flashingNew) { detail = DetailData.Ach(a.id) }
                    }
                }
            }
        }
    }
}

/**
 * 滑动胶囊分段选择器（方案 5.5）：选中底色是一枚会平滑滑到目标位置的胶囊。
 * 用于固定项数的筛选行（子 Tab / 状态筛选）；长列表（阵营）仍用横向 chip。
 */
@Composable
private fun SlidingCapsuleRow(
    items: List<Pair<String, String>>,
    selected: String,
    modifier: Modifier = Modifier,
    height: Dp = 34.dp,
    onSelect: (String) -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val gold = colorFromHex(t.gold)
    val shape = RoundedCornerShape(MbdiShape.pill)
    val density = LocalDensity.current
    val positions = remember { mutableStateMapOf<String, Pair<Float, Float>>() }
    val target = positions[selected]
    val capX by animateFloatAsState(target?.first ?: 0f, MbdiSpring.Soft, label = "capX")
    val capW by animateFloatAsState(target?.second ?: 0f, MbdiSpring.Soft, label = "capW")

    Box(modifier.height(height)) {
        if (target != null) {
            Box(
                Modifier
                    .offset { IntOffset(capX.roundToInt(), 0) }
                    .width(with(density) { capW.toDp() })
                    .fillMaxHeight()
                    .clip(shape)
                    .background(gold.copy(alpha = 0.13f))
                    .border(1.dp, gold.copy(alpha = 0.50f), shape)
            )
        }
        Row(Modifier.fillMaxSize()) {
            items.forEach { (id, name) ->
                val active = selected == id
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .onGloballyPositioned { coords ->
                            positions[id] = coords.positionInParent().x to coords.size.width.toFloat()
                        }
                        .clip(shape)
                        .clickable { onSelect(id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        name,
                        fontSize = MbdiType.Caption,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = colorFromHex(if (active) t.gold else t.text3)
                    )
                }
            }
        }
    }
}

/** 「NEW」角标（方案 5.5：新解锁闪光只演示一次，状态存本地） */
@Composable
private fun NewBadge(modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    Box(
        modifier
            .clip(RoundedCornerShape(MbdiShape.pill))
            .background(colorFromHex(t.gold))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text("NEW", fontSize = MbdiType.Caption, fontWeight = FontWeight.Black, color = colorFromHex(t.bg))
    }
}

/** 一行可横向滑动的筛选 chip */
@Composable
private fun ChipRow(items: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    val t = AppStorage.currentMBDITheme()
    LazyRow(
        Modifier.fillMaxWidth().padding(top = MbdiSpace.s),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items) { (id, name) ->
            val active = selected == id
            val shape = RoundedCornerShape(MbdiShape.pill)
            Text(
                name,
                fontSize = MbdiType.Caption,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                color = colorFromHex(if (active) t.gold else t.text3),
                modifier = Modifier
                    .clip(shape)
                    .background(if (active) colorFromHex(t.gold).copy(alpha = 0.13f) else Color.Transparent)
                    .border(1.dp, colorFromHex(if (active) t.goldDim else t.brd), shape)
                    .clickable { onSelect(id) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    val t = AppStorage.currentMBDITheme()
    Box(Modifier.fillMaxSize().padding(MbdiSpace.xxl), contentAlignment = Alignment.TopCenter) {
        Text(
            text,
            fontSize = MbdiType.Body,
            color = colorFromHex(t.text3),
            textAlign = TextAlign.Center
        )
    }
}

// ===================== 网格卡片 =====================

/**
 * 头像 / 代号剪影。
 * 未解锁时不再画「?」，而是画**代号首字的灰色剪影** —— 能看到轮廓才有收集欲。
 */
@Composable
private fun SigilCircle(
    unlocked: Boolean,
    seed: String,
    imageKey: String?,
    bgColor: String,
    size: androidx.compose.ui.unit.Dp
) {
    val t = AppStorage.currentMBDITheme()
    val ctx = LocalContext.current
    val bmp = remember(imageKey) { imageKey?.let { AvatarStore.loadAvatar(ctx, it) } }
    Box(
        Modifier.size(size).clip(RoundedCornerShape(50)).background(
            if (unlocked) colorFromHex(bgColor) else colorFromHex(t.brd)
        ),
        contentAlignment = Alignment.Center
    ) {
        if (unlocked && bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(), contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50))
            )
        } else {
            Text(
                seed.firstOrNull()?.toString() ?: "?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (unlocked) Color.White else colorFromHex(t.text3).copy(alpha = 0.55f)
            )
        }
    }
}

/**
 * 图鉴卡外壳（方案 5.5）：
 *  - 已解锁 = 微光卡（卡面一圈角色色细描边）；
 *  - 未解锁 = 缓慢扫描线（绘制期读值，多个卡片同时扫不产生每帧重组；安静模式静态）；
 *  - [isNew] = NEW 角标（第 8 批：金色扫光已按要求删除）。
 */
@Composable
private fun GalleryCardShell(
    unlocked: Boolean,
    accentColor: String,
    isNew: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val quiet = rememberQuietMode()
    val cardShape = RoundedCornerShape(MbdiShape.card)
    val scanP: State<Float> = if (unlocked || quiet) {
        remember { mutableFloatStateOf(0.4f) }
    } else {
        rememberInfiniteTransition(label = "scan").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(3800, easing = LinearEasing), RepeatMode.Restart),
            label = "scanP"
        )
    }
    // ★ 第 8 批：删掉了「新解锁金色扫光」（用户要求全局扫光效果一律去掉；NEW 角标保留）
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .mbdiCard(shape = cardShape)
            .then(
                if (unlocked) Modifier.border(1.dp, colorFromHex(accentColor).copy(alpha = 0.42f), cardShape)
                else Modifier
            )
            .then(if (!unlocked) Modifier.scanline(t.scan.copy(alpha = 0.10f), scanP) else Modifier)
            .clickable(onClick = onClick)
    ) {
        content()
        if (isNew) {
            NewBadge(Modifier.align(Alignment.TopEnd).padding(6.dp))
        }
    }
}

@Composable
private fun GalleryCharCard(id: String, isNew: Boolean, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val c = CHARACTERS.firstOrNull { it.id == id } ?: return
    val unlocked = AppStorage.data.unlockedChars.contains(id)
    val rank = rankOf(id)
    GalleryCardShell(unlocked, c.avatarColor, isNew, onClick) {
        Column(
            Modifier.fillMaxSize().padding(MbdiSpace.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ★ 用户要求：已删除头像外的泛光光环
            SigilCircle(unlocked, c.code, AppStorage.data.customAvatars[id], c.avatarColor, 44.dp)
            Spacer(Modifier.height(6.dp))
            Text(
                c.name,
                fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(if (unlocked) t.text else t.text2),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                c.code,
                fontSize = MbdiType.Label, fontWeight = FontWeight.Black,
                color = colorFromHex(if (unlocked) t.purple else t.text3),
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.7f)
            )
            Spacer(Modifier.height(4.dp))
            if (unlocked) {
                MbdiChip(rank, colorFromHex(t.purpleDim), filled = true)
            } else {
                MbdiChip("未解锁", colorFromHex(t.text3))
            }
        }
    }
}

@Composable
private fun GalleryResCard(id: String, isNew: Boolean, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val type = RESONANCE_TYPES[id] ?: return
    val unlocked = AppStorage.data.unlockedResonance.contains(id)
    GalleryCardShell(unlocked, type.color, isNew, onClick) {
        Column(
            Modifier.fillMaxSize().padding(MbdiSpace.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                SigilCircle(unlocked, type.code, null, type.color, 44.dp)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                type.name,
                fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(if (unlocked) t.text else t.text2)
            )
            Text("MBDI-${type.code}", fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
            Spacer(Modifier.height(4.dp))
            MbdiChip(
                if (unlocked) "已归档" else "未解锁",
                colorFromHex(if (unlocked) t.purpleDim else t.text3),
                filled = unlocked
            )
        }
    }
}

@Composable
private fun GalleryAchCard(id: String, isNew: Boolean, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val a = ACHIEVEMENTS.firstOrNull { it.id == id } ?: return
    val unlocked = AppStorage.data.achievements.contains(id)
    GalleryCardShell(unlocked, t.gold, isNew, onClick) {
        Column(
            Modifier.fillMaxSize().padding(MbdiSpace.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Lucide.of(a.icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = colorFromHex(if (unlocked) t.gold else t.text3).copy(alpha = if (unlocked) 1f else 0.5f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                a.name,
                fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(if (unlocked) t.text else t.text2),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                a.desc,
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                textAlign = TextAlign.Center,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

// ===================== 详情全屏页 =====================

/**
 * 详情页外壳：顶栏（← 返回）+ 可滚动内容 + 未解锁时的引导按钮。
 * ★ 2026-10-01（方案 5.5）：背景泛起该档案的主题色环境光；进入时档案卡缩放浮现。
 */
@Composable
private fun DetailPage(d: DetailData, onBack: () -> Unit, onGoTest: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val locked = when (d) {
        is DetailData.Char -> !AppStorage.data.unlockedChars.contains(d.id)
        is DetailData.Res -> !AppStorage.data.unlockedResonance.contains(d.id)
        is DetailData.Ach -> !AppStorage.data.achievements.contains(d.id)
    }
    // 该档案的主题色（背景环境光用；成就没有专属色就不泛光）
    val accentHex = when (d) {
        is DetailData.Char -> CHARACTERS.firstOrNull { it.id == d.id }?.avatarColor
        is DetailData.Res -> RESONANCE_TYPES[d.id]?.color
        else -> null
    }
    // 入场：档案卡缩放浮现（Gentle 弹簧；关动画直接终值）
    val animOn = rememberAnimationsOn()
    val enter = remember { Animatable(if (animOn) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animOn) enter.animateTo(1f, MbdiSpring.Gentle)
    }

    Column(
        Modifier
            .fillMaxSize()
            .drawBehind {
                val hex = accentHex ?: return@drawBehind
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(colorFromHex(hex).copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = size.width * 0.9f
                    )
                )
            }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 6.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50)).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Lucide.ArrowLeft, contentDescription = "返回", modifier = Modifier.size(22.dp), tint = colorFromHex(t.text2))
            }
            Text(
                "档案详情",
                fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp)
        ) {
            Column(
                Modifier.graphicsLayer {
                    val v = enter.value
                    scaleX = 0.86f + 0.14f * v
                    scaleY = 0.86f + 0.14f * v
                    alpha = 0.25f + 0.75f * v
                }
            ) {
                SectionCard {
                    when (d) {
                        is DetailData.Char -> CharDetail(d.id)
                        is DetailData.Res -> ResDetail(d.id)
                        is DetailData.Ach -> AchDetail(d.id)
                    }
                }
            }
            if (locked) {
                Spacer(Modifier.height(MbdiSpace.m))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MbdiButton("去找和 TA 匹配的答案", onGoTest, style = BtnStyle.Primary)
                }
            }
            Spacer(Modifier.height(110.dp))   // 悬浮导航栏留白
        }
    }
}

/** 角色详情（保留原有内容：头像 / 代号释义 / 七维雷达 / 描述） */
@Composable
private fun CharDetail(id: String) {
    val t = AppStorage.currentMBDITheme()
    val c = CHARACTERS.firstOrNull { it.id == id } ?: return
    val unlocked = AppStorage.data.unlockedChars.contains(id)
    val rank = rankOf(id)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SigilCircle(unlocked, c.code, AppStorage.data.customAvatars[id], c.avatarColor, 72.dp)
        Text(
            c.name, fontSize = MbdiType.Number, fontWeight = FontWeight.Bold,
            color = colorFromHex(t.gold), modifier = Modifier.padding(top = MbdiSpace.s)
        )
        Box(Modifier.padding(top = 6.dp)) {
            MbdiChip(
                if (unlocked) rank.ifEmpty { "禁闭者" } else "未解锁",
                colorFromHex(if (unlocked) t.purple else t.text3),
                filled = true
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
            modifier = Modifier.padding(top = MbdiSpace.m)
        ) {
            c.meanings.forEach { m ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        m.l, fontSize = MbdiType.Code, fontWeight = FontWeight.Black,
                        color = colorFromHex(t.gold),
                        modifier = Modifier
                            .background(colorFromHex(t.gold).copy(alpha = 0.07f), RoundedCornerShape(MbdiShape.small))
                            .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.small))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                    Text(
                        m.m, fontSize = MbdiType.Caption, color = colorFromHex(t.text2),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
        if (id != "juzhang") {
            Text(
                "七维观测属性", fontSize = MbdiType.Label, color = colorFromHex(t.text3),
                modifier = Modifier.padding(top = MbdiSpace.l)
            )
            StatsRadar(c.stats, c.avatarColor, Modifier.size(250.dp).padding(top = 6.dp))
            val ds = c.statsDisplay ?: c.stats.map { it.toString() }
            val names = listOf("同理心", "意志力", "体质", "教育程度", "智商", "敏捷", "力量")
            Spacer(Modifier.height(MbdiSpace.s))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                names.forEachIndexed { i, n ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(n, fontSize = MbdiType.Caption, color = colorFromHex(t.text3), modifier = Modifier.weight(1f))
                        Text("${ds[i]}", fontSize = MbdiType.Caption, color = colorFromHex(t.text2))
                    }
                }
            }
        } else {
            // ★ v1.5.0：局长 = 玩家本人。「局长档案」展示你自己的七维画像 ——
            //   数据独立于内置角色（存 AppStorage.juzhangStats），完成任意一档鉴定后自动生成/更新。
            val myStats = AppStorage.data.juzhangStats
            if (myStats != null && myStats.size == 7) {
                val myTier = AppStorage.data.juzhangTier ?: 2
                val myDate = AppStorage.data.juzhangDate
                Text(
                    "我的七维档案", fontSize = MbdiType.Label, color = colorFromHex(t.goldDim),
                    modifier = Modifier.padding(top = MbdiSpace.l)
                )
                StatsRadar(myStats, t.gold, Modifier.size(250.dp).padding(top = 6.dp))
                val names = listOf("同理心", "意志力", "体质", "教育程度", "智商", "敏捷", "力量")
                Spacer(Modifier.height(MbdiSpace.s))
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    names.forEachIndexed { i, n ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(n, fontSize = MbdiType.Caption, color = colorFromHex(t.text3), modifier = Modifier.weight(1f))
                            Text(
                                "%.1f".format(myStats.getOrElse(i) { 0.0 }),
                                fontSize = MbdiType.Caption, color = colorFromHex(t.text2)
                            )
                        }
                    }
                }
                Text(
                    "档案来源：${com.wqmtmbdi.mbcc.ui.test.getTierLabel(myTier)}" +
                        (if (!myDate.isNullOrBlank()) " · $myDate" else ""),
                    fontSize = MbdiType.Caption, color = colorFromHex(t.goldDim),
                    modifier = Modifier.padding(top = MbdiSpace.s)
                )
            } else {
                Text(
                    "完成一次人格鉴定（深度鉴定 / 狂厄共振）后，这里会自动生成你的七维档案",
                    fontSize = MbdiType.Caption, color = colorFromHex(t.goldDim),
                    modifier = Modifier.padding(top = MbdiSpace.l)
                )
            }
        }
        Text(
            c.desc, fontSize = MbdiType.Label, lineHeight = 21.sp, color = colorFromHex(t.text2),
            modifier = Modifier.padding(top = MbdiSpace.m)
        )
    }
}

/** 狂厄特化详情 */
@Composable
private fun ResDetail(id: String) {
    val t = AppStorage.currentMBDITheme()
    val type = RESONANCE_TYPES[id] ?: return
    val unlocked = AppStorage.data.unlockedResonance.contains(id)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SigilCircle(unlocked, type.code, null, type.color, 72.dp)
        Text(
            type.name, fontSize = MbdiType.Number, fontWeight = FontWeight.Bold,
            color = colorFromHex(t.gold), modifier = Modifier.padding(top = MbdiSpace.s)
        )
        Text(
            "归档编号：MBDI-${type.code}", fontSize = MbdiType.Caption,
            color = colorFromHex(t.text3), letterSpacing = 1.sp
        )
        Box(Modifier.padding(top = 6.dp)) {
            MbdiChip(
                if (unlocked) "已归档" else "未解锁",
                colorFromHex(if (unlocked) t.purple else t.text3),
                filled = true
            )
        }
        Text(
            type.trait, fontSize = MbdiType.Label, lineHeight = 21.sp, color = colorFromHex(t.text2),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MbdiSpace.m)
        )
        if (unlocked) {
            Text(
                type.desc, fontSize = MbdiType.Label, lineHeight = 21.sp, color = colorFromHex(t.text2),
                modifier = Modifier.padding(top = MbdiSpace.s)
            )
        } else {
            Text(
                "完成一次狂厄共鸣测试即可解锁此档案",
                fontSize = MbdiType.Caption, color = colorFromHex(t.goldDim),
                modifier = Modifier.padding(top = MbdiSpace.m)
            )
        }
    }
}

/** 成就详情 */
@Composable
private fun AchDetail(id: String) {
    val t = AppStorage.currentMBDITheme()
    val a = ACHIEVEMENTS.firstOrNull { it.id == id } ?: return
    val unlocked = AppStorage.data.achievements.contains(id)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Lucide.of(a.icon),
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = colorFromHex(if (unlocked) t.gold else t.text3).copy(alpha = if (unlocked) 1f else 0.45f)
        )
        Text(
            a.name, fontSize = MbdiType.Number, fontWeight = FontWeight.Bold,
            color = colorFromHex(t.gold), modifier = Modifier.padding(top = MbdiSpace.s)
        )
        Box(Modifier.padding(top = 6.dp)) {
            MbdiChip(
                if (unlocked) "已达成" else "未达成",
                colorFromHex(if (unlocked) t.purple else t.text3),
                filled = true
            )
        }
        Text(
            if (unlocked) "获得方式：${a.desc}" else "达成条件：${a.desc}",
            fontSize = MbdiType.Label, lineHeight = 21.sp, color = colorFromHex(t.text2),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MbdiSpace.m)
        )
    }
}
