package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 星辰计划 · 免责声明全文（独立整页）。
 *
 * ★ 2026-11-03 按《无期：记账本》2026-10-30 版布局搬运：
 *   星辰计划主页只留一句摘要 + 入口，全文（[StarPlanTerms.CLAUSES]）放这里。
 *   入口：星辰计划页 → 免责声明。
 */
@Composable
fun StarPlanTermsPage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("免责声明", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Text(
                "星辰计划 · 打赏前请阅读以下说明；继续打赏或继续使用，视为知悉并接受。",
                fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text3),
                modifier = Modifier.padding(top = MbdiSpace.s)
            )
            Spacer(Modifier.height(MbdiSpace.m))

            Column(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .padding(MbdiSpace.l)
            ) {
                Text(
                    "感谢你对软件的喜爱与支持。为明确打赏性质及相关权益，特作如下说明：",
                    fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text2)
                )
                Spacer(Modifier.height(MbdiSpace.s))
                StarPlanTerms.CLAUSES.forEachIndexed { i, c ->
                    TermsClause(i + 1, c.title, c.body)
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

/** 一条条款：编号 + 小标题 + 正文 */
@Composable
private fun TermsClause(index: Int, title: String, body: String) {
    val t = AppStorage.currentMBDITheme()
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Box(
            Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colorFromHex(t.gold).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$index", fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold, color = colorFromHex(t.gold))
        }
        Spacer(Modifier.width(MbdiSpace.s))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
            Spacer(Modifier.height(3.dp))
            Text(body, fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text2))
        }
    }
}
