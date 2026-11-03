package com.wqmtmbdi.mbcc.ui.profile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppChrome
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.AvatarCropActivity
import com.wqmtmbdi.mbcc.AvatarStore
import com.wqmtmbdi.mbcc.MbdiSurfaceKind
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.danger
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.mbdiSurface
import androidx.compose.material3.Icon
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpring
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn
import kotlin.math.roundToInt

/** 我的页（v1.5.1：资料卡 + 6 个常用图标入口（4 列 × 2 行）+ 页脚版本号） */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var showRecords by remember { mutableStateOf(false) }
    var showCrashLog by remember { mutableStateOf(false) }
    /**
     * 整页入口（null = 正常「我的」页）：
     * "avatars" 禁闭者形象 ｜ "settings" 全部设置 ｜ "theme" 主题 ｜ "about" 关于 ｜ "opensource" 开源组件
     */
    var page by remember { mutableStateOf<String?>(null) }
    var editField by remember { mutableStateOf<String?>(null) }   // "name" | "sig"
    var tick by remember { mutableIntStateOf(0) }
    var confirmReset by remember { mutableStateOf(false) }   // 重置全部数据（二次确认）

    val profileCrop = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        tick++   // 裁剪返回刷新
        AppStorage.data.customProfile?.avatar = AvatarStore.PROFILE_KEY
        AppStorage.save()
        checkCustomizer()
    }
    val profilePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val tmp = copyToCache(context, uri)
            if (tmp != null) {
                profileCrop.launch(
                    Intent(context, AvatarCropActivity::class.java)
                        .putExtra("src", tmp.absolutePath)
                        .putExtra("key", AvatarStore.PROFILE_KEY)
                )
            } else {
                // ★ 第 6 批：失败走统一气泡条（原来点完没反应）
                com.wqmtmbdi.mbcc.ui.components.TipBus.show("这张图读不出来，换一张试试")
            }
        }
    }
    tick
    val cp = AppStorage.data.customProfile
    val pName = cp?.name?.takeIf { it.isNotBlank() }
    val pSig = cp?.sig?.takeIf { it.isNotBlank() }
    val unlockedCount = AppStorage.data.unlockedChars.size

    // ★ 整页入口：禁闭者形象 / 全部设置 / 主题 / 关于 / 更新日志 / 开源组件
    // ★ 都登记为「独立页」——底部导航自动隐藏，返回键先返回上一级
    when (page) {
        "avatars" -> {
            // 禁闭者形象（v1.5.2：弹窗改独立整页）
            PageChrome(onBack = { page = null })
            AvatarPage(onBack = { page = null })
            return
        }
        "theme" -> {
            PageChrome(onBack = { page = "settings" })
            ThemePage(onBack = { page = "settings" })
            return
        }
        "about" -> {
            PageChrome(onBack = { page = "settings" })
            AboutPage(onBack = { page = "settings" }, onOpenChangelog = { page = "changelog" })
            return
        }
        "changelog" -> {
            // 关于 → 更新日志（独立整页，全部展开），返回回到关于
            PageChrome(onBack = { page = "about" })
            ChangelogPage(onBack = { page = "about" })
            return
        }
        "opensource" -> {
            PageChrome(onBack = { page = "settings" })
            OpenSourcePage(onBack = { page = "settings" })
            return
        }
    }

    Column(
        // ★ 内容末尾留出悬浮胶囊的高度（导航栏是叠加层，不占高度，不给留白会把最后一项永久挡住）
        modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 10.dp, bottom = 100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp, bottom = MbdiSpace.s)
        ) {
            Icon(
                Lucide.UserRound, contentDescription = null,
                modifier = Modifier.size(22.dp), tint = colorFromHex(t.gold)
            )
            Text(
                "我的",
                fontWeight = FontWeight.Bold, fontSize = MbdiType.Title,
                color = colorFromHex(t.gold), letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // ---- 资料卡（★ 方案 5.7：证件铭牌材质 + 头像相机角标 + 可编辑态虚线）----
        Column(
            Modifier.fillMaxWidth().mbdiSurface(MbdiSurfaceKind.Plaque, RoundedCornerShape(MbdiShape.card)).padding(MbdiSpace.l),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(76.dp)) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorFromHex(t.goldDim))
                        .clickable { profilePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = AvatarStore.loadAvatar(context, AvatarStore.PROFILE_KEY)
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(), contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50))
                        )
                    } else {
                        Icon(
                            Lucide.UserRound, contentDescription = null,
                            modifier = Modifier.size(32.dp), tint = colorFromHex(t.bg)
                        )
                    }
                }
                // 相机角标：把「点头像更换」从一行小字升成角标（方案 5.7）
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorFromHex(t.gold))
                        .border(2.dp, colorFromHex(t.card), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Lucide.Camera, contentDescription = "更换头像",
                        modifier = Modifier.size(13.dp), tint = colorFromHex(t.bg)
                    )
                }
            }
            Text(
                pName ?: "点击设置昵称",
                fontSize = MbdiType.Number, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.text),
                modifier = Modifier
                    .clickable { editField = "name" }
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .editableUnderline(colorFromHex(t.gold).copy(alpha = 0.5f))
            )
            Text(
                pSig ?: "点击设置个性签名",
                fontSize = MbdiType.Label, color = colorFromHex(t.text3),
                modifier = Modifier
                    .clickable { editField = "sig" }
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .editableUnderline(colorFromHex(t.text3).copy(alpha = 0.45f))
            )
            Spacer(Modifier.height(MbdiSpace.m))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                StatItem(AppStorage.data.testCount ?: 0, 10, "鉴定次数", t.gold, Modifier.weight(1f))
                StatItem(unlockedCount, CHARACTERS.size, "已解锁", t.purple, Modifier.weight(1f))
                StatItem(
                    AppStorage.data.achievements.size,
                    com.wqmtmbdi.mbcc.data.ACHIEVEMENTS.size,
                    "成就", t.goldDim, Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(MbdiSpace.l))

        // ---- 常用入口（v1.5.1：4 列 × 2 行图标网格，一行四个）----
        //  ★ 低频内容（关于 / 开源组件 / 重置数据）已收进「全部设置」，个人页只留每天会点的动作；
        //    这里刻意不再加第 7 个，一屏装不下就不好找了。
        // ★ v1.5.2：删除「全部设置」页，所有入口平铺在「我的」页（4 列 × 2 行）
        val quick = listOf(
            QuickEntry(Lucide.ClipboardList, "鉴定记录", t.gold) { showRecords = true },
            QuickEntry(Lucide.TriangleAlert, "崩溃日志", t.text3) { showCrashLog = true },
            QuickEntry(Lucide.Image, "禁闭者形象", t.purple) { page = "avatars" },
            QuickEntry(Lucide.Palette, "主题", t.gold) { page = "theme" },
            QuickEntry(Lucide.Info, "关于", t.text2) { page = "about" },
            QuickEntry(Lucide.Package, "开源组件", t.purpleDim) { page = "opensource" },
            QuickEntry(Lucide.TriangleAlert, "重置数据", t.danger) { confirmReset = true }
        )
        Column(
            Modifier
                .fillMaxWidth()
                .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            quick.chunked(4).forEach { rowItems ->
                Row(Modifier.fillMaxWidth()) {
                    rowItems.forEach { e ->
                        QuickTile(e.icon, e.label, colorFromHex(e.tint), Modifier.weight(1f)) { e.onClick() }
                    }
                    repeat(4 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        Spacer(Modifier.height(MbdiSpace.m))
        Text(
            "无期：人格鉴定 v${com.wqmtmbdi.mbcc.BuildConfig.VERSION_NAME}",
            fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MbdiSpace.s))
    }

    RecordsOverlay(showRecords) { showRecords = false }
    // ★ 第 7 批：崩溃日志（自动留档 + 折叠查看 + 导出 txt）
    CrashLogOverlay(showCrashLog) { showCrashLog = false }

    // 重置全部数据（危险操作，二次确认）
    if (confirmReset) {
        Dialog(onDismissRequest = { confirmReset = false }) {
            com.wqmtmbdi.mbcc.ui.components.DialogScale {
                Column(
                    Modifier.fillMaxWidth(0.84f)
                        .mbdiSolid(shape = RoundedCornerShape(MbdiShape.panel))
                        .padding(MbdiSpace.l),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "重置全部数据？",
                        fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                    )
                    Text(
                        "将清空：鉴定记录、已解锁的禁闭者与狂厄特化、成就、自定义头像与个人资料。此操作不可撤销。",
                        fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text3),
                        modifier = Modifier.padding(top = MbdiSpace.s, bottom = MbdiSpace.l)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                        MbdiButton(
                            "取消", { confirmReset = false },
                            modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                        )
                        MbdiButton(
                            "确认重置",
                            {
                                AppStorage.resetAllData()
                                confirmReset = false
                                tick++
                            },
                            modifier = Modifier.weight(1f), style = BtnStyle.Danger
                        )
                    }
                }
            }
        }
    }

    if (editField != null) {
        EditProfileDialog(
            title = if (editField == "name") "设置昵称（限6字）" else "设置个性签名（限12字）",
            maxLen = if (editField == "name") 6 else 12,
            onCancel = { editField = null },
            onSave = { value ->
                val p = AppStorage.data.customProfile ?: com.wqmtmbdi.mbcc.CustomProfile().also { AppStorage.data.customProfile = it }
                if (editField == "name") p.name = value else p.sig = value
                AppStorage.save()
                checkCustomizer()
                tick++
                editField = null
            }
        )
    }
}

