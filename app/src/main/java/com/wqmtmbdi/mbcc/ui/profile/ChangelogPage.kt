package com.wqmtmbdi.mbcc.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wqmtmbdi.mbcc.AppStorage
import com.wqmtmbdi.mbcc.colorFromHex
import com.wqmtmbdi.mbcc.ui.components.MbdiChip
import com.wqmtmbdi.mbcc.ui.components.MbdiPageTopBar
import com.wqmtmbdi.mbcc.ui.components.SectionCard
import com.wqmtmbdi.mbcc.ui.theme.MbdiSpace
import com.wqmtmbdi.mbcc.ui.theme.MbdiType

/**
 * 更新日志（独立整页，v1.5.1 由「关于」页内嵌折叠列表改过来）。
 *
 * ★ 用户要求：**全部展开**，不再有折叠/收起交互；每条一卡，从最新往旧排。
 * ★ 数据在文件末尾的 [CHANGELOG]，改文案只改那里（最新在前）。
 * ★ 入口：全部设置 → 关于 → 更新日志。
 */
@Composable
fun ChangelogPage(onBack: () -> Unit) {
    val t = AppStorage.currentMBDITheme()

    Column(Modifier.fillMaxSize()) {
        MbdiPageTopBar("更新日志", onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(MbdiSpace.s))
            CHANGELOG.forEachIndexed { i, e ->
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            e.ver,
                            fontSize = MbdiType.Heading, fontWeight = FontWeight.Bold,
                            color = colorFromHex(t.gold)
                        )
                        if (i == 0) {
                            Spacer(Modifier.width(6.dp))
                            MbdiChip("最新", colorFromHex(t.gold), filled = true)
                        }
                        Spacer(Modifier.weight(1f))
                        Text(e.date, fontSize = MbdiType.Caption, color = colorFromHex(t.text3))
                    }
                    Spacer(Modifier.height(MbdiSpace.s))
                    Text(
                        e.body,
                        fontSize = MbdiType.Label, lineHeight = 20.sp,
                        color = colorFromHex(t.text2)
                    )
                }
                Spacer(Modifier.height(MbdiSpace.s))
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}

/** 一条更新日志 */
private data class Changelog(val ver: String, val date: String, val body: String)

/**
 * 更新日志（最新在前）。
 * 写法约定：每条用「某模块 + 做了什么 + 一句话收益」的简洁句式，不写长篇功能介绍与逐条流水账。
 */
