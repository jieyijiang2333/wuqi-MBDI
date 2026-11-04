package com.wqmtmbdi.mbcc.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 首启隐私条款（版本化，对齐 [AppStorage.PRIVACY_VERSION]；不同意则退出）。
 *
 * ★ v1.5.2 用户要求改版：
 *  - **真正全屏**（`usePlatformDefaultWidth = false` + 状态栏/导航栏避让）；
 *  - **删掉阅读进度圆环**，不再强制滑到底；
 *  - 底部改成「小方框勾选 → 右侧确认」：勾选后点确认立即进入；
 *  - 保留「不同意并退出」（二次确认），合规上仍需给出拒绝路径。
 */
@Composable
fun PrivacyDialog(visible: Boolean, onAgree: () -> Unit) {
    if (!visible) return
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var confirmReject by remember { mutableStateOf(false) }
    var agreed by remember { mutableStateOf(false) }

    // 读取隐私全文（读不到时退回内置简版，保证弹窗永远可用）
    val policy = remember {
        runCatching {
            context.assets.open("privacy_policy.txt").bufferedReader().use { it.readText() }
        }.getOrElse { FALLBACK_POLICY }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colorFromHex(t.bg))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                // ---- 标题 ----
                Row(
                    Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Lucide.ShieldCheck, contentDescription = null,
                        modifier = Modifier.size(22.dp), tint = colorFromHex(t.gold)
                    )
                    Text(
                        "隐私条款", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = colorFromHex(t.gold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // ---- 正文：逐行渲染，标题自动加粗 ----
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    policy.split("\n").forEach { line ->
                        when {
                            line.isBlank() -> Spacer(Modifier.height(7.dp))
                            Regex("^[一二三四五六七八九十]+、").containsMatchIn(line) -> Text(
                                line,
                                fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold,
                                color = colorFromHex(t.gold), lineHeight = 24.sp,
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
                    Spacer(Modifier.height(20.dp))
                }

                // ---- 底部：勾选框 + 右侧确认 ----
                Column(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp, bottom = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CheckSquare(checked = agreed) { agreed = !agreed }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "我已阅读并同意《用户协议与隐私声明》",
                            fontSize = MbdiType.Label, color = colorFromHex(t.text2),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        MbdiButton(
                            "确认", onAgree,
                            modifier = Modifier.width(96.dp),
                            style = BtnStyle.Primary,
                            enabled = agreed
                        )
                    }
                    Text(
                        "不同意并退出",
                        fontSize = MbdiType.Caption, color = colorFromHex(t.text3),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { confirmReject = true }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    // 「不同意」二次确认（原来一点就直接杀进程，误触没有回头路）
    if (confirmReject) {
        Dialog(onDismissRequest = { confirmReject = false }) {
            com.wqmtmbdi.mbcc.ui.components.DialogScale {
                Column(
                    Modifier
                        .fillMaxWidth(0.84f)
                        .background(colorFromHex(t.card), RoundedCornerShape(MbdiShape.panel))
                        .padding(MbdiSpace.l),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "不同意并退出？",
                        fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                    )
                    Text(
                        "不同意隐私条款将无法继续使用本应用，应用会立即关闭。",
                        fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text3),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = MbdiSpace.s, bottom = MbdiSpace.l)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                        MbdiButton(
                            "返回", { confirmReject = false },
                            modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                        )
                        MbdiButton(
                            "确认退出",
                            { android.os.Process.killProcess(android.os.Process.myPid()) },
                            modifier = Modifier.weight(1f), style = BtnStyle.Danger
                        )
                    }
                }
            }
        }
    }
}

/** 小方框勾选（20dp，勾上后金色底 + 白勾） */
@Composable
private fun CheckSquare(checked: Boolean, onClick: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    Box(
        Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (checked) colorFromHex(t.gold) else Color.Transparent)
            .border(
                1.5.dp,
                colorFromHex(if (checked) t.gold else t.text3),
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                Lucide.Check, contentDescription = "已同意",
                modifier = Modifier.size(14.dp), tint = colorFromHex(t.bg)
            )
        }
    }
}

/** 兜底简版条款（只在 assets 读取失败时使用） */
private const val FALLBACK_POLICY = """谢谢使用「无期：人格鉴定」。

本应用全部数据（鉴定记录、解锁图鉴、成就、头像等）仅保存在您的设备本地，不经过任何服务器，不上传任何个人信息。应用不读取设备型号、系统版本等任何设备标识信息；本应用为《无期迷途》粉丝二创作品，与官方无关。

如隐私条款内容发生变更，应用会在您再次打开时重新展示并征求同意。继续使用本应用即表示您已阅读并同意上述条款。"""
