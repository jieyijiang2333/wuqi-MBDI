package com.wqmtmbdi.mbcc.data

/** 代码字母释义（H=人性 Humanity 等） */
data class Meaning(val l: String, val m: String)

/** 角色（对齐 HTML CHARACTERS；stats 官方整数直连，引擎只用 stats） */
data class GameChar(
    val id: String,
    val name: String,
    val code: String,
    val avatarColor: String,
    val stats: List<Int>,
    val statsDisplay: List<String>? = null,  // 000 的 '?' 特例，仅显示层用
    val meanings: List<Meaning>,
    val desc: String
)

/** 人格鉴定选项（s 为七维权重；数值为 Double，仅个别写成 Int 字面量，用 Number 统一承载） */
data class QOption(val l: String, val t: String, val s: List<Number>)

/** 人格鉴定题目 */
data class Question(val id: Int, val label: String, val text: String, val options: List<QOption>)

/** 狂厄共鸣选项（s 为维度→加分映射） */
data class ResOption(val l: String, val t: String, val s: Map<String, Int>)

/** 狂厄共鸣题目 */
data class ResQuestion(val id: Int, val label: String, val text: String, val options: List<ResOption>)

/** 狂厄特化类型（对齐 HTML RESONANCE_TYPES） */
data class ResonanceType(
    val id: String,
    val name: String,
    val code: String,
    val color: String,
    val trait: String,
    val desc: String
)

/** 阵营信息（对齐 HTML FACTIONS_INFO） */
data class FactionInfo(val id: String, val name: String, val desc: String)

/** 成就（对齐 HTML ACHIEVEMENTS） */
data class Achievement(val id: String, val icon: String, val name: String, val desc: String)

/** 公告（对齐 HTML ANNOUNCEMENTS） */
data class Announcement(val id: Int, val date: String, val content: String, val sign: String)
