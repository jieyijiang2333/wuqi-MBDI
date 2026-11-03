package com.wqmtmbdi.mbcc.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS
import com.wqmtmbdi.mbcc.data.Announcement
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 公告（独立整页，v1.5.1 由弹窗改过来；v1.5.2 支持「列表 → 单条公告详情」两级）。
 *
 * ★ 入口：测试页右上角铃铛；有未读公告时启动会自动打开本页。
 * ★ **打开即视为已读**：进入页面就能看到内容，红点不必再留。
 * ★ 列表页每条只显示摘要，点击进入**该条公告的详情页**（二级页）；返回键先回列表、再关公告页。
 */
@Composable
fun AnnouncementPage(onBack: () -> Unit, initialDetailId: Int? = null) {
    val t = AppStorage.currentMBDITheme()
    /**
     * null = 列表；非 null = 正在看某一条公告的详情。
     * ★ v1.5.2：启动自动打开时传 [initialDetailId]（最新公告），直接落在详情页；
     *   返回一次到「从上到下」的公告列表，再返回才回主页面。
     */
    var detail by remember { mutableStateOf(ANNOUNCEMENTS.firstOrNull { it.id == initialDetailId }) }

    LaunchedEffect(Unit) { AppStorage.setAnnouncementRead() }

    // 详情页：系统返回键先回列表（列表页时交给外层关闭公告）
    BackHandler(enabled = detail != null) { detail = null }

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar(
            if (detail == null) "公告" else "公告详情",
            onBack = { if (detail != null) detail = null else onBack() }
        )

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(MbdiSpace.s))

            val d = detail
            if (d == null) {
                // ---------- 列表 ----------
                val list = ANNOUNCEMENTS.sortedByDescending { it.id }
                if (list.isEmpty()) {
                    Text(
                        "暂无公告",
                        fontSize = MbdiType.Body, color = colorFromHex(t.text3),
                        modifier = Modifier.fillMaxWidth().padding(top = MbdiSpace.l)
                    )
                } else {
                    list.forEachIndexed { i, ann ->
                        SectionCard(modifier = Modifier.clickable { detail = ann }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MbdiChip(ann.date, colorFromHex(t.goldDim))
                                if (i == 0) {
                                    Spacer(Modifier.width(6.dp))
                                    MbdiChip("最新", colorFromHex(t.gold), filled = true)
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    Lucide.ChevronRight, contentDescription = "查看全文",
                                    modifier = Modifier.padding(start = 6.dp).size(16.dp), tint = colorFromHex(t.text3)
                                )
                            }
                            Spacer(Modifier.height(MbdiSpace.s))
                            Text(
                                ann.content,
                                fontSize = MbdiType.Body, lineHeight = 22.sp,
                                color = colorFromHex(t.text2),
                                maxLines = 3, overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(MbdiSpace.s))
                            Text(
                                ann.sign,
                                fontSize = MbdiType.Caption, color = colorFromHex(t.goldDim),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(MbdiSpace.s))
                    }
                }
            } else {
                // ---------- 单条公告详情 ----------
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MbdiChip(d.date, colorFromHex(t.goldDim))
                        if (d.id == ANNOUNCEMENTS.maxOfOrNull { it.id }) {
                            Spacer(Modifier.width(6.dp))
                            MbdiChip("最新", colorFromHex(t.gold), filled = true)
                        }
                    }
                    Spacer(Modifier.height(MbdiSpace.m))
                    Text(
                        d.content,
                        fontSize = MbdiType.Body, lineHeight = 23.sp,
                        color = colorFromHex(t.text2)
                    )
                    Spacer(Modifier.height(MbdiSpace.l))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            d.sign,
                            fontSize = MbdiType.Label, color = colorFromHex(t.goldDim)
                        )
                    }
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}
