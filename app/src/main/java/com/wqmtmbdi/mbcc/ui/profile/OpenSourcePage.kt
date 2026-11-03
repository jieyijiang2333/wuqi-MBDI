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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 开源组件（v1.5.1 新增，用户要求）。
 *
 * 列出本软件实际使用的第三方开源项目 + 用途 + 许可证，方便用户与合规审查查阅。
 * ★ 加依赖时顺手在这里补一行（版本号照 app/build.gradle 写）。
 */
@Composable
fun OpenSourcePage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("开源组件", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Text(
                "本软件在开发中使用了以下开源项目，感谢它们的作者与社区。各组件均按其自身的开源许可证使用。",
                fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text3),
                modifier = Modifier.padding(top = MbdiSpace.s, bottom = MbdiSpace.m)
            )

            SectionCard(title = "运行时与界面") {
                OssRow("Kotlin", "2.0.21", "编程语言与标准库", "Apache License 2.0")
                OssDivider()
                OssRow("Kotlin Coroutines", "随 Kotlin / Compose 提供", "异步任务与并发", "Apache License 2.0")
                OssDivider()
                OssRow(
                    "Jetpack Compose", "BOM 2024.09.03",
                    "声明式 UI 框架（Runtime / UI / Foundation / Material 3）", "Apache License 2.0"
                )
                OssDivider()
                OssRow("AndroidX Core KTX", "1.13.1", "Android 核心功能扩展", "Apache License 2.0")
                OssDivider()
                OssRow("Activity Compose", "1.9.2", "Compose 与 Activity 集成", "Apache License 2.0")
                OssDivider()
                OssRow("Gson", "2.10.1", "JSON 序列化（本地存档读写）", "Apache License 2.0")
            }

            Spacer(Modifier.height(MbdiSpace.m))

            SectionCard(title = "图标") {
                OssRow("Lucide", "0.469.0", "界面图标（47 个，ISC 许可）", "ISC License")
            }

            Spacer(Modifier.height(MbdiSpace.m))

            SectionCard(title = "测试与构建") {
                OssRow("JUnit 4", "4.13.2", "单元测试框架（仅构建期使用，不随安装包分发）", "Eclipse Public License 1.0")
            }

            Spacer(Modifier.height(MbdiSpace.l))
            Text(
                "许可证全文可在对应项目的官方仓库查看。",
                fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(60.dp))
        }
    }
}

/** 一行开源组件：名称 + 版本 + 用途 + 许可证 */
@Composable
private fun OssRow(name: String, version: String?, use: String, license: String) {
    val t = AppStorage.currentMBDITheme()
    Column(Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                name, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.text), modifier = Modifier.weight(1f)
            )
            Text(license, fontSize = MbdiType.Caption, color = colorFromHex(t.purple))
        }
        Text(
            buildString {
                if (!version.isNullOrBlank()) append(version).append(" · ")
                append(use)
            },
            fontSize = MbdiType.Caption, lineHeight = 17.sp, color = colorFromHex(t.text3),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun OssDivider() {
    val t = AppStorage.currentMBDITheme()
    Box(Modifier.fillMaxWidth().height(1.dp).background(colorFromHex(t.brd)))
}
