package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
 * 隐私政策（只读整页，v1.5.2 新增）。
 *
 * ★ 以前只有首启/版本更新时的强制弹窗，同意后就没有地方能再看一遍；
 *   本页从「我的 → 隐私条款」进入，只读展示 `assets/privacy_policy.txt` 全文。
 * ★ 想改协议内容只改那个 txt（改完记得把 `AppStorage.PRIVACY_VERSION` +1，老用户会重新弹窗）。
 */
@Composable
fun PrivacyPage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    val body = remember {
        runCatching {
            context.assets.open("privacy_policy.txt").bufferedReader().use { it.readText() }
        }.getOrNull() ?: "隐私政策读取失败，请重新安装后再试。"
    }

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("隐私政策", onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .mbdiCard(shape = RoundedCornerShape(MbdiShape.card))
                    .padding(MbdiSpace.l)
            ) {
                body.split("\n").forEach { line ->
                    when {
                        line.isBlank() -> Spacer(Modifier.height(7.dp))
                        Regex("^[一二三四五六七八九十]+、").containsMatchIn(line) -> Text(
                            line,
                            fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold,
                            color = colorFromHex(t.gold),
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        line.startsWith("▍") -> Text(
                            line,
                            fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                            color = colorFromHex(t.text), lineHeight = 23.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        else -> Text(
                            line,
                            fontSize = MbdiType.Body, lineHeight = 23.sp,
                            color = colorFromHex(t.text2)
                        )
                    }
                }
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}
