package com.wqmtmbdi.mbcc.ui.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.MBDIThemes
import com.wqmtmbdi.mbcc.colorFromHex
import androidx.compose.material3.Icon
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.mbdiSolid
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/** 主题配色弹窗（对齐 HTML #themeOverlay L711-715 + renderThemeGrid L1479） */
@Composable
fun ThemeOverlay(visible: Boolean, onDismiss: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    if (!visible) return
    var tick by remember { mutableStateOf(0) }
    tick
    Dialog(onDismissRequest = onDismiss) {
        Box(Modifier.fillMaxWidth(0.92f).mbdiSolid(shape = RoundedCornerShape(18.dp))) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(Lucide.Palette, contentDescription = null, modifier = Modifier.size(18.dp), tint = colorFromHex(t.gold))
                        Text("主题配色", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = colorFromHex(t.gold), modifier = Modifier.padding(start = 7.dp))
                    }
                    Icon(Lucide.X, contentDescription = "关闭",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onDismiss)
                            .padding(6.dp),
                        tint = colorFromHex(t.text2))
                }
                Text("选择你喜欢的配色方案", fontSize = 12.sp, color = colorFromHex(t.text3),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 10.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MBDIThemes.ALL) { theme ->
                        val active = AppStorage.currentMBDITheme().id == theme.id
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .mbdiCard(shape = RoundedCornerShape(12.dp))
                                .border(
                                    if (active) 1.dp else 0.dp, colorFromHex(t.gold),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    AppStorage.setTheme(theme.id)
                                    tick++
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                theme.swatches.forEach { s ->
                                    Box(
                                        Modifier.size(22.dp).clip(RoundedCornerShape(50))
                                            .background(colorFromHex(s))
                                            .border(1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(theme.name, fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
                                Text(theme.desc, fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
                            }
                            if (active) Icon(Lucide.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = colorFromHex(t.gold))
                        }
                    }
                }
            }
        }
    }
}
