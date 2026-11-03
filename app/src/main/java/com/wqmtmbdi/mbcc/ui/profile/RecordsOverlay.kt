package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.Record
import com.wqmtmbdi.mbcc.StatsRadar
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import androidx.compose.material3.Icon
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiSheet
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 鉴定记录弹窗。
 *
 * ★ 2026-09-28 第 4 批：
 *  - 改用统一的 [MbdiSheet]（自带 ✕）；
 *  - ★ **详情改成同一弹窗内切换**（列表 ⇄ 详情），详情顶部有「← 返回列表」——
 *    以前详情是**另一个 Dialog**，用户点系统返回键或关掉之后，**记录列表也跟着没了**，
 *    得从头再点一次「我的 → 鉴定记录」；
 *  - 「清空」从 9sp 小字改成危险按钮 + 二次确认（文案写明条数）；
 *  - 空状态给出引导；字号 8sp/9sp → 11sp。
 */
@Composable
fun RecordsOverlay(visible: Boolean, onDismiss: () -> Unit) {
    if (!visible) return
    val t = AppStorage.currentMBDITheme()
    var tick by remember { mutableStateOf(0) }
    var confirmClear by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf<Record?>(null) }
    tick

    // ---- 清空二次确认 ----
    if (confirmClear) {
        val n = AppStorage.data.records.size
        Dialog(onDismissRequest = { confirmClear = false }) {
            com.wqmtmbdi.mbcc.ui.components.DialogScale {
            Column(
                Modifier.fillMaxWidth(0.84f)
                    .mbdiSolid(shape = RoundedCornerShape(MbdiShape.panel))
                    .padding(MbdiSpace.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("清空全部记录？", fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
                Text(
                    "将删除全部 $n 条鉴定记录，此操作不可撤销。",
                    fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text3),
                    modifier = Modifier.padding(top = MbdiSpace.s, bottom = MbdiSpace.l)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                    MbdiButton(
                        "取消", { confirmClear = false },
                        modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                    )
                    MbdiButton(
                        "清空",
                        {
                            AppStorage.clearRecords()
                            confirmClear = false
                            detail = null
                            tick++
                        },
                        modifier = Modifier.weight(1f), style = BtnStyle.Danger
                    )
                }
            }
            }   // ← DialogScale 结束
        }
    }

    // ---- 详情：同一个弹窗内切换，返回不会把列表一起关掉 ----
    val cur = detail
    if (cur != null) {
        MbdiSheet(title = "记录详情", titleIcon = Lucide.FileText, onDismiss = { detail = null }, maxHeight = 580.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(MbdiShape.small))
                    .clickable { detail = null }
                    .padding(vertical = MbdiSpace.xs, horizontal = 2.dp)
            ) {
                Icon(Lucide.ArrowLeft, contentDescription = null, modifier = Modifier.size(15.dp), tint = colorFromHex(t.goldDim))
                Text(
                    "返回列表",
                    fontSize = MbdiType.Label,
                    color = colorFromHex(t.goldDim),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(Modifier.height(MbdiSpace.s))
            RecordDetail(cur)
            Spacer(Modifier.height(MbdiSpace.s))
        }
        return
    }

    // ---- 列表 ----
    MbdiSheet(
        title = "鉴定记录",
        titleIcon = Lucide.History,
        onDismiss = onDismiss,
        maxHeight = 580.dp,
        // ★ 修复（崩溃日志）：列表是 LazyColumn，不能再套 verticalScroll（无限高度约束会闪退）
        scrollable = false,
        footer = {
            if (AppStorage.data.records.isNotEmpty()) {
                MbdiButton(
                    "清空全部记录", { confirmClear = true },
                    modifier = Modifier.fillMaxWidth(), style = BtnStyle.Danger
                )
            }
        }
    ) {
        Text(
            "共 ${AppStorage.data.records.size} 条 · 点任意一条查看当时的完整结果",
            fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
            modifier = Modifier.padding(bottom = MbdiSpace.s)
        )
        if (AppStorage.data.records.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = MbdiSpace.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Lucide.Inbox, contentDescription = null, modifier = Modifier.size(30.dp), tint = colorFromHex(t.text3))
                Text(
                    "还没有鉴定记录。\n去「测试」页完成一次人格鉴定，结果会自动存到这里。",
                    fontSize = MbdiType.Body, lineHeight = 21.sp, color = colorFromHex(t.text3),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = MbdiSpace.s)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                items(AppStorage.data.records) { r ->
                    // ★ 方案 5.8：列表改「时间线」——日期为节点、条目为卡片
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(Modifier.width(18.dp).fillMaxHeight()) {
                            Canvas(Modifier.fillMaxSize()) {
                                val x = size.width / 2f
                                drawLine(
                                    color = colorFromHex(t.gold).copy(alpha = 0.22f),
                                    start = Offset(x, 0f),
                                    end = Offset(x, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                val ny = 24.dp.toPx()
                                drawCircle(colorFromHex(t.gold), radius = 3.2.dp.toPx(), center = Offset(x, ny))
                                drawCircle(colorFromHex(t.bg), radius = 1.2.dp.toPx(), center = Offset(x, ny))
                            }
                        }
                        Row(
                            Modifier
                                .weight(1f)
                                .mbdiCard(shape = RoundedCornerShape(MbdiShape.small))
                                .clickable { detail = r }
                                .padding(MbdiSpace.m),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        r.type, fontSize = MbdiType.Label,
                                        fontWeight = FontWeight.Bold, color = colorFromHex(t.gold)
                                    )
                                    Text(
                                        "${r.date} ${r.time}",
                                        fontSize = MbdiType.Caption, color = colorFromHex(t.text3)
                                    )
                                }
                                Text(
                                    r.title, fontSize = MbdiType.Body, color = colorFromHex(t.text2),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Icon(Lucide.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp), tint = colorFromHex(t.text3))
                        }
                    }
                }
            }
        }
    }
}