/** 常用入口数据（图标 / 名称 / 主题色 / 点击动作） */
private data class QuickEntry(
    val icon: ImageVector,
    val label: String,
    val tint: String,
    val onClick: () -> Unit
)

/**
 * 常用入口格子（v1.5.1，4 列网格里的一个）：图标 + 文字，整格可点。
 * 按压：整格 0.94 缩放 + 图标 1.06 放大 + 轻触感（与记账本个人页同一手感）。
 */
@Composable
private fun QuickTile(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val haptics = LocalHapticFeedback.current
    val inter = remember { MutableInteractionSource() }
    val pressed by inter.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = MbdiSpring.Snappy,
        label = "quickTile"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = MbdiSpring.Snappy,
        label = "quickTileIcon"
    )
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = inter, indication = null) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(40.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
                .background(
                    Brush.linearGradient(
                        listOf(tint.copy(alpha = 0.26f), tint.copy(alpha = 0.12f))
                    ),
                    RoundedCornerShape(13.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(19.dp), tint = tint)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label, fontSize = MbdiType.Caption, fontWeight = FontWeight.Medium,
            color = colorFromHex(t.text2), maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

/**
 * 整页「外壳」登记（v1.5.1 抽出）：进入独立页时隐藏底部导航、接管返回键，离开时复位。
 * 与 AppChrome 的约定保持一致——新增整页只需在这里调一次。
 */
@Composable
private fun PageChrome(onBack: () -> Unit) {
    DisposableEffect(Unit) {
        AppChrome.fullscreen = true
        AppChrome.onBack = { onBack(); true }
        onDispose {
            AppChrome.fullscreen = false
            AppChrome.onBack = null
        }
    }
}

/**
 * 统计三项升级「迷你仪表」（方案 5.7）：小环 + 数字滚动（等宽数字）。
 * 环弧按 have/total 生长；鉴定次数以 10 次为满环刻度（成就「鉴定狂人」的里程碑）。
 */
@Composable
private fun StatItem(have: Int, total: Int, label: String, color: String, modifier: Modifier = Modifier) {
    val t = AppStorage.currentMBDITheme()
    val c = colorFromHex(color)
    val animOn = rememberAnimationsOn()
    val roll = remember { Animatable(if (animOn) 0f else have.toFloat()) }
    LaunchedEffect(have) {
        if (!animOn) {
            roll.snapTo(have.toFloat())
            return@LaunchedEffect
        }
        roll.snapTo(0f)
        roll.animateTo(have.toFloat(), tween(900, easing = FastOutSlowInEasing))
    }
    Column(
        modifier
            .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val sw = 4.dp.toPx()
                val d = size.minDimension - sw
                val tl = Offset((size.width - d) / 2f, (size.height - d) / 2f)
                val sz = Size(d, d)
                drawArc(colorFromHex(t.brd), 0f, 360f, false, style = Stroke(sw), topLeft = tl, size = sz)
                val frac = if (total > 0) have.toFloat() / total else 1f
                val grow = if (have > 0) (roll.value / have).coerceIn(0f, 1f) else 1f
                drawArc(
                    color = c, startAngle = -90f, sweepAngle = 360f * frac * grow, useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round), topLeft = tl, size = sz
                )
            }
            Text(
                "${roll.value.roundToInt()}",
                style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = c,
                    fontFeatureSettings = "tnum"
                )
            )
        }
        Text(
            label, fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

/** 可编辑态的虚线提示（方案 5.7）：文字下方一条淡虚线，暗示「这里能改」 */
private fun Modifier.editableUnderline(color: androidx.compose.ui.graphics.Color): Modifier = this.drawBehind {
    val dash = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
    drawLine(
        color = color,
        start = Offset(size.width * 0.14f, size.height),
        end = Offset(size.width * 0.86f, size.height),
        strokeWidth = 1.dp.toPx(),
        pathEffect = dash
    )
}

@Composable
private fun PlainButton(text: String, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    Text(
        text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colorFromHex(t.text2),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .mbdiCard(shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

/** 编辑昵称/签名弹窗（对齐 HTML editCustomName/editCustomSig 的 prompt 语义） */
@Composable
private fun EditProfileDialog(title: String, maxLen: Int, onCancel: () -> Unit, onSave: (String) -> Unit) {
    val t = AppStorage.currentMBDITheme()
    var value by remember { mutableStateOf("") }
    // ★ 第 6 批（方案第六章）：聚焦时输入框描边点亮 + 字数计数用等宽数字；imePadding 让按钮随键盘上浮
    val focus = remember { MutableInteractionSource() }
    val focused by focus.collectIsFocusedAsState()
    Dialog(onDismissRequest = onCancel) {
        com.wqmtmbdi.mbcc.ui.components.DialogScale {
        Column(
            Modifier.fillMaxWidth(0.86f).imePadding()
                .mbdiSolid(shape = RoundedCornerShape(16.dp)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold))
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        colorFromHex(if (focused) t.gold else t.brd),
                        RoundedCornerShape(MbdiShape.small)
                    )
            ) {
                TextField(
                    value = value,
                    onValueChange = { value = it.trim().take(maxLen) },
                    singleLine = true,
                    interactionSource = focus,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = colorFromHex(t.text)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                "${value.length}/$maxLen",
                style = TextStyle(
                    fontSize = MbdiType.Caption,
                    color = colorFromHex(if (value.length >= maxLen) t.gold else t.text3),
                    fontFeatureSettings = "tnum"
                ),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("取消", fontSize = 11.sp, color = colorFromHex(t.text3),
                    modifier = Modifier
                        .border(1.dp, colorFromHex(t.goldDim), RoundedCornerShape(4.dp))
                        .clickable(onClick = onCancel)
                        .padding(horizontal = 20.dp, vertical = 8.dp))
                Text("确定", fontSize = 11.sp, color = colorFromHex(t.bg),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorFromHex(t.gold))
                        .clickable { if (value.isNotBlank()) onSave(value) }
                        .padding(horizontal = 20.dp, vertical = 8.dp))
            }
        }
        }   // ← DialogScale 结束
    }
}

/** 检查个人定制成就（对齐 HTML checkCustomizer） */
private fun checkCustomizer() {
    val p = AppStorage.data.customProfile ?: return
    if (p.avatar != null && !p.name.isNullOrBlank() && !p.sig.isNullOrBlank()) {
        AppStorage.grantAchievement("customizer")
    }
}
