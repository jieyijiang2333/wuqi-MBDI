package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.MBDIThemes
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 主题页（v1.5.1 新增，用户要求：6 种配色 + 深色模式）。
 *
 * 两个维度：
 *  - **深色模式**开关：浅色 / 深色两版界面（同一套配色下切换明暗）；
 *  - **配色**：6 套色相（零镜仪器 / 白夜档案 / 赤棘 / 苔庭 / 霜原 / 薄暮），卡片本身就是该配色的样张。
 *
 * ★ 选中即生效（`AppStorage.setAppearance`：写存档 + themeTick 触发全屏重组）。
 * ★ 入口：我的 → 全部设置 → 主题。
 */
@Composable
fun ThemePage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val current = MBDIThemes.familyOf(t)
    val follow = AppStorage.data.followSystemDark == true
    val dark = if (follow) AppStorage.systemDark else (AppStorage.data.darkMode ?: true)

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("主题", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(MbdiSpace.s))

            // ---- 跟随系统（默认关闭）----
            Row(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .clickable { AppStorage.setFollowSystemDark(!follow) }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "跟随系统",
                        fontSize = MbdiType.Body, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                    )
                    Text(
                        "深色模式跟随手机的设置 · 默认关闭",
                        fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                MbdiSwitch(checked = follow) { AppStorage.setFollowSystemDark(it) }
            }

            Spacer(Modifier.height(MbdiSpace.s))

            // ---- 深色模式（跟随系统开启时由系统决定，开关置灰）----
            Row(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .clickable(enabled = !follow) { AppStorage.setAppearance(current.id, !dark) }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "深色模式",
                        fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                        color = colorFromHex(t.text)
                    )
                    Text(
                        if (follow) "跟随系统：${if (dark) "深色" else "浅色"}"
                        else if (dark) "深色界面 · 夜间更省眼" else "浅色界面 · 白天更清晰",
                        fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                MbdiSwitch(checked = dark, enabled = !follow) { AppStorage.setAppearance(current.id, it) }
            }

            // ---- 配色 ----
            Row(
                Modifier.padding(start = 4.dp, top = MbdiSpace.l, bottom = MbdiSpace.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Canvas(Modifier.size(width = 6.dp, height = 11.dp)) {
                    drawLine(
                        color = colorFromHex(t.gold).copy(alpha = 0.75f),
                        start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        end = androidx.compose.ui.geometry.Offset(0f, size.height),
                        strokeWidth = 1.8.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
                Text(
                    "配色", fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold,
                    color = colorFromHex(t.text3), letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(
                "共 6 套；每套都包含浅色与深色两版，选中即生效。",
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                modifier = Modifier.padding(start = 4.dp, bottom = MbdiSpace.s)
            )

            MBDIThemes.FAMILIES.chunked(2).forEach { rowItems ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)
                ) {
                    rowItems.forEach { f ->
                        ThemeSampleCard(
                            family = f,
                            dark = dark,
                            selected = f.id == current.id,
                            modifier = Modifier.weight(1f)
                        ) {
                            AppStorage.setAppearance(f.id, dark)
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(MbdiSpace.s))
            }

            Text(
                "主题只影响界面配色，不影响鉴定结果与存档。",
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                modifier = Modifier.padding(start = 4.dp, top = MbdiSpace.xs)
            )
            Spacer(Modifier.height(60.dp))
        }
    }
}

/** 一套配色样张：4 色条 + 名称 + 描述；选中显示描边与勾 */
@Composable
private fun ThemeSampleCard(
    family: MBDIThemes.Family,
    dark: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val preview = if (dark) family.dark else family.light
    val shape = RoundedCornerShape(MbdiShape.card)
    Column(
        modifier
            .clip(shape)
            .background(colorFromHex(preview.card))
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) colorFromHex(preview.gold) else colorFromHex(preview.brd),
                shape
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(MbdiShape.small))) {
            Row(Modifier.fillMaxSize()) {
                preview.swatches.forEach { c ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(colorFromHex(c)))
                }
            }
            if (selected) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(colorFromHex(preview.gold)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Lucide.Check, contentDescription = "当前配色",
                        modifier = Modifier.size(10.dp), tint = colorFromHex(preview.card)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            family.name, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
            color = colorFromHex(preview.text), maxLines = 1
        )
        Text(
            family.desc, fontSize = MbdiType.Caption, color = colorFromHex(preview.text3),
            modifier = Modifier.padding(top = 2.dp), maxLines = 1
        )
    }
}

/** 开关：52×30 胶囊 + 圆点滑动（全 App 只此一处用，主题页专用） */
@Composable
private fun MbdiSwitch(checked: Boolean, enabled: Boolean = true, onChange: (Boolean) -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val knobX by animateDpAsState(if (checked) 25.dp else 3.dp, label = "themeSwitchKnob")
    Box(
        Modifier
            .size(width = 52.dp, height = 30.dp)
            .graphicsLayer(alpha = if (enabled) 1f else 0.45f)
            .clip(CircleShape)
            .background(
                if (checked) colorFromHex(t.gold)
                else colorFromHex(t.text3).copy(alpha = 0.30f)
            )
            .clickable(enabled = enabled) { onChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = knobX, y = 3.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
