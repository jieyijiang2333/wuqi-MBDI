package com.wqmtmbdi.mbcc.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.AvatarCropActivity
import com.wqmtmbdi.mbcc.AvatarStore
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.danger
import com.wqmtmbdi.mbcc.data.CHARACTERS
import com.wqmtmbdi.mbcc.data.GameChar
import com.wqmtmbdi.mbcc.data.rankOf
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.components.TipBus
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType
import java.io.File

/** 把相册 Uri 复制到缓存文件（供 AvatarCropActivity 读取） */
fun copyToCache(context: Context, uri: Uri): File? = try {
    val tmp = File(context.cacheDir, "avatar_src_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        tmp.outputStream().use { out -> input.copyTo(out) }
    }
    tmp
} catch (e: Exception) {
    null
}

/** 等级分组顺序（与图鉴同口径） */
private val RANK_ORDER = listOf("狂厄级", "高危级", "普管级", "特殊")

/**
 * 禁闭者形象（v1.5.2：弹窗改独立整页，用户要求）。
 *
 * - **一行三个**，每个角色一行内「左头像 · 右名字」，下面一排按钮（未上传 = 上传；已上传 = 更换 / 删除）；
 * - 按**图鉴的等级**分组：狂厄级 / 高危级 / 普管级 / 特殊；
 * - 入口：我的 → 禁闭者形象。
 */
@Composable
fun AvatarPage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    var pendingKey by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableIntStateOf(0) }

    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        tick++   // 裁剪返回后刷新列表
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val key = pendingKey ?: return@rememberLauncherForActivityResult
        if (uri == null) return@rememberLauncherForActivityResult
        val tmp = copyToCache(context, uri)
        if (tmp != null) {
            cropLauncher.launch(
                Intent(context, AvatarCropActivity::class.java)
                    .putExtra("src", tmp.absolutePath)
                    .putExtra("key", key)
            )
        } else {
            TipBus.show("这张图读不出来，换一张试试")
        }
    }

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("禁闭者形象", onBack)
        Text(
            "点头像或「上传」选择图片，裁剪为圆形后生效；已上传的可以更换或删除。",
            fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text3),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            RANK_ORDER.forEach { rank ->
                val list = CHARACTERS.filter { rankOf(it.id) == rank }
                if (list.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            Modifier.fillMaxWidth().padding(start = 2.dp, top = 8.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                rank, fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold,
                                color = colorFromHex(t.gold), letterSpacing = 1.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "${list.size}", fontSize = MbdiType.Caption,
                                color = colorFromHex(t.text3)
                            )
                        }
                    }
                    items(list, key = { it.id }) { c ->
                        AvatarCell(
                            char = c,
                            tick = tick,
                            onPick = { pendingKey = c.id; picker.launch("image/*") },
                            onDelete = {
                                AvatarStore.clearAvatar(context, c.id)
                                AppStorage.data.customAvatars.remove(c.id)
                                AppStorage.save()
                                tick++
                                TipBus.show("已删除「${c.name}」的头像")
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 单个角色格：左头像 + 右名字，下面一排按钮 */
@Composable
private fun AvatarCell(
    char: GameChar,
    tick: Int,
    onPick: () -> Unit,
    onDelete: () -> Unit
) {
    val t = AppStorage.currentMBDITheme()
    val context = LocalContext.current
    val bmp = remember(char.id, tick) { AvatarStore.loadAvatar(context, char.id) }
    val has = bmp != null

    Column(
        Modifier.fillMaxWidth().mbdiCard(shape = RoundedCornerShape(MbdiShape.card)).padding(7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorFromHex(char.avatarColor))
                    .clickable(onClick = onPick),
                contentAlignment = Alignment.Center
            ) {
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(), contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50))
                    )
                } else {
                    Text(
                        char.code.firstOrNull()?.toString() ?: "?",
                        fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            Text(
                char.name,
                fontSize = MbdiType.Caption, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.text), maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(6.dp))
        if (has) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallAction("更换", colorFromHex(t.gold), Modifier.weight(1f), onPick)
                SmallAction("删除", colorFromHex(t.danger), Modifier.weight(1f), onDelete)
            }
        } else {
            SmallAction("上传", colorFromHex(t.gold), Modifier.fillMaxWidth(), onPick)
        }
    }
}

/** 格内小按钮（胶囊底 + 细文字） */
@Composable
private fun SmallAction(text: String, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    Text(
        text,
        modifier = modifier
            .clip(RoundedCornerShape(MbdiShape.pill))
            .background(accent.copy(alpha = 0.14f))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        fontSize = MbdiType.Caption,
        fontWeight = FontWeight.Bold,
        color = accent,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}
