package com.wqmtmbdi.mbcc.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ★ MBDI 设计令牌（全 App 唯一来源，改这里就是改全 App）
 *
 * 规则（见《人格鉴定-UI重做方案.md》第三章）：
 *  - 字号只用这 6 档，**下限 11sp**，不许再出现 10sp 及以下；
 *  - 间距只用 4 的倍数；
 *  - 圆角只留 3 档 + 胶囊。
 *  新写界面一律从这里取，不要再手写 `13.sp` / `7.dp` 这种散值。
 */
object MbdiType {
    /** 结果页角色名（全 App 唯一） */
    val Display = 34.sp
    /** 四字代号（结果页 / 图鉴详情） */
    val Code = 26.sp
    /** 页面大标题 */
    val Title = 20.sp
    /** 区块标题 / 卡片标题 */
    val Heading = 16.sp
    /** 正文 / 题目 / 选项卡 */
    val Body = 14.sp
    /** 次要信息 / 按钮文字 */
    val Label = 12.sp
    /** 辅助说明 —— 全 App 字号下限 */
    val Caption = 11.sp
    /** 数字强调（统计数字、百分比） */
    val Number = 22.sp
}

object MbdiSpace {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}

object MbdiShape {
    val small = 8.dp      // 标签 / 输入框
    val card = 16.dp      // 卡片
    val panel = 24.dp     // 全屏面板 / 大弹窗
    val pill = 999.dp     // 按钮 / chip
}

object MbdiMotion {
    const val FAST = 180       // 微反馈（按下、切换）
    const val NORMAL = 280     // 展开收起
    const val PAGE = 450       // 页面级
    const val REVEAL = 1500    // 雷达图生长
    const val REVEAL_TOTAL = 4500  // 结果页揭晓仪式总时长（2026-10-01 四幕编排：静默→洗档→锁定→契合）
}

/** 点击热区规范：任何可点元素不小于这个尺寸 */
val MinTouchTarget = 44.dp

/**
 * 系统是否允许播放动画（跟着「开发者选项 → 动画时长缩放」走）。
 * 关掉时所有动画直接跳到终值，避免界面空白或卡顿。
 */
@Composable
fun rememberAnimationsOn(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) > 0f
        }.getOrDefault(true)
    }
}

/**
 * ★ 弹簧规格表（2026-10-01 设计系统 2.0，见《人格鉴定-界面深度设计方案.md》3.1）：
 * 交互反馈类运动一律走这四条弹簧（不再用时长曲线）；数值滚动 / 进度条等线性量继续用 tween。
 */
object MbdiSpring {
    /** 按钮 / 选项 / chip：快、几乎无回弹 */
    val Snappy: SpringSpec<Float> = spring(dampingRatio = 0.90f, stiffness = 1100f)
    /** 卡片展开收起 / 列表入场：有一点「落定感」 */
    val Soft: SpringSpec<Float> = spring(dampingRatio = 0.75f, stiffness = 420f)
    /** 页面级转场 / 氛围迁移：稳、慢、无回弹 */
    val Gentle: SpringSpec<Float> = spring(dampingRatio = 1.00f, stiffness = 190f)
    /** 仅 Wow 时刻（揭晓落定 / 解锁闪光）：允许明显回弹 */
    val Bouncy: SpringSpec<Float> = spring(dampingRatio = 0.55f, stiffness = 380f)
}

/**
 * 安静模式（方案第六章）：系统关动画，或低端机（低内存）——
 * 所有「可关闭的增强」（氛围光呼吸、扫光循环、真模糊、Wow 演出）先问它，退化为静态 / 直接呈现。
 */
@Composable
fun rememberQuietMode(): Boolean {
    val animationsOn = rememberAnimationsOn()
    val context = LocalContext.current
    val lowRam = remember {
        runCatching {
            (context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager)
                .isLowRamDevice
        }.getOrDefault(false)
    }
    return !animationsOn || lowRam
}
