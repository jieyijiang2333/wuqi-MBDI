package com.wqmtmbdi.mbcc.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.CrashRecord
import com.wqmtmbdi.mbcc.CrashLogStore
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.danger
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.DialogScale
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.components.MbdiSheet
import com.wqmtmbdi.mbcc.ui.components.TipBus
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 崩溃日志（第 7 批新增，用户要求）：
 *  - 闪退自动留档（CrashCatcher，见 CrashLog.kt）；
 *  - 列表**自动折叠**：最新一条默认展开，点标题行展开/收起；
 *  - 展开后可「下载」成 txt 文件（或复制堆栈），直接私信发给作者；
 *  - 底部可一次导出全部或清空（清空需二次确认）。
 *  入口：我的 → 我的数据 → 崩溃日志。
 */
@Composable
fun CrashLogOverlay(visible: Boolean, onDismiss: () -> Unit) {
    if (!visible) return
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    tick
    val logs = remember(tick) { CrashLogStore.records(context) }
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    // 折叠状态：最新一条默认展开，其余收起
    var expandedIdx by remember(logs.size) { mutableIntStateOf(if (logs.isEmpty()) -1 else 0) }
    var confirmClear by remember { mutableStateOf(false) }

    // 「下载」→ 系统保存对话框，存成 txt 文件后用户直接发文件
    var pendingRecord by remember { mutableStateOf<CrashRecord?>(null) }
    var pendingAll by remember { mutableStateOf(false) }
    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        val rec = pendingRecord
        val all = pendingAll
        pendingRecord = null
        pendingAll = false
        if (uri == null) return@rememberLauncherForActivityResult
        val ok = try {
            val text = if (all) CrashLogStore.allFileText(context)
            else rec?.let { CrashLogStore.fileText(context, it) } ?: return@rememberLauncherForActivityResult
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(text.toByteArray(Charsets.UTF_8))
                true
            } ?: false
        } catch (_: Exception) {
            false
        }
        TipBus.show(if (ok) "已保存，把文件私信发给作者即可" else "保存失败，换个位置再试一次")
    }

    // 清空二次确认
    if (confirmClear) {
        Dialog(onDismissRequest = { confirmClear = false }) {
            DialogScale {
                Column(
                    Modifier.fillMaxWidth(0.84f)
                        .mbdiSolid(shape = RoundedCornerShape(MbdiShape.panel))
                        .padding(MbdiSpace.l),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "清空崩溃日志？",
                        fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                    )
                    Text(
                        "将删除全部 ${logs.size} 条崩溃记录，此操作不可撤销。",
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
                                CrashLogStore.clear(context)
                                confirmClear = false
                                tick++
                            },
                            modifier = Modifier.weight(1f), style = BtnStyle.Danger
                        )
                    }
                }
            }
        }
    }

    MbdiSheet(
        title = "崩溃日志",
        titleIcon = Lucide.TriangleAlert,
        onDismiss = onDismiss,
        maxHeight = 600.dp,
        footer = {
            if (logs.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                    MbdiButton(
                        "下载全部 txt",
                        {
                            pendingAll = true
                            saveLauncher.launch("崩溃日志-无期人格鉴定-全部.txt")
                        },
                        modifier = Modifier.weight(1f), style = BtnStyle.Secondary
                    )
                    MbdiButton(
                        "清空", { confirmClear = true },
                        modifier = Modifier.weight(1f), style = BtnStyle.Danger
                    )
                }
            }
        }
    ) {
        Text(
            "共 ${logs.size} 条 · 相同内容 5 分钟内自动合并计数 · 最新一条默认展开 · 展开后可下载成 txt 私信发给作者",
            fontSize = MbdiType.Caption, lineHeight = 17.sp, color = colorFromHex(t.text3),
            modifier = Modifier.padding(bottom = MbdiSpace.s)
        )
        if (logs.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = MbdiSpace.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Lucide.ShieldCheck, contentDescription = null, modifier = Modifier.size(30.dp), tint = colorFromHex(t.text3))
                Text(
                    "还没有崩溃记录。\n如果 App 突然闪退，会自动记录在这里，到时把文件私信发给作者即可。",
                    fontSize = MbdiType.Body, lineHeight = 21.sp, color = colorFromHex(t.text3),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = MbdiSpace.s)
                )
            }
        } else {
            logs.forEachIndexed { i, log ->
                val isExp = i == expandedIdx
                // 折叠头：箭头（展开时旋转 90°）+ 时间 + 次数徽标
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MbdiShape.small))
                        .clickable { expandedIdx = if (isExp) -1 else i }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Lucide.ChevronRight, contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                            .graphicsLayer { rotationZ = if (isExp) 90f else 0f },
                        tint = colorFromHex(t.gold)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        fmt.format(Date(log.time)),
                        fontSize = MbdiType.Label, fontWeight = FontWeight.Bold,
                        color = colorFromHex(t.text)
                    )
                    Spacer(Modifier.weight(1f))
                    if (log.count > 1) {
                        Text(
                            "x${log.count}",
                            fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold,
                            color = colorFromHex(t.danger),
                            modifier = Modifier
                                .background(colorFromHex(t.danger).copy(alpha = 0.12f), RoundedCornerShape(MbdiShape.pill))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                if (isExp) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(MbdiShape.small))
                            .background(colorFromHex(t.card).copy(alpha = 0.85f))
                            .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.small))
                            .padding(MbdiSpace.m)
                    ) {
                        Text(
                            log.stack,
                            fontSize = MbdiType.Caption, lineHeight = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            color = colorFromHex(t.text2)
                        )
                        Spacer(Modifier.height(MbdiSpace.s))
                        Row(horizontalArrangement = Arrangement.spacedBy(MbdiSpace.s)) {
                            MbdiButton(
                                "下载 txt",
                                {
                                    pendingRecord = log
                                    saveLauncher.launch("崩溃日志-无期人格鉴定-${fmt.format(Date(log.time)).replace(":", "").replace(" ", "-")}.txt")
                                },
                                style = BtnStyle.Secondary
                            )
                            MbdiButton(
                                "复制堆栈",
                                {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("crash", log.stack))
                                    TipBus.show("堆栈已复制")
                                },
                                style = BtnStyle.Text
                            )
                        }
                    }
                    Spacer(Modifier.height(MbdiSpace.xs))
                }
            }
        }
    }
}