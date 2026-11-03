package com.wqmtmbdi.mbcc

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

/**
 * 全 App 统一的沉浸式布局配置：状态栏与导航栏**全部透明**，不画任何遮罩。
 *
 * 为什么不用裸的 `enableEdgeToEdge()`：
 * 它的默认参数会给导航栏刷一层 90% 不透明的白色遮罩（AndroidX 的 DefaultLightScrim），
 * 会在屏幕最底部形成一条"实心"色带，挡住粒子背景。做法与「无期：记账本」的
 * `EdgeToEdge.kt` 一致。
 *
 * [darkIcons] 状态栏 / 导航栏图标是否用**深色**：
 * MBDI 有「纯白指令」「蔷薇之心」两套浅色主题，那时必须传 true，
 * 否则白色图标压在浅色背景上会看不见。深色主题传 false（浅色图标）。
 *
 * 所有 Activity 一律调用本函数，不要再直接调用 `enableEdgeToEdge()`。
 */
fun ComponentActivity.edgeToEdgeTransparent(darkIcons: Boolean = false) {
    runCatching {
        val style = if (darkIcons) {
            // 亮色栏样式 → 图标为深色
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        } else {
            // 深色栏样式 → 图标为浅色
            SystemBarStyle.dark(Color.TRANSPARENT)
        }
        enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
    }
}