/** 记录详情（原本 8~9sp 的字号统一提到 11sp） */
@Composable
private fun RecordDetail(r: Record) {
    val t = AppStorage.currentMBDITheme()
    val found = if (r.charId != null && r.type.contains("人格鉴定"))
        CHARACTERS.firstOrNull { it.id == r.charId } else null
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (found != null) {
            Text(
                "${r.type} · ${r.date} ${r.time}",
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3)
            )
            Text(
                found.name, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold), modifier = Modifier.padding(top = MbdiSpace.s)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s),
                modifier = Modifier.padding(top = MbdiSpace.s)
            ) {
                found.meanings.forEach { m ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            m.l, fontSize = 20.sp, fontWeight = FontWeight.Black,
                            color = colorFromHex(t.gold),
                            modifier = Modifier
                                .background(colorFromHex(t.gold).copy(alpha = 0.07f), RoundedCornerShape(MbdiShape.small))
                                .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.small))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                        Text(
                            m.m, fontSize = MbdiType.Caption, color = colorFromHex(t.text2),
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
            Text(
                found.desc, fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text2),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = MbdiSpace.m)
            )
            if (found.id != "juzhang") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = MbdiSpace.m)
                ) {
                    Icon(Lucide.Radar, contentDescription = null, modifier = Modifier.size(14.dp), tint = colorFromHex(t.text3))
                    Text(
                        "七维观测属性", fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                        letterSpacing = 1.sp, modifier = Modifier.padding(start = 5.dp)
                    )
                }
                StatsRadar(found.stats, found.avatarColor, Modifier.size(220.dp).padding(top = 6.dp))
                val ds = found.statsDisplay ?: found.stats.map { it.toString() }
                val names = listOf("同理心", "意志力", "体质", "教育程度", "智商", "敏捷", "力量")
                Spacer(Modifier.height(MbdiSpace.s))
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    names.forEachIndexed { i, n ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                n, fontSize = MbdiType.Caption,
                                color = colorFromHex(t.text3), modifier = Modifier.weight(1f)
                            )
                            Text("${ds[i]}", fontSize = MbdiType.Caption, color = colorFromHex(t.text2))
                        }
                    }
                }
            }
        } else {
            Text(
                r.type, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold), textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "${r.date} ${r.time}", fontSize = MbdiType.Caption,
                color = colorFromHex(t.text3), modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                r.detail, fontSize = MbdiType.Label, lineHeight = 21.sp, color = colorFromHex(t.text2),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = MbdiSpace.m)
            )
        }
    }
}
