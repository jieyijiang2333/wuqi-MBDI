package com.wqmtmbdi.mbcc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.engine.DcpmEngine
import com.wqmtmbdi.mbcc.ui.common.PrivacyDialog
import com.wqmtmbdi.mbcc.ui.gallery.GalleryScreen
import com.wqmtmbdi.mbcc.ui.profile.ProfileScreen
import com.wqmtmbdi.mbcc.ui.render.EnvironmentLayer
import com.wqmtmbdi.mbcc.ui.test.TestStage
import com.wqmtmbdi.mbcc.ui.test.TestState
import com.wqmtmbdi.mbcc.ui.test.TestTabScreen
import com.wqmtmbdi.mbcc.ui.test.resetToHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ★ 第 9 批（长期性修复 · 用户要求）：全屏 / 独立页面的「系统外壳」状态。
 *
 * 以前「导航栏隐藏」是各页面自己记着处理的（答题页写死在 MainActivity、图鉴详情和
 * 星辰计划一新增就忘了），所以反复出现「独立页面打开后导航栏还在」。
 * 现在统一成一个开关：任何整页在进入时
 *  - `AppChrome.fullscreen = true`  → 底部导航自动隐藏；
 *  - `AppChrome.onBack = { … }`     → 返回键先交给该页（返回上一级），而不是退出 App；
 * 页面离开时在 DisposableEffect 的 onDispose 里复位。**新增整页只要登记一次即可。**
 */
object AppChrome {
    var fullscreen by mutableStateOf(false)
    var onBack: (() -> Boolean)? = null
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ★ 第 7 批：全局崩溃自动留档（先于一切初始化，闪退也能记下来）
        CrashCatcher.install(applicationContext)
        AppStorage.init(applicationContext)
        DcpmEngine.init()
        // 后台校准（对齐 HTML setTimeout 10ms 语义；校准完成前均匀先验也可正常匹配）。
        // ★ v1.5.0：角色池扩到 98 名后，全量蒙特卡洛模拟耗时明显变长 —— 改为
        // 「只在无缓存 / 缓存签名不符时跑一次」，缓存命中直接复用（先验是软的，不影响正确性）。
        if (AppStorage.loadCalib(DcpmEngine.CALIB_SIG) == null) {
            CoroutineScope(Dispatchers.Default).launch {
                runCatching { DcpmEngine.calibrate() }
            }
        }
        edgeToEdgeTransparent()
        setContent {
            MBDITheme {
                // 状态栏 / 导航栏图标颜色跟着当前主题明暗走：浅色主题（纯白指令 / 蔷薇之心）用深色图标
                val darkIcons = AppStorage.currentMBDITheme().isLight()
                SideEffect { edgeToEdgeTransparent(darkIcons = darkIcons) }
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val t = AppStorage.currentMBDITheme()
    var currentTab by remember { mutableStateOf(MbdiTab.TEST) }
    // ★ 测试状态提到这里持有：切到图鉴/我的再切回来，答题进度与结果都还在
    val testState = remember { TestState() }
    // ★ 方案 5.10：零镜启动——冷启动时首页做一次淡入（底下是 windowBackground 的静态启动帧）
    val quiet = com.wqmtmbdi.mbcc.ui.theme.rememberQuietMode()
    val animOn = com.wqmtmbdi.mbcc.ui.theme.rememberAnimationsOn()
    val rootFade = remember { androidx.compose.animation.core.Animatable(if (quiet || !animOn) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!(quiet || !animOn)) {
            rootFade.animateTo(1f, androidx.compose.animation.core.tween(320, easing = androidx.compose.animation.core.FastOutSlowInEasing))
        }
    }
    var showPrivacy by remember {
        mutableStateOf(AppStorage.privacyAgreedVersion() < AppStorage.PRIVACY_VERSION)
    }
    // ★ 第 13 批（重做公告逻辑）：公告状态提升到全局（AppRoot），不放在「测试页」里 ——
    //   打开 App 时若有未读公告，自动弹一次（等价于「公告内容更新才会再弹」）；
    //   铃铛点击、自动弹出都走这一个状态；关闭（确认 / ✕ / 遮罩）即写已读，之后不再自动弹。
    //   自动弹前会等隐私弹窗先关掉（首启场景），保证公告不被盖住、也不会被漏掉。
    var showAnn by remember { mutableStateOf(false) }
    /** true = 这次是「启动自动打开」——直接落在最新公告详情；false = 铃铛点击，进列表 */
    var annAtLatest by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!AppStorage.isAnnouncementUnread()) return@LaunchedEffect
        snapshotFlow { showPrivacy }.first { !it }   // 隐私弹窗未关时先等它
        delay(350)                                    // 让首页先落定，再来公告
        if (AppStorage.isAnnouncementUnread()) {
            annAtLatest = true
            showAnn = true
        }
    }

