package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.BuildConfig
import com.wqmtmbdi.mbcc.R
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 关于页（v1.5.1 改造：由弹窗改为独立整页，用户要求）。
 *
 * 内容只保留必要信息：App 图标 + 软件名 + 最新版本 + 更新日志。
 * 入口在「全部设置 → 关于」；作者 / 联系方式等暂不放（用户要求"其他的暂时不需要"）。
 *
 * 历史沿革（保留原设计要点）：更新日志为折叠式，最新一条默认展开；
 * 顶部一行粉丝二创声明（合规底线）。
 */
@Composable
fun AboutPage(onBack: () -> Unit, onOpenChangelog: () -> Unit) {
    val t = AppStorage.currentMBDITheme()

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("关于", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            // ---- App 图标 + 名称 + 最新版本 ----
            Column(
                Modifier.fillMaxWidth().padding(top = MbdiSpace.m, bottom = MbdiSpace.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = "应用图标",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, colorFromHex(t.gold).copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                )
                Text(
                    "无期：人格鉴定",
                    fontSize = MbdiType.Number, fontWeight = FontWeight.Bold,
                    color = colorFromHex(t.gold), letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = MbdiSpace.m)
                )
                Text(
                    "最新版本 v${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE}",
                    fontSize = MbdiType.Label, color = colorFromHex(t.text2),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "开发者：海与燕制作组",
                    fontSize = MbdiType.Label, color = colorFromHex(t.text2),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "粉丝二创作品，与《无期迷途》官方无关；此游戏搭配 AI 辅助制作。",
                    fontSize = MbdiType.Caption, lineHeight = 17.sp, color = colorFromHex(t.text3),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 12.dp, end = 12.dp)
                )
            }

            // ---- 更新日志入口（v1.5.1：内容移到独立整页，全部展开）----
            Row(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .clickable(onClick = onOpenChangelog)
                    .padding(horizontal = MbdiSpace.l, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Lucide.History, contentDescription = null,
                    modifier = Modifier.size(17.dp), tint = colorFromHex(t.gold)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "更新日志", Modifier.weight(1f),
                    fontSize = MbdiType.Body, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                )
                Text("v${BuildConfig.VERSION_NAME}", fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
                Spacer(Modifier.width(6.dp))
                Icon(
                    Lucide.ChevronRight, contentDescription = null,
                    modifier = Modifier.size(16.dp), tint = colorFromHex(t.text3)
                )
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}
