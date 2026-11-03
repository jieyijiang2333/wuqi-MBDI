package com.wqmtmbdi.mbcc.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 首启隐私条款全屏弹窗（版本化，对齐 [AppStorage.PRIVACY_VERSION]；不同意则退出）。
 *
 * ★ 2026-10-01（对齐记账本 / 局长终端口径，用户要求）：
 *  - 正文改为**完整版用户协议与隐私声明**（assets/privacy_policy.txt，十七节），不再是一页短条款；
 *  - **强制阅读**：滚动进度环形指示器，阅读到底部（100%）后「同意并继续」才亮起；
 *  - 「不同意并退出」与「同意并继续」并排；不同意走二次确认后关闭应用；
 *  - 滚动条隐藏、点遮罩与返回键都关不掉（Dialog onDismissRequest 为空）。
 */
@Composable
fun PrivacyDialog(visible: Boolean, onAgree: () -> Unit) {
    if (!visible) return
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var confirmReject by remember { mutableStateOf(false) }

    // 读取隐私全文（读不到时退回内置简版，保证弹窗永远可用）
    val policy = remember {
        runCatching {
            context.assets.open("privacy_policy.txt").bufferedReader().use { it.readText() }
        }.getOrElse { FALLBACK_POLICY }
    }

    val scroll = rememberScrollState()
    var reachedEnd by remember { mutableStateOf(false) }
    val progress by remember {
        derivedStateOf {
            val max = scroll.maxValue
            if (max <= 0) 1f else (scroll.value.toFloat() / max).coerceIn(0f, 1f)
        }
    }
    // 读到过底部就一直算已读（往上滑回去也不撤销）
    LaunchedEffect(progress) {
        if (progress >= 0.999f) reachedEnd = true
    }

    Dialog(onDismissRequest = {}) {
        com.wqmtmbdi.mbcc.ui.components.DialogScale {
        Box(
            Modifier
                .fillMaxSize()
                .background(colorFromHex(t.bg))
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 4.dp),
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
                // 正文：可滚动、无滚动条（Compose 默认不画）+ ★ 底部渐变遮罩（方案 5.8）
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(horizontal = 22.dp)
                    ) {
                        Text(
                            policy,
                            fontSize = 13.sp, lineHeight = 23.sp, color = colorFromHex(t.text2),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, colorFromHex(t.bg))
                                )
                            )
                    )
                }
                // 底部：阅读进度环 + 两个按钮
                Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 20.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        PrivacyProgressRing(progress = if (reachedEnd) 1f else progress)
                        Text(
                            if (reachedEnd) "已读完，可以同意了"
                            else "请滑动阅读到底部，「同意并继续」才会亮起",
                            fontSize = MbdiType.Caption,
                            color = colorFromHex(if (reachedEnd) t.gold else t.text3),
                            modifier = Modifier.padding(start = MbdiSpace.s)
                        )
                    }
                    Spacer(Modifier.height(MbdiSpace.s))
                    Row(horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                        // ★ 以前点「不同意」直接 killProcess，误触就退出；现在先问一句
                        MbdiButton(
                            "不同意并退出", { confirmReject = true },
                            modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                        )
                        MbdiButton(
                            "同意并继续", onAgree,
                            modifier = Modifier.weight(1f), style = BtnStyle.Primary,
                            enabled = reachedEnd
                        )
                    }
                }
            }
        }
        }   // ← DialogScale 结束
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
            }   // ← DialogScale 结束
        }
    }
}

/** 阅读进度环：环形进度 + 百分比数字（读满即 100%） */
@Composable
private fun PrivacyProgressRing(progress: Float) {
    val t = AppStorage.currentMBDITheme()
    val gold = colorFromHex(t.gold)
    val track = colorFromHex(t.brd)
    Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = 3.dp.toPx()
            val d = size.minDimension - sw
            val tl = Offset((size.width - d) / 2f, (size.height - d) / 2f)
            drawArc(
                color = track, startAngle = 0f, sweepAngle = 360f,
                useCenter = false, style = Stroke(sw), topLeft = tl, size = Size(d, d)
            )
            drawArc(
                color = gold, startAngle = -90f, sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false, style = Stroke(sw, cap = StrokeCap.Round), topLeft = tl, size = Size(d, d)
            )
        }
        Text(
            "${(progress * 100).toInt()}",
            fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold, color = gold
        )
    }
}

/** 兜底简版条款（只在 assets 读取失败时使用） */
private const val FALLBACK_POLICY = """谢谢使用「无期：人格鉴定」。

本应用全部数据（鉴定记录、解锁图鉴、成就、头像等）仅保存在您的设备本地，不经过任何服务器，不上传任何个人信息。应用不读取设备型号、系统版本等任何设备标识信息；本应用为《无期迷途》粉丝二创作品，与官方无关。

如隐私条款内容发生变更，应用会在您再次打开时重新展示并征求同意。继续使用本应用即表示您已阅读并同意上述条款。"""