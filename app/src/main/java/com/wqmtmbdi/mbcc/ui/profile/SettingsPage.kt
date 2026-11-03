package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.BuildConfig
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.danger
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.DialogScale
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 全部设置（v1.5.1 新增，用户要求）。
 *
 * 定位：整个 App 唯一的功能总目录 —— 「我的」页只留 6 个常用图标入口，
 * 低频内容（关于 / 开源组件 / 重置全部数据）全部收进这里。
 * ★ 以后新增设置项一律加在本页，不要再往「我的」页堆。
 */
@Composable
fun SettingsPage(
    onBack: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenOpenSource: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    var confirmReset by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("全部设置", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            SettingsGroupLabel("外观")
            Column(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
            ) {
                SettingsRow(
                    icon = Lucide.Palette,
                    title = "主题",
                    sub = "6 套配色 · 深色模式",
                    onClick = onOpenTheme
                )
            }

            SettingsGroupLabel("关于")
            Column(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
            ) {
                SettingsRow(
                    icon = Lucide.Info,
                    title = "关于",
                    sub = "软件信息与更新日志",
                    onClick = onOpenAbout
                )
                SettingsDivider()
                SettingsRow(
                    icon = Lucide.Package,
                    title = "开源组件",
                    sub = "本软件使用的开源项目与许可",
                    onClick = onOpenOpenSource
                )
            }

            SettingsGroupLabel("数据")
            Column(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
            ) {
                SettingsRow(
                    icon = Lucide.TriangleAlert,
                    title = "重置全部数据",
                    sub = "清空鉴定记录、解锁与成就",
                    danger = true,
                    onClick = { confirmReset = true }
                )
            }

            Spacer(Modifier.height(MbdiSpace.xxl))
            Text(
                "无期：人格鉴定 v${BuildConfig.VERSION_NAME}（build ${BuildConfig.VERSION_CODE}）",
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(60.dp))
        }
    }

    // 重置全部数据（危险操作，必须二次确认；从「我的」页原样搬来）
    if (confirmReset) {
        Dialog(onDismissRequest = { confirmReset = false }) {
            DialogScale {
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
                            },
                            modifier = Modifier.weight(1f), style = BtnStyle.Danger
                        )
                    }
                }
            }
        }
    }
}

/** 设置分组小标题（斜线 + 文字，与「我的」页原来的分组标题同一语言） */
@Composable
private fun SettingsGroupLabel(text: String) {
    val t = AppStorage.currentMBDITheme()
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
            text, fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold,
            color = colorFromHex(t.text3), letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

/** 设置条目：左图标 + 标题 + 说明 + 右箭头，整行可点 */
@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    sub: String? = null,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val tint = colorFromHex(if (danger) t.danger else t.gold)
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp), tint = tint)
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                title, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(if (danger) t.danger else t.text)
            )
            if (sub != null) {
                Text(
                    sub, fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Icon(
            Lucide.ChevronRight, contentDescription = null,
            modifier = Modifier.size(18.dp), tint = colorFromHex(t.text3)
        )
    }
}

/** 组内分隔线（左侧让出图标宽度，与行内容对齐） */
@Composable
private fun SettingsDivider() {
    val t = AppStorage.currentMBDITheme()
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 58.dp, end = 14.dp)
            .height(1.dp)
            .background(colorFromHex(t.brd))
    )
}
