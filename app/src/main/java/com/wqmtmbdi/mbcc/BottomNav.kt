package com.wqmtmbdi.mbcc

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.wqmtmbdi.mbcc.ui.icons.Lucide
import com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

/** 底部导航的三个标签页（对齐 HTML bottomNav：测试/图鉴/我的） */
enum class MbdiTab(val title: String, val icon: ImageVector) {
    TEST("测试", Lucide.ClipboardList),
    GALLERY("图鉴", Lucide.BookOpen),
    PROFILE("我的", Lucide.UserRound)
}

/** 胶囊容器圆角（容器高约 60dp，取一半 → 正胶囊形） */
private val NavShape = RoundedCornerShape(999.dp)

/**
 * 底部导航 —— 完全照「无期：记账本」的方案做（见《记账本 开发文档》「导航与边到边（5 tab）」）。
 *
 * 三条必须遵守的做法：
 *  ① **外层容器只负责布局，不带任何整条背景色带**：左右各留 12dp、底部留 12dp，
 *     留白处与胶囊下方**必须透出页面背景**（"底部实心白条"就是这么来的）。
 *  ② **绝对不要给每个 item 挂 `clickable`** —— material 涟漪在**长按**时会画出一块
 *     **长方形灰色蒙版**（用户明确要求删掉）。整条 `Row` 自己接管手势。
 *  ③ **长按 → 拖拽换页**：`pointerInput + awaitEachGesture` 手写。
 *     ① 先等长按超时（超时前抬手 = 点击；滑动超过 `touchSlop` = 忽略，避免误切页）；
 *     ② 长按成立 → `dragIdx` 跟着手指走（指示器也跟着动），松手切到手指所在那一页。
 *
 * 位置换算：每项占 `itemW + spacing`，`index = (x / stepPx).toInt()`；
 * `itemW` 来自 `BoxWithConstraints`，`toPx()` 在 `PointerInputScope`（实现了 Density）里可直接用。
 */
@Composable
fun BottomNav(
    current: MbdiTab,
    onTabSelected: (MbdiTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = AppStorage.currentMBDITheme()
    val items = MbdiTab.entries
    val itemH = 48.dp
    val spacing = 4.dp

    val gold = colorFromHex(t.gold)
    val goldDim = colorFromHex(t.goldDim)
    val activeText = colorFromHex(t.onAccent)   // ★ 第 8 批：金胶囊上的文字用主题 onAccent（深色底）
    val idleText = colorFromHex(t.text3)

    // ★ 切换时轻触感（第 6 批）；第 8 批删掉了选中胶囊的循环扫光
    val quiet = rememberQuietMode()
    val haptics = LocalHapticFeedback.current

    fun selectTab(tab: MbdiTab) {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onTabSelected(tab)
    }

    // 长按拖拽时手指所在的那一页（-1 = 没在拖拽）；指示器会跟着它走
    var dragIdx by remember { mutableIntStateOf(-1) }

    // —— 外层容器：纯布局，零背景零描边零裁剪，只负责与手机边框留空隙 ——
    Box(
        modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
        // —— 内层胶囊本体：圆角 + 半透明渐变底 + 淡描边 + 淡投影 ——
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp, shape = NavShape,
                    ambientColor = Color(0x52000000), spotColor = Color(0x40000000)
                )
                .clip(NavShape)
                .background(
                    // ★ 第 8 批：深色玻璃胶囊（主题面板色 + 半透明），不再是白色底
                    Brush.verticalGradient(
                        listOf(
                            colorFromHex(t.card).copy(alpha = 0.94f),
                            colorFromHex(t.bg).copy(alpha = 0.88f)
                        )
                    )
                )
                .border(1.dp, colorFromHex(t.brd).copy(alpha = 0.9f), NavShape)
                .padding(6.dp)
        ) {
            val itemW = (maxWidth - spacing * (items.size - 1)) / items.size

            // 选中胶囊的平移偏移量：跟随当前选中项平滑动画；拖拽时跟着手指走
            val visualIdx = if (dragIdx in items.indices) dragIdx else items.indexOf(current).coerceAtLeast(0)
            val offset by animateDpAsState(
                targetValue = itemW * visualIdx + spacing * visualIdx,
                animationSpec = tween(
                    durationMillis = if (dragIdx >= 0) 120 else 320,
                    easing = FastOutSlowInEasing
                ),
                label = "navIndicator"
            )

            // 底层：渐变选中胶囊（滑动指示器）—— ★ 第 8 批：去掉循环扫光，保持干净的金属金
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = offset)
                    .width(itemW)
                    .height(itemH)
                    .clip(RoundedCornerShape(25.dp))
                    .background(
                        Brush.horizontalGradient(listOf(gold, goldDim)),
                        RoundedCornerShape(25.dp)
                    )
            )

            // 顶层：整条 Row 自己接管手势（点按 / 长按拖拽），item 上**不挂 clickable**
            Row(
                Modifier
                    .fillMaxWidth()
                    .pointerInput(items.size, itemW) {
                        val stepPx = (itemW + spacing).toPx()
                        fun idxAt(x: Float): Int = (x / stepPx).toInt().coerceIn(0, items.size - 1)
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val startX = down.position.x
                            var curX = startX
                            var moved = false
                            // ① 先等长按（超时前若抬手 = 点击；若滑动超过阈值 = 不算长按）
                            val longPressed = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                while (true) {
                                    val e = awaitPointerEvent()
                                    val ch = e.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!ch.pressed) break
                                    curX = ch.position.x
                                    if (abs(curX - startX) > viewConfiguration.touchSlop) {
                                        moved = true
                                        break
                                    }
                                }
                                false
                            }
                            if (longPressed == null) {
                                // ② 长按成立 → 进入拖拽：指示器跟着手指走，松手切到那一页
                                dragIdx = idxAt(curX)
                                while (true) {
                                    val e = awaitPointerEvent()
                                    val ch = e.changes.firstOrNull { it.id == down.id }
                                    if (ch != null && ch.pressed) {
                                        curX = ch.position.x
                                        dragIdx = idxAt(curX)
                                    }
                                    if (e.changes.none { it.pressed }) break
                                }
                                dragIdx.let { if (it in items.indices) selectTab(items[it]) }
                                dragIdx = -1
                            } else if (!moved) {
                                // ③ 普通点击（没长按也没滑动）
                                selectTab(items[idxAt(startX)])
                            }
                            // ④ 没长按就滑动的：忽略（避免误切页）
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                items.forEach { tab ->
                    val isSel = tab == current
                    // ★ 方案 5.9：切换项时图标上移 1dp（Snappy 弹簧，安静模式直接跳）
                    val lift by animateDpAsState(
                        targetValue = if (isSel) (-1).dp else 0.dp,
                        animationSpec = if (quiet) tween(0)
                        else androidx.compose.animation.core.spring(dampingRatio = 0.90f, stiffness = 1100f),
                        label = "navLift"
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(itemH)
                            // ★ 第 6 批无障碍：读屏能报出「已选中的第几个页签」
                            .semantics { selected = isSel; contentDescription = tab.title },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).offset(y = lift),
                                tint = if (isSel) activeText else idleText
                            )
                            Text(
                                tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) activeText else idleText
                            )
                        }
                    }
                }
            }
        }
    }
}
