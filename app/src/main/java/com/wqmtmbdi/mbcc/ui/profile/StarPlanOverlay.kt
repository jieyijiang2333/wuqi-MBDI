package com.wqmtmbdi.mbcc.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.R
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.mbdiCard
import com.wqmtmbdi.mbcc.ui.components.BtnStyle
import com.wqmtmbdi.mbcc.ui.components.MbdiButton
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.MbdiShape
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 星辰计划（支持作者）—— v1.5.2 精简版（用户要求：删掉全部回馈福利，只留打赏与感谢）。
 *
 * 页面只有三块：**扫码打赏 → 一段感谢 → 免责声明入口**。
 * ★ 不再提供任何档位 / 权益 / 提前体验 / 互关等回馈；打赏就是纯粹的支持。
 * ★ 免责声明全文在独立页 [StarPlanTermsPage]（条款见 [StarPlanTerms]）。
 * ★ 收款码图片：res/drawable/wechat_qr.png、alipay_qr.jpg（换码就替换这两份）。
 */
@Composable
fun StarPlanPage(onOpenTerms: () -> Unit, onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    var zoom by remember { mutableStateOf(-1) }

    Column(Modifier.fillMaxSize()) {
        // 顶栏：返回 + 标题（对齐图鉴详情页内的整页导航习惯）
        Row(
            Modifier.fillMaxWidth().padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50)).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Lucide.ArrowLeft, contentDescription = "返回",
                    modifier = Modifier.size(20.dp), tint = colorFromHex(t.gold)
                )
            }
            Icon(Lucide.Sparkle, contentDescription = null, modifier = Modifier.size(18.dp), tint = colorFromHex(t.gold))
            Text(
                "星辰计划", fontSize = MbdiType.Title, fontWeight = FontWeight.Bold,
                color = colorFromHex(t.gold), letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
                .padding(bottom = 100.dp)   // 让开底部悬浮导航
        ) {
            // ========== 导语 ==========
            Text(
                "打赏支持作者。软件全部功能免费，打赏不改变功能使用。",
                fontSize = MbdiType.Body, lineHeight = 21.sp, color = colorFromHex(t.text2),
                modifier = Modifier.padding(start = 2.dp, top = MbdiSpace.xs)
            )

            Spacer(Modifier.height(MbdiSpace.xl))

            // ========== ① 扫码打赏（二维码置顶） ==========
            StarCardTitle("扫码打赏", "微信 / 支付宝扫码都可以，打多少都是心意")
            Spacer(Modifier.height(MbdiSpace.s))
            StarCard {
                Row(Modifier.fillMaxWidth(0.86f).align(Alignment.CenterHorizontally)) {
                    QrTile(QR_LIST[0], Modifier.weight(1f)) { zoom = 0 }
                    Spacer(Modifier.width(14.dp))
                    QrTile(QR_LIST[1], Modifier.weight(1f)) { zoom = 1 }
                }
                Spacer(Modifier.height(MbdiSpace.m))
                Text(
                    "点一下二维码可以放大，放大后更容易扫；也可以截图保存，用另一台设备扫。",
                    fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text3)
                )
            }

            Spacer(Modifier.height(MbdiSpace.xxl))

            // ========== ② 感谢 ==========
            StarCardTitle("写在最后", null)
            Spacer(Modifier.height(MbdiSpace.s))
            StarCard {
                Text(
                    "感谢每一位支持过这个软件的人。",
                    fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp, color = colorFromHex(t.text)
                )
                Spacer(Modifier.height(MbdiSpace.s))
                Text(
                    "打赏是自愿的心意，不附带任何权益或回报。软件将停止更新，感谢一路以来的支持。",
                    fontSize = MbdiType.Label, lineHeight = 20.sp, color = colorFromHex(t.text2)
                )
            }

            Spacer(Modifier.height(MbdiSpace.xxl))

            // ========== ③ 免责声明（摘要 + 入口，全文独立页） ==========
            Text(
                "打赏性质与相关规则以《免责声明》为准。",
                fontSize = MbdiType.Caption, lineHeight = 18.sp, color = colorFromHex(t.text3),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(MbdiSpace.s))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MbdiShape.card))
                    .background(colorFromHex(t.card).copy(alpha = 0.92f))
                    .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.card))
                    .clickable(onClick = onOpenTerms)
                    .padding(horizontal = MbdiSpace.l, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Lucide.ShieldCheck, contentDescription = null,
                    modifier = Modifier.size(16.dp), tint = colorFromHex(t.gold)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "免责声明（共 ${StarPlanTerms.CLAUSES.size} 条）", Modifier.weight(1f),
                    fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(t.text)
                )
                Icon(
                    Lucide.ChevronRight, contentDescription = null,
                    modifier = Modifier.size(16.dp), tint = colorFromHex(t.text3)
                )
            }

            Spacer(Modifier.height(MbdiSpace.xl))
        }
    }

    // 点开收款码：放大再扫（两张并排时尺寸偏小）
    if (zoom in QR_LIST.indices) {
        QrZoomDialog(QR_LIST[zoom]) { zoom = -1 }
    }
}