private val CHANGELOG: List<Changelog> = listOf(
    Changelog(
        "v1.5.0", "2026.11.03",
        "• 禁闭者图鉴大幅扩充：新增 78 名禁闭者，全库共 98 名，新增狂厄级 / 高危级 / 普管级 / 特殊四种等级，图鉴可按等级筛选。\n" +
            "• 新增「局长档案」：完成一次鉴定后自动生成你的专属七维画像。\n" +
            "• 鉴定模式改为两栏五档：严格档 30 / 40 / 50 题只按人格匹配，娱乐档 12 / 20 题会优先补全未收集的角色。\n" +
            "• 我的页改版：常用入口改为 4 列 × 2 行图标网格，低频功能收进新增的「全部设置」，并移除反馈中心入口。\n" +
            "• 禁闭者形象改为独立页面：一行三个、按等级分组，上传后可直接更换或删除。\n" +
            "• 新增 6 套主题配色与深色模式，深色模式支持跟随系统，可在「全部设置 → 主题」切换。\n" +
            "• 星辰计划页面功能优化修改。\n" +
            "• 公告与更新日志改为独立整页，更新日志全部展开，公告支持查看单条详情。\n" +
            "• 关于页新增应用图标与最新版本；新增开源组件说明页。\n" +
            "• 匹配引擎适配扩充后的角色池，各档位概率分布分别重新校准。\n" +
            "• 移除预测性返回的过渡动画，返回操作更直接。"
    ),
    Changelog(
        "v1.4.0", "2026.10.02",
        "• 底层重构：底层架构全面升级为安卓原生，带来更流畅的运行速度与更稳定的系统级体验。\n" +
            "• 性能与低端机适配：答题与结果页粒子精简至 24 个，低端机运行更稳。\n" +
            "• 新增「崩溃日志」：闪退自动留档，方便开发者快速定位问题。\n" +
            "• 新增「反馈中心」：提供反馈规范与模板，让建议与问题反馈更高效。\n" +
            "• 图鉴系统扩充：完善收集进度与解锁状态展示。\n" +
            "• 结果页新功能：新增揭晓动画与环形匹配度，带来更具仪式感的测试体验。\n" +
            "• 我的页布局优化：入口重新分类整理，操作路径更清晰。\n" +
            "• 首页布局优化：改为两个玩法并列入口，提升核心功能的曝光度。\n" +
            "• 全局界面深度重做：涉及结果页揭晓四幕、雷达光桥、首页三环仪表、档位频段卡、答题能量条与选项锁定、图鉴三环与解锁角标、共鸣页「频率接收」仪式与六维频率环、我的页证件铭牌与迷你仪表等全面翻新。\n" +
            "• 配色方案重构：界面整体重做，曾采用深墨金「零镜仪器」，全界面视觉更加清晰、色彩搭配更协调。\n" +
            "• 图标体系统一：全 App 统一为 Lucide 线形图标，视觉更统一。\n" +
            "• 雷达图重画：修正对比曲线「两把尺子」的问题，新增刻度与生长动画，可点某一维度看具体差距。\n" +
            "• 页面布局与弹窗视觉统一：底部导航改为居中悬浮胶囊；全部弹窗统一重做。\n" +
            "• 其他视觉调整：全 App 字号统一；冷启动改为「零镜启动」；全局去掉扫光特效；返回时底层画面带景深对焦；「星辰计划」改为独立整页；「关于」只保留更新日志，历史版本改成折叠式；档案详情从弹窗改成整页。\n" +
            "• 返回键逻辑优化：优化全局返回逻辑，弹窗优先关闭，避免误退，操作更符合直觉。\n" +
            "• 侧滑返回动画重做：重写侧滑手势交互，让页面切换更自然流畅。\n" +
            "• 答题页体验优化：针对答题过程中的操作细节进行体验升级。\n" +
            "• 弹窗交互优化：优化弹窗的进出动画与交互响应，提升操作效率。\n" +
            "• 首页引导优化：调整首页引导文案及展示位置，帮助新玩家快速上手。"
    ),
    Changelog(
        "v1.2.0", "2026.08.28",
        "• 废除α抽象空间，匹配全面回归角色七维属性\n" +
            "• 引入官方十大狂厄性质阵营：怠惰、破坏、混沌、背离、执迷、暗潮、谵妄、异质、不朽、愤怒\n" +
            "• 两级匹配：先判定狂厄性质，再于阵营内匹配禁闭者\n" +
            "• 题库全面重写为七维权重，概率分布重新校准\n" +
            "• 图鉴新增阵营筛选与阵营标签展示\n" +
            "• 结果页新增阵营徽章与七维画像对比雷达"
    ),
    Changelog(
        "v1.1.0", "2026.08.20",
        "• 新增6名禁闭者：珀尔夫人、安吉尔、罗睺、可可莉克、摩尔、卡门奈特，总数扩展至20名\n" +
            "• 「我的」页面按钮整合，玩法介绍与关于合并为四标签，形象与配色独立为双按钮"
    ),
    Changelog(
        "v1.0.0", "2026.08.15",
        "• 重新设计α空间变换矩阵，分离度提升40%\n" +
            "• 题目权重全面重构，引入正负双向权衡\n" +
            "• α因子覆盖率从±100%优化至±15%以内\n" +
            "• 移除16题档位，双档位概率互补，全14名禁闭者可达\n" +
            "• 新增12名禁闭者：000、哈梅尔、黛伦、爱缇、观星者、曼提斯、珀尔夫人、安吉尔、罗睺、可可莉克、摩尔、卡门奈特\n" +
            "• 禁闭者总数扩展至20名\n" +
            "• 优化操作体验"
    ),
    Changelog("v0.9.0", "2026.08.10", "• 图鉴系统页面全面重构"),
    Changelog(
        "v0.8.0", "2026.08.05",
        "• 全新α空间抽象引擎，引入7维观测属性→6维抽象因子变换矩阵\n" +
            "• 所有禁闭者新增七维属性面板\n" +
            "• 新增三档鉴定选择：基础 / 深度 / 狂厄共振\n" +
            "• 专属共振引擎：阈值门控+边际饱和+协同触发+混沌因子"
    ),
    Changelog("v0.7.1", "2026.07.29", "优化部分界面显示内容"),
    Changelog(
        "v0.7.0", "2026.07.20",
        "• 结果页新增个人信息展示\n• 新增粉红主题「蔷薇之心」\n• 修复雷达图在红/白主题下的显示问题"
    ),
    Changelog(
        "v0.6.0", "2026.07.10",
        "• 新增「狂厄共鸣测试·零镜回响」玩法\n• 新增六维频率图谱与MBDI联动分析\n• 新增鉴定记录系统\n• 图鉴新增6种狂厄特化档案"
    ),
    Changelog(
        "v0.5.0", "2026.07.05",
        "• 界面布局整体下移优化\n• 公告改为手动查看+未读提醒\n• 新增禁闭者形象自定义"
    ),
    Changelog(
        "v0.4.0", "2026.07.04",
        "• 弹窗统一为居中模态\n• 答题导航新增上一题/下一题\n• 新增主菜单按钮与玩法介绍弹窗"
    ),
    Changelog(
        "v0.3.0", "2026.07.04",
        "• 测试题目全面重制\n• 弹窗尺寸增大30%\n• 修复公告重复弹窗问题"
    ),
    Changelog(
        "v0.2.0", "2026.07.03",
        "• 导航栏间距优化\n• 新增「纯白指令」主题\n• 主题配色改为居中弹窗"
    ),
    Changelog(
        "v0.1.0", "2026.07.02",
        "• 初始版本发布\n• 8名禁闭者MBCC代码收录\n• 12道情景鉴定题目\n• 图鉴、成就、主题配色系统"
    )
)