    // ---- 返回键 ----
    // ★ v1.5.2 用户要求：删除「预测性返回」的手势预览动画（幽灵层 + 推走 / 模糊），只保留返回逻辑。
    //   优先级不变：独立页交回该页 → 答题途中先确认 → 非首页回首页 → 其他 Tab 回「测试」→ 退出 App。
    val stageNow = testState.stage
    val canGoBack = currentTab != MbdiTab.TEST || stageNow != TestStage.HOME || AppChrome.fullscreen
    BackHandler(enabled = canGoBack) {
        when {
            AppChrome.onBack?.invoke() == true -> { }                        // 独立页：先交出返回权（返回上一级）
            stageNow == TestStage.QUESTION -> testState.pendingExit = true   // 答题中：先问一句，不直接退
            stageNow != TestStage.HOME -> resetToHome(testState)
            else -> currentTab = MbdiTab.TEST
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = rootFade.value }   // 零镜启动的首页淡入（绘制期读值，不重组）
            .background(colorFromHex(t.bg))
    ) {
        // 环境层（z 底层，指针穿透）—— 氛围光 + 粒子 + 纸纹底噪三合一（设计系统 2.0，方案 4.1）
        // ★ 答题页 / 结果页粒子降到 24 个：那两页有内容动画在跑，把 GPU 让出来，低端机才不掉帧
        val busyStage = stageNow == TestStage.QUESTION || stageNow == TestStage.RESULT
        EnvironmentLayer(
            Modifier.fillMaxSize(),
            particleCount = if (busyStage) 24 else 66,
            accent = colorFromHex(t.gold),
            accent2 = colorFromHex(t.purple),
            pageIndex = currentTab.ordinal   // 氛围光随页面平滑迁移（方案 5.10）
        )

        // 前景层（内容 + 底部导航）
        Box(Modifier.fillMaxSize()) {
        // ★ 内容层顶部避让状态栏：整体从状态栏下方开始，界面不会再和手机状态栏重合
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            // ★★ 内容区一路延伸到屏幕底部（weight(1f) 占满剩余高度）——
            //    **不给底部导航预留高度**，这样底部留白处与胶囊下方都能透出内容与粒子背景
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    MbdiTab.TEST -> TestTabScreen(testState, onOpenAnnouncement = { annAtLatest = false; showAnn = true })
                    // 档案详情的「去找和 TA 匹配的答案」要能直接跳到测试页
                    MbdiTab.GALLERY -> GalleryScreen(onGoTest = { currentTab = MbdiTab.TEST })
                    MbdiTab.PROFILE -> ProfileScreen()
                }
            }
        }
        // ★★ 悬浮胶囊导航栏（对齐记账本：`Box(fillMaxWidth()).align(BottomCenter)`）：
        //    放在根 Box 最上层、底部居中，**不在 Column 内、不占内容高度** ——
        //    内容会从半透明胶囊后面穿过，左右留白与下方空隙都透出底层
        //    （"底部实心白条 / 看不到下面图层"就是这么来的）。
        //    ★ 2026-10-02 第 9 批（长期性修复）：改为统一开关 ——
        //    答题（QUESTION）/ 结果（RESULT）与**所有登记的独立页**（AppChrome.fullscreen：
        //    图鉴详情、星辰计划、反馈中心…）都会自动隐藏导航，新增整页不用再改这里。
        val hideNav = testState.stage == TestStage.QUESTION ||
            testState.stage == TestStage.RESULT ||
            AppChrome.fullscreen
        if (!hideNav) {
            Box(Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                BottomNav(
                    current = currentTab,
                    onTabSelected = { currentTab = it },
                    // 底部避让手势条 / 导航栏
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
        }   // ← 前景层结束（内容 + 底部导航）

        // ★ 第 6 批：全局轻提示「气泡条」——底部导航上方滑入、2 秒自动消退（Toast 已退役）
        com.wqmtmbdi.mbcc.ui.components.TipBarHost(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 88.dp)
        )

        // ★ v1.5.1：公告改为独立整页（未读自动打开 + 铃铛点击都走这里）。
        //   ★ 页面外层自己 statusBarsPadding()——它不在内容层的避让范围里，顶部不会压到状态栏。
        if (showAnn) {
            DisposableEffect(Unit) {
                AppChrome.fullscreen = true
                AppChrome.onBack = { showAnn = false; true }
                onDispose {
                    AppChrome.fullscreen = false
                    AppChrome.onBack = null
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(colorFromHex(t.bg))
                    .statusBarsPadding()
            ) {
                com.wqmtmbdi.mbcc.ui.common.AnnouncementPage(
                    onBack = { showAnn = false },
                    // 启动自动打开：直接进最新公告的详情；返回一次到公告列表，再返回回主页面
                    initialDetailId = if (annAtLatest) {
                        com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS.maxOfOrNull { it.id }
                    } else null
                )
            }
        }

        if (showPrivacy) {
            PrivacyDialog(
                visible = true,
                onAgree = {
                    AppStorage.setPrivacyAgreed()
                    showPrivacy = false
                }
            )
        }
    }
}