// ===================== 收款码 =====================

/** 收款码：名称 + 资源 id + 扫码提示 */
private data class Qr(val label: String, @DrawableRes val resId: Int, val tip: String)

private val QR_LIST = listOf(
    Qr("微信打赏", R.drawable.wechat_qr, "微信扫一扫"),
    Qr("支付宝打赏", R.drawable.alipay_qr, "支付宝扫一扫")
)

/** 一张收款码：白底方框 + 名称，点一下放大 */
@Composable
private fun QrTile(item: Qr, modifier: Modifier = Modifier, onZoom: () -> Unit) {
    val t = AppStorage.currentMBDITheme()
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(MbdiShape.card))
                // 收款码必须白底、不叠主题色，保证任何配色下都能扫
                .background(androidx.compose.ui.graphics.Color.White)
                .border(1.dp, colorFromHex(t.brd), RoundedCornerShape(MbdiShape.card))
                .clickable(onClick = onZoom)
                .padding(8.dp)
        ) {
            Image(
                painterResource(item.resId),
                contentDescription = item.label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(MbdiSpace.s))
        Text(item.label, fontSize = MbdiType.Label, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
    }
}

/** 收款码放大页：白底铺满，方便扫（同样刻意用白底） */
@Composable
private fun QrZoomDialog(item: Qr, onDismiss: () -> Unit) {
    val white = androidx.compose.ui.graphics.Color.White
    Dialog(onDismissRequest = onDismiss) {
        com.wqmtmbdi.mbcc.ui.components.DialogScale {
            Column(
                Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(MbdiShape.panel))
                    .background(white)
                    .padding(MbdiSpace.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    item.label, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF1B1B1B)
                )
                Spacer(Modifier.height(MbdiSpace.m))
                Image(
                    painterResource(item.resId),
                    contentDescription = item.label,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(MbdiSpace.s))
                Text(
                    "用另一台设备扫（${item.tip}）；也可以直接截图保存这张图。",
                    fontSize = MbdiType.Caption, lineHeight = 18.sp,
                    color = androidx.compose.ui.graphics.Color(0xFF6B6B6B),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(MbdiSpace.m))
                MbdiButton("关闭", onDismiss, modifier = Modifier.fillMaxWidth(), style = BtnStyle.Primary)
            }
        }
    }
}

// ===================== 页内小组件 =====================

/** 小节标题（可选副标题、可选前置图标） */
@Composable
private fun StarCardTitle(
    title: String,
    sub: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val t = AppStorage.currentMBDITheme()
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = colorFromHex(t.gold))
            Spacer(Modifier.width(6.dp))
        }
        Column(Modifier.padding(start = 2.dp)) {
            Text(title, fontSize = MbdiType.Body, fontWeight = FontWeight.Bold, color = colorFromHex(t.text))
            if (sub != null) {
                Text(sub, fontSize = MbdiType.Caption, lineHeight = 17.sp, color = colorFromHex(t.text3))
            }
        }
    }
}

/** 统一卡片底（磨砂面板材质） */
@Composable
private fun StarCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().mbdiCard(shape = RoundedCornerShape(MbdiShape.card)).padding(MbdiSpace.m),
        content = content
    )
}
