package com.wqmtmbdi.mbcc.data

/** 七维标签顺序（对齐 HTML STATS_LABELS7）：[同理心,意志力,体质,教育程度,智商,敏捷,力量] */
val STATS_LABELS7 = listOf("同理心", "意志力", "体质", "教育程度", "智商", "敏捷", "力量")

// ===== 角色数据：七维 [同理心,意志力,体质,教育程度,智商,敏捷,力量] =====
// v1.3.0：CHR_STATS 全部为官方整数直连（DCPM 匹配用），不再做任何暗改；
// 只有 000 官方未公布参数（'?'）走显示层特例，见其 statsDisplay
// ★ v1.5.0：本表改名为 BASE_CHR_STATS（内置 20 名），全量库 = 本表 + CHR_STATS_V15（78 名）
internal val BASE_CHR_STATS: Map<String, List<Int>> = mapOf(
    "juzhang" to listOf(9, 10, 5, 8, 9, 5, 4),
    "serin" to listOf(10, 9, 5, 9, 10, 4, 4),
    "zoya" to listOf(6, 9, 8, 6, 8, 7, 10),
    "hella" to listOf(7, 9, 8, 3, 7, 8, 6),
    "hecate" to listOf(0, 8, 4, 5, 8, 5, 4),
    "baiyi" to listOf(9, 9, 6, 7, 7, 10, 7),
    "langley" to listOf(5, 10, 6, 9, 9, 7, 7),
    "irina" to listOf(8, 9, 6, 9, 9, 5, 5),
    "zero" to listOf(4, 9, 4, 10, 10, 5, 3),
    "hamel" to listOf(9, 9, 7, 5, 5, 8, 7),
    "deren" to listOf(8, 7, 7, 8, 7, 6, 7),
    "etti" to listOf(6, 5, 5, 5, 8, 6, 7),
    "astro" to listOf(4, 8, 6, 3, 9, 7, 6),
    "mantis" to listOf(1, 9, 1, 8, 1, 9, 9),
    "pearl" to listOf(7, 7, 5, 8, 7, 6, 7),
    "angel" to listOf(2, 8, 8, 6, 8, 9, 8),
    "raven" to listOf(5, 9, 10, 7, 8, 8, 8),
    "coco" to listOf(8, 8, 4, 7, 8, 7, 6),
    "more" to listOf(6, 9, 3, 9, 10, 2, 2),
    "carmen" to listOf(8, 8, 4, 8, 7, 7, 6)
)

// ★ v1.5.0：本表改名为 BASE_CHARACTERS（内置 20 名），全量库 = 本表 + CHARACTERS_V15（78 名）
internal val BASE_CHARACTERS: List<GameChar> = listOf(
    GameChar(
        id = "juzhang", name = "局长", code = "HOPE", avatarColor = "#1a2a6c",
        stats = BASE_CHR_STATS["juzhang"]!!,
        meanings = listOf(Meaning("H", "人性（Humanity）"), Meaning("O", "洞察（Observation）"), Meaning("P", "忍耐（Patience）"), Meaning("E", "共情（Empathy）")),
        desc = "你不以力量或权谋见长，却以对人的理解与包容穿透黑暗。即便在狂厄最深处，你依然选择看见\"人\"而非\"怪物\"。希望是你唯一的武器，也是最坚固的铠甲。你是所有禁闭者的连接点——你就是局长。"
    ),
    GameChar(
        id = "serin", name = "瑟琳", code = "CTRL", avatarColor = "#6b2fa0",
        stats = BASE_CHR_STATS["serin"]!!,
        meanings = listOf(Meaning("C", "掌控（Control）"), Meaning("T", "谋略（Tactics）"), Meaning("R", "理性（Rationality）"), Meaning("L", "统率（Leadership）")),
        desc = "你是棋盘的执子者，永远比对手多看三步。情感被严格封装在逻辑之外，所有行动都服务于最终目标。\"CTRL\"既是控制键，也是你人生的常态——你控制局面，也控制自己。你就是瑟琳。"
    ),
    GameChar(
        id = "zoya", name = "卓娅", code = "BRUT", avatarColor = "#8b2500",
        stats = BASE_CHR_STATS["zoya"]!!,
        meanings = listOf(Meaning("B", "蛮力（Brawn）"), Meaning("R", "无畏（Ruthlessness）"), Meaning("U", "不屈（Unyielding）"), Meaning("T", "暴烈（Tempest）")),
        desc = "军团长出身的你，战斗是你与世界的对话方式。不迂回、不犹豫，用拳头和钢铁证明生存的法则。\"BRUT\"剔除了残忍，只留下纯粹的力量与意志。你就是卓娅。"
    ),
    GameChar(
        id = "hella", name = "海拉", code = "WILD", avatarColor = "#2d7a3a",
        stats = BASE_CHR_STATS["hella"]!!,
        meanings = listOf(Meaning("W", "狡黠（Wily）"), Meaning("I", "冲动（Impulsive）"), Meaning("L", "活泼（Lively）"), Meaning("D", "大胆（Daring）")),
        desc = "像野猫一样任性又机灵，你从不按规矩出牌。嘴上刻薄，行动却常常带着出乎意料的善意。\"WILD\"捕捉了你未被驯化的生命力，即使在牢笼里也依然张牙舞爪。你就是海拉。"
    ),
    GameChar(
        id = "hecate", name = "赫卡蒂", code = "VOID", avatarColor = "#3a3a6a",
        stats = BASE_CHR_STATS["hecate"]!!,
        meanings = listOf(Meaning("V", "誓言（Vow）"), Meaning("O", "服从（Obedience）"), Meaning("I", "孤僻（Isolated）"), Meaning("D", "决绝（Determined）")),
        desc = "你像一把沉默的利刃，誓言即生命。对外界保持疏离，内心却燃烧着不可动摇的使命。\"VOID\"既指你情感的空洞表象，也指你将自己献祭于无尽黑暗的决绝姿态。你就是赫卡蒂。"
    ),
    GameChar(
        id = "baiyi", name = "白逸", code = "FLEX", avatarColor = "#2a5a7a",
        stats = BASE_CHR_STATS["baiyi"]!!,
        meanings = listOf(Meaning("F", "灵动（Fluid）"), Meaning("L", "活跃（Lively）"), Meaning("E", "专精（Expertise）"), Meaning("X", "变数（X-factor）")),
        desc = "街头智慧与即兴应变的天才。你永远有Plan B，甚至没有Plan只靠直觉也能赢。\"FLEX\"强调你的柔韧与不可预测，像水一样流窜于任何缝隙。你就是白逸。"
    ),
    GameChar(
        id = "irina", name = "伊琳娜", code = "REGN", avatarColor = "#6a1a4a",
        stats = BASE_CHR_STATS["irina"]!!,
        meanings = listOf(Meaning("R", "统治（Rule）"), Meaning("E", "远见（Eye for future）"), Meaning("G", "严苛（Grim）"), Meaning("N", "王者（Noble）")),
        desc = "你天生属于王座。每一步都经精密计算，对人对己同样冷酷。\"REGN\"昭示你不容置疑的权威，以及眼中从未熄灭的、俯瞰众神的火光。你就是伊琳娜。"
    ),
    GameChar(
        id = "langley", name = "兰利", code = "SPID", avatarColor = "#4a3a2a",
        stats = BASE_CHR_STATS["langley"]!!,
        meanings = listOf(Meaning("S", "策略（Strategy）"), Meaning("P", "精准（Precision）"), Meaning("I", "无形（Invisible）"), Meaning("D", "狡诈（Devious）")),
        desc = "你像蛛网中心的猎手，不动声色，却早已将丝线布及每个角落。每一步都指向猎物，从不浪费一次行动。\"SPID\"既是你的战术形态，也是你性情中最真实的注脚。你就是兰利。"
    ),
    GameChar(
        id = "zero", name = "000", code = "NUL0", avatarColor = "#1a1a2e",
        stats = BASE_CHR_STATS["zero"]!!,
        statsDisplay = listOf("4", "9", "?", "10", "10", "?", "?"),
        meanings = listOf(Meaning("N", "空值（Null）"), Meaning("U", "未定义（Undefined）"), Meaning("L", "逻辑（Logic）"), Meaning("0", "原点（Origin）")),
        desc = "你不属于任何体系，却能够渗透所有体系。代码与逻辑是你的语言，而数字世界的尽头，是你重新定义现实的起点。你不是在寻找答案——你是在重写问题本身。你就是000。"
    ),
    GameChar(
        id = "hamel", name = "哈梅尔", code = "LYRE", avatarColor = "#2a6a5a",
        stats = BASE_CHR_STATS["hamel"]!!,
        meanings = listOf(Meaning("L", "抒情（Lyric）"), Meaning("Y", "向往（Yearning）"), Meaning("R", "韵律（Rhythm）"), Meaning("E", "回响（Echo）")),
        desc = "你的语言不在唇齿之间，而在肢体与韵律之中。你不争辩、不征服，却能让最狂暴的灵魂在你的旋律中安静下来。温柔不是软弱——是比任何刀锋都更难抵抗的力量。你就是哈梅尔。"
    ),
    GameChar(
        id = "deren", name = "黛伦", code = "REEL", avatarColor = "#4a2a5a",
        stats = BASE_CHR_STATS["deren"]!!,
        meanings = listOf(Meaning("R", "镜头（Reel）"), Meaning("E", "表达（Express）"), Meaning("E", "编辑（Edit）"), Meaning("L", "光影（Light）")),
        desc = "你看世界的方式和别人不同——每一帧都是构图，每一次沉默都是留白。你不是在记录现实，而是在用影像重新书写它。镜头是你的武器，而真相，只存在于你的剪辑之中。你就是黛伦。"
    ),
    GameChar(
        id = "etti", name = "爱缇", code = "ARCH", avatarColor = "#8a7a4a",
        stats = BASE_CHR_STATS["etti"]!!,
        meanings = listOf(Meaning("A", "审美（Aesthetic）"), Meaning("R", "构筑（Render）"), Meaning("C", "蓝图（Blueprint）"), Meaning("H", "匠心（Handcraft）")),
        desc = "在你的世界里，没有什么是\"就这样吧\"。每一根线条都必须精确，每一处空间都必须有意义。你不是在建造建筑——你是在为混沌的世界划定秩序。图纸就是你的法律。你就是爱缇。"
    ),
    GameChar(
        id = "astro", name = "观星者", code = "ASTR", avatarColor = "#2a3a6a",
        stats = BASE_CHR_STATS["astro"]!!,
        meanings = listOf(Meaning("A", "星轨（Astral）"), Meaning("S", "凝视（Stare）"), Meaning("T", "时轮（Time）"), Meaning("R", "启示（Revelation）")),
        desc = "你从不低头看脚下的路，因为你的眼睛永远望向苍穹。群星在你眼中不是光点，是一张写满命运的网。你不预言未来——你只是在所有人还没看到的时候，已经读懂了结局。你就是观星者。"
    ),
    GameChar(
        id = "mantis", name = "曼提斯", code = "MANT", avatarColor = "#3a5a2a",
        stats = BASE_CHR_STATS["mantis"]!!,
        meanings = listOf(Meaning("M", "螳刃（Mantis）"), Meaning("A", "暗影（Aglow）"), Meaning("N", "夜狩（Night）"), Meaning("T", "棘刺（Thorn）")),
        desc = "你是黑暗中的猎手。你的体温与周围的空气融为一体，你的心跳与背景噪音同步共振。你不思考——你只反应。不犹豫——只执行。当猎物意识到危险的时候，你的镰刃已经划过了它的咽喉。你就是曼提斯。"
    ),
    GameChar(
        id = "pearl", name = "珀尔夫人", code = "PEAR", avatarColor = "#8a6a7a",
        stats = BASE_CHR_STATS["pearl"]!!,
        meanings = listOf(Meaning("P", "仪态（Poise）"), Meaning("E", "优雅（Elegance）"), Meaning("A", "气场（Aura）"), Meaning("R", "涵养（Refinement）")),
        desc = "你是沙龙里的女王，每一句话都经过精心雕琢。你不需要提高音量——一个眼神就足够了。世人以为你在谈笑风生，却不知道每一场对话都是一步棋。优雅是你最锋利的刀刃。你就是珀尔夫人。"
    ),
    GameChar(
        id = "angel", name = "安吉尔", code = "BLAD", avatarColor = "#4a4a5a",
        stats = BASE_CHR_STATS["angel"]!!,
        meanings = listOf(Meaning("B", "刃锋（Blade）"), Meaning("L", "致命（Lethal）"), Meaning("A", "冷酷（Absolute）"), Meaning("D", "暗影（Dark）")),
        desc = "你的行动比言语更快，你的刀刃比承诺更可靠。情感是累赘，犹豫是死因。你在黑暗中移动，像一阵没有温度的风——目标倒下之前，甚至不知道你的存在。你就是安吉尔。"
    ),
    GameChar(
        id = "raven", name = "罗睺", code = "IRON", avatarColor = "#6a5a4a",
        stats = BASE_CHR_STATS["raven"]!!,
        meanings = listOf(Meaning("I", "钢铁（Iron）"), Meaning("R", "坚韧（Resilient）"), Meaning("O", "压制（Overpower）"), Meaning("N", "不屈（Never-yield）")),
        desc = "你是移动的堡垒。在最惨烈的战场上，当所有人都在后退的时候，你还在前进。伤痛对你来说不是停止的信号，而是继续的理由。你不相信奇迹——你只相信拳头落在肉上的声音。你就是罗睺。"
    ),
    GameChar(
        id = "coco", name = "可可莉克", code = "POIS", avatarColor = "#7a3a6a",
        stats = BASE_CHR_STATS["coco"]!!,
        meanings = listOf(Meaning("P", "毒蕊（Poison）"), Meaning("O", "幽兰（Orchid）"), Meaning("I", "迷醉（Intoxicate）"), Meaning("S", "魅惑（Seduce）")),
        desc = "最美丽的花朵往往藏着最烈的毒。你从不在正面交锋中浪费力气——一个微笑、一句话、一杯酒，敌人就会自己走向毁灭。你享受猎物落入陷阱前那一刻的陶醉眼神。你就是可可莉克。"
    ),
    GameChar(
        id = "more", name = "摩尔", code = "MIND", avatarColor = "#2a3a4a",
        stats = BASE_CHR_STATS["more"]!!,
        meanings = listOf(Meaning("M", "智谋（Mind）"), Meaning("I", "洞察（Intellect）"), Meaning("N", "棋局（Network）"), Meaning("D", "布局（Design）")),
        desc = "你的身体脆弱不堪，但没有人敢轻视你——因为你的头脑比任何武器都危险。你在棋盘上移动的不是棋子，是人。你不知道\"正面冲突\"是什么意思，因为在你看来，战斗在开始之前就已经结束了。你就是摩尔。"
    ),
    GameChar(
        id = "carmen", name = "卡门奈特", code = "DRAM", avatarColor = "#9a3a4a",
        stats = BASE_CHR_STATS["carmen"]!!,
        meanings = listOf(Meaning("D", "戏剧（Drama）"), Meaning("R", "浪漫（Romance）"), Meaning("A", "魅力（Allure）"), Meaning("M", "假面（Masquerade）")),
        desc = "生活是一场永不落幕的演出，而你生来就是主角。你用最华丽的辞藻包裹最锋利的话语，用最动人的微笑掩饰最冰冷的意图。所有人都是你的观众，没有人知道面具之下是什么。你就是卡门奈特。"
    )
)

// ===== ★ v1.5.0 全角色库（98 名 = 内置 20 + 新增 78） =====
// 新增数据在 GameCharsV15.kt；重名角色以老数据为准，不做覆盖。
/** 全角色七维（98 名） */
val CHR_STATS: Map<String, List<Int>> = BASE_CHR_STATS + CHR_STATS_V15

/** 全角色档案（98 名） */
val CHARACTERS: List<GameChar> = BASE_CHARACTERS + CHARACTERS_V15

/**
 * ★ v1.5.0 角色等级：狂厄级 / 高危级 / 普管级 / 特殊
 * 口径对齐记账本角色池（CharData.CHAR_RANK_GROUPS）；局长 = 特殊。
 */
val CHAR_RANK: Map<String, String> = mapOf(
    "juzhang" to "特殊",
    "serin" to "狂厄级", "zoya" to "狂厄级", "hella" to "普管级", "hecate" to "高危级",
    "baiyi" to "狂厄级", "langley" to "狂厄级", "irina" to "狂厄级", "zero" to "狂厄级",
    "hamel" to "狂厄级", "deren" to "狂厄级", "etti" to "狂厄级", "astro" to "狂厄级",
    "mantis" to "狂厄级", "pearl" to "狂厄级", "angel" to "狂厄级", "raven" to "狂厄级",
    "coco" to "狂厄级", "more" to "狂厄级", "carmen" to "狂厄级"
) + CHAR_RANK_V15

/** 取角色等级（全部角色均已登记；未登记返回空串，UI 自动不显示） */
fun rankOf(id: String): String = CHAR_RANK[id] ?: ""

/** 角色→阵营映射（局长无官方阵营 → "特殊"，对齐 HTML CHAR_FACTION；lazy 延迟到 FACTIONS 初始化后） */
val CHAR_FACTION: Map<String, String?> by lazy {
    buildMap {
        FACTIONS.forEach { (fid, cids) -> cids.forEach { put(it, fid) } }
        put("juzhang", null)
    }
}

// ===== 官方狂厄性质阵营：角色ID列表（对齐 HTML FACTIONS） =====
val FACTIONS: Map<String, List<String>> = mapOf(
    "laodai" to listOf("serin", "pearl", "carmen"),       // 怠惰
    "pohuai" to listOf("zoya", "hella"),                  // 破坏
    "hundun" to listOf("hecate", "coco"),                 // 混沌
    "beili" to listOf("baiyi", "langley"),                // 背离
    "zhimii" to listOf("irina", "hamel"),                 // 执迷
    "anchao" to listOf("zero", "angel", "more"),          // 暗潮
    "zhanwang" to listOf("deren", "etti"),                // 谵妄
    "yizhi" to listOf("astro"),                           // 异质
    "buxiu" to listOf("mantis"),                          // 不朽
    "femu" to listOf("raven")                             // 愤怒
)

val FACTIONS_INFO: Map<String, FactionInfo> = mapOf(
    "laodai" to FactionInfo("laodai", "怠惰", "以精神倦怠、虚无逃避为核心诱因，狂厄多表现为惰性侵蚀、规则停滞类异能，偏向以消极方式扭曲现实。"),
    "pohuai" to FactionInfo("pohuai", "破坏", "以暴力冲动、毁灭欲望为核心诱因，狂厄直接转化为强悍的物理破坏力，倾向以宣泄式破坏释放狂厄。"),
    "hundun" to FactionInfo("hundun", "混沌", "以意识紊乱、精神崩坏为核心诱因，狂厄效果无序不可控，异能常伴随随机、连锁式的异变反应。"),
    "beili" to FactionInfo("beili", "背离", "以挣脱秩序、背叛规则为核心诱因，狂厄赋予持有者突破常理的机动、隐匿或反制能力，擅长打破既定局面。"),
    "zhimii" to FactionInfo("zhimii", "执迷", "以极致偏执的执念（情感、理想、目标）为核心诱因，异能强度与精神深度绑定，随执念强化而升级。"),
    "anchao" to FactionInfo("anchao", "暗潮", "以潜藏恶意、隐秘算计为核心诱因，狂厄表现为隐蔽、长效的侵蚀与操控，擅长在暗处布局、悄然影响局势。"),
    "zhanwang" to FactionInfo("zhanwang", "谵妄", "以认知错乱、虚妄幻象为核心诱因，狂厄可扭曲他人感知、制造虚假认知，以精神欺骗瓦解目标。"),
    "yizhi" to FactionInfo("yizhi", "异质", "本身存在形式异于常规人类，狂厄表现为违背物理常识的异常异能，脱离普通禁闭者的能力逻辑。"),
    "buxiu" to FactionInfo("buxiu", "不朽", "以对生命存续的扭曲执念为核心，狂厄赋予极强的生命韧性与再生能力，难以被彻底击溃消灭。"),
    "femu" to FactionInfo("femu", "愤怒", "以暴怒、憎恨、复仇欲为核心诱因，战斗力随情绪激烈程度同步攀升，越愤怒破坏力越强。")
)

// ===== 狂厄共鸣 · 六特化维度（对齐 HTML RES_DIMS） =====
val RES_DIMS = listOf("enlighten", "anomaly", "ferocious", "tenacious", "precise", "mystic")

val RESONANCE_TYPES: Map<String, ResonanceType> = mapOf(
    "enlighten" to ResonanceType(
        id = "enlighten", name = "启迪特化", code = "ENLIGHTEN", color = "#4a8fe0",
        trait = "你的灵魂深处蕴藏着强大的共鸣力，能够感知并影响周遭一切生命的情绪与意志。你不擅长冷漠，也无法对痛苦视而不见——这正是你最珍贵的狂厄天赋。",
        desc = "你拥有穿透黑暗的心灵感知力。在零镜回响中，你的共鸣频率呈现出罕见的广谱覆盖特征。你不仅能够理解他人的痛苦，更能将这份理解转化为影响现实的精神力量。MBCC建议将你归类为【启迪特化】，适合执行需要深度心理渗透的任务。"
    ),
    "anomaly" to ResonanceType(
        id = "anomaly", name = "异能特化", code = "ANOMALY", color = "#c04ad0",
        trait = "你的想象力已经突破了现实的边界，在你内心深处的某个角落，理性与疯狂之间的壁垒正在消融。你相信世界可以被重新定义——而你的意志，就是那把改写现实的钥匙。",
        desc = "零镜回响数据显示，你的精神波动存在异常的不规则峰值。你的想象力不是逃避现实的工具，而是重塑现实的武器。当常人的认知止步于\"不可能\"时，你在那里找到了新的法则。MBCC警告：【异能特化】个体需要定期进行精神稳定性评估。"
    ),
    "ferocious" to ResonanceType(
        id = "ferocious", name = "狂暴特化", code = "FEROCIOUS", color = "#e05030",
        trait = "你体内沉睡着一头猛兽。所有规则对你而言都只是等待被打破的枷锁，你的本能比你的头脑更快，你的愤怒比你的恐惧更诚实。毁灭不是你的目的——自由才是。",
        desc = "你的狂厄共鸣呈现出典型的进攻性波形。你厌恶束缚，将一切形式的人身或精神限制视为对存在的否定。这种近乎本能的叛逆并非失控，而是你对\"自由\"这一核心信条的终极捍卫。MBCC建议：【狂暴特化】个体适合部署在需要高强度突破能力的作战单元。"
    ),
    "tenacious" to ResonanceType(
        id = "tenacious", name = "坚韧特化", code = "TENACIOUS", color = "#4a9a5a",
        trait = "你像一座沉默的山脉，在最沉重的压力下反而展现出惊人的稳定性。承诺对你而言不是言语的装饰，而是需要用生命去兑现的契约。你可以被击倒，但永远不会被摧毁。",
        desc = "精神压力测试显示，你的心理韧性处于极高水平。你在极端环境下的情绪稳定性、专注力和持续行动能力远超常规标准。你的耐力不是来自麻木，而是来自内心深处不可动摇的信念锚点。MBCC认定：【坚韧特化】个体是持久作战和危机管理的第一选择。"
    ),
    "precise" to ResonanceType(
        id = "precise", name = "精准特化", code = "PRECISE", color = "#d0b020",
        trait = "你的专注力像一束激光，能够穿透表象直达本质。在纷繁复杂的信息洪流中，你总能捕捉到最关键的那一个细节。你的判断不是直觉——是无数次精密计算后的必然结论。",
        desc = "认知测试表明，你的信息处理模式具有高度专精化特征。你能够在极短时间内完成对复杂系统的分析、建模和关键节点定位。这种能力使你在混乱的战场环境中依然保持清醒的战略头脑。MBCC归档：【精准特化】个体适合执行需要高度专注力和判断力的精密任务。"
    ),
    "mystic" to ResonanceType(
        id = "mystic", name = "诡秘特化", code = "MYSTIC", color = "#8a6aaa",
        trait = "你的直觉像一条看不见的河流，在理性无法触及的黑暗中悄然流淌。你常常\"知道\"一些你不应该知道的事情，你的选择看似随机却总能在事后被证明是唯一正确的道路。理性主义者惧怕你——因为你无法被解释。",
        desc = "零镜系统在分析你的共振频谱时发现了无法归类的异常波动。你的决策路径跳过了常规的逻辑推理阶段，呈现出跳跃式的直觉特征。你与混沌之间似乎存在着某种无需翻译的默契。MBCC备注：【诡秘特化】个体的行为预测模型置信度低于常规标准。"
    )
)

/** 维度中文名与颜色（对齐 HTML renderDimBars） */
val RES_DIM_NAMES = mapOf(
    "enlighten" to "启迪", "anomaly" to "异能", "ferocious" to "狂暴",
    "tenacious" to "坚韧", "precise" to "精准", "mystic" to "诡秘"
)

/** 联动分析文案（对齐 HTML getCrossoverMsg） */
fun crossoverMsg(charCode: String, typeId: String): String = when (typeId) {
    "enlighten" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【启迪特化】。一致性评价：你的共感力与洞察力强化了人格特质，使你在理解他人方面达到了共鸣级深度。"
    "anomaly" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【异能特化】。一致性评价：你的想象力与极端倾向为你的人格注入了扭曲现实的潜能。"
    "ferocious" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【狂暴特化】。一致性评价：你本就存在的强烈自我意识，在狂厄频率中激活了毁灭与自由的双重共鸣。"
    "tenacious" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【坚韧特化】。一致性评价：你的倔强与责任心在极端情境下转化为不可摧毁的精神壁垒。"
    "precise" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【精准特化】。一致性评价：你的专注力与判断力在狂厄频率中精炼为穿透表象的锐利目光。"
    "mystic" -> "你的MBDI人格代码为【$charCode】，狂厄特化为【诡秘特化】。一致性评价：你的直觉与变数倾向在零镜系统中呈现出不可归类的异常共鸣。"
    else -> "未知共鸣频率。"
}

// ===== 50题题库（对齐 HTML QUESTIONS）—— 每题4选项映射七维权重 [同理心,意志力,体质,教育程度,智商,敏捷,力量] =====
// 前32题：深度鉴定档；全部50题：狂厄共振档
val QUESTIONS: List<Question> = listOf(
    Question(1, "情景 · 壹", "面对黑环爆发，你的第一反应是？", listOf(
        QOption("A", "立即组织疏散，优先保护平民", listOf(8, 8, 8, 4, 2, 4, 6)),
        QOption("B", "分析黑环结构特征，寻找封印突破口", listOf(4, 4, 2, 8, 8, 2, 8)),
        QOption("C", "快速评估局势，合理调配资源", listOf(2, 6, 6, 6, 6, 6, 4)),
        QOption("D", "尝试感知黑环内部的狂厄频率", listOf(6, 2, 4, 2, 4, 8, 2))
    )),
    Question(2, "情景 · 贰", "在狄斯城街头遭遇帮派伏击，你如何应对？", listOf(
        QOption("A", "观察周围环境，利用地形设局反制", listOf(5, 6, 6, 8, 6, 6, 6)),
        QOption("B", "正面迎战，用火力直接撕开突破口", listOf(5, 4, 8, 4, 2, 4, 8)),
        QOption("C", "假装示弱，趁对方松懈时反击脱身", listOf(5, 2, 4, 2, 4, 8, 4)),
        QOption("D", "冷静分析对方人数和站位再做行动", listOf(5, 8, 2, 6, 8, 2, 2))
    )),
    Question(3, "情景 · 叁", "发现一名禁闭者拥有危险的潜在能力，你会？", listOf(
        QOption("A", "主动接触建立信任，引导其控制能力", listOf(8, 4, 5, 2, 6, 6, 5)),
        QOption("B", "保持距离严密监控，评估威胁等级", listOf(6, 6, 5, 6, 8, 4, 5)),
        QOption("C", "暗中调查对方的背景和过往记录", listOf(4, 2, 5, 8, 4, 8, 5)),
        QOption("D", "直接上报MBCC，交由专业部门处理", listOf(2, 8, 5, 4, 2, 2, 5))
    )),
    Question(4, "情景 · 肆", "上级交给你一项模糊不清的秘密任务，你优先做什么？", listOf(
        QOption("A", "先行动起来，在过程中随机应变", listOf(6, 8, 5, 2, 6, 8, 5)),
        QOption("B", "制定完整计划，考虑所有风险与变量", listOf(4, 6, 5, 6, 8, 6, 5)),
        QOption("C", "找相关人员谈话，了解任务背景", listOf(8, 4, 5, 4, 2, 4, 5)),
        QOption("D", "查阅历史档案寻找类似案例参考", listOf(2, 2, 5, 8, 4, 2, 5))
    )),
    Question(5, "情景 · 伍", "面对一个对你充满敌意的市民，你如何化解？", listOf(
        QOption("A", "保持冷静克制，用理性和逻辑说服", listOf(4, 8, 5, 8, 8, 6, 6)),
        QOption("B", "用轻松幽默的方式缓和紧张气氛", listOf(6, 2, 5, 6, 6, 8, 4)),
        QOption("C", "直接表明立场和底线，不浪费时间", listOf(2, 6, 5, 4, 4, 4, 8)),
        QOption("D", "耐心倾听对方的诉求，找到症结所在", listOf(8, 4, 5, 2, 2, 2, 2))
    )),
    Question(6, "情景 · 陆", "在废墟中发现一份被加密的狂厄研究档案，你如何处理？", listOf(
        QOption("A", "想办法破解加密，直接获取研究内容", listOf(6, 4, 5, 6, 8, 6, 5)),
        QOption("B", "严格按流程上报，不动这份档案", listOf(4, 8, 5, 8, 2, 4, 5)),
        QOption("C", "先复制一份保存，再将原物依规上报", listOf(2, 6, 5, 4, 4, 2, 5)),
        QOption("D", "尝试用直觉判断档案的真伪和来源", listOf(8, 2, 5, 2, 6, 8, 5))
    )),
    Question(7, "情景 · 柒", "深夜听到关于某个禁闭者的危险传言，你？", listOf(
        QOption("A", "立即前往实地探查虚实", listOf(6, 6, 6, 6, 4, 6, 8)),
        QOption("B", "先记下来，第二天核实后再行动", listOf(4, 8, 4, 4, 8, 4, 2)),
        QOption("C", "当晚就潜入相关地点蹲守", listOf(2, 4, 8, 2, 2, 8, 6)),
        QOption("D", "先传唤知情者详细询问", listOf(8, 2, 2, 8, 6, 2, 4))
    )),
    Question(8, "情景 · 捌", "一场激战后发现队友伤亡惨重，你第一时间会？", listOf(
        QOption("A", "沉默地开始清理战场，用行动替代言语", listOf(6, 6, 6, 6, 6, 6, 8)),
        QOption("B", "安抚存活的队员，重新整理编队", listOf(8, 4, 4, 4, 4, 4, 2)),
        QOption("C", "快速复盘战斗经过，找出失误之处", listOf(4, 2, 2, 8, 8, 8, 4)),
        QOption("D", "咬牙撑住，命令自己继续前进", listOf(2, 8, 8, 2, 2, 2, 6))
    )),
    Question(9, "情景 · 玖", "你被困在一个几乎无法逃离的封闭空间，你？", listOf(
        QOption("A", "锤击墙壁测试每一寸，寻找弱点", listOf(6, 6, 8, 6, 2, 2, 8)),
        QOption("B", "保持体力，冷静观察等待最佳时机", listOf(4, 8, 6, 4, 8, 4, 6)),
        QOption("C", "检查周围每一件物品寻找可利用的工具", listOf(2, 4, 4, 8, 6, 8, 4)),
        QOption("D", "闭目凝神，感知整个空间的细微变化", listOf(8, 2, 2, 2, 4, 6, 2))
    )),
    Question(10, "情景 · 拾", "一个手无寸铁的人徘徊在狂厄感染边缘，你？", listOf(
        QOption("A", "先控制起来隔离观察", listOf(4, 8, 8, 5, 8, 6, 8)),
        QOption("B", "尝试与对方沟通，了解感染来源", listOf(8, 2, 4, 5, 6, 4, 4)),
        QOption("C", "远远跟着观察，确认情况再行动", listOf(2, 6, 2, 5, 4, 8, 2)),
        QOption("D", "设法制止他靠近感染区", listOf(6, 4, 6, 5, 2, 2, 6))
    )),
    Question(11, "情景 · 拾壹", "在档案中发现关于自己过去被篡改的记录，你？", listOf(
        QOption("A", "不动声色地将档案复制保留", listOf(6, 8, 5, 6, 5, 8, 5)),
        QOption("B", "直接去档案室查询更多相关记录", listOf(4, 2, 5, 8, 5, 4, 5)),
        QOption("C", "约谈相关经手人员逐层追查", listOf(8, 6, 5, 4, 5, 2, 5)),
        QOption("D", "装作没看见，暗中调查", listOf(2, 4, 5, 2, 5, 6, 5))
    )),
    Question(12, "情景 · 拾贰", "面对一场注定失败的战斗，你选择？", listOf(
        QOption("A", "即使注定失败也要站着打到最后", listOf(6, 8, 8, 6, 2, 6, 8)),
        QOption("B", "寻找撤退路径保存有生力量", listOf(4, 6, 6, 4, 6, 8, 6)),
        QOption("C", "想办法拖延时间在绝境中寻找转机", listOf(2, 4, 4, 2, 8, 4, 4)),
        QOption("D", "重新定义\"胜利\"——以最小损失为目标", listOf(8, 2, 2, 8, 4, 2, 2))
    )),
    Question(13, "情景 · 拾叁", "路边发现一个敌对组织的伤者，你？", listOf(
        QOption("A", "救他一命，但保持警觉随时准备应对", listOf(8, 6, 5, 4, 4, 8, 8)),
        QOption("B", "按规章上报，由系统来处理", listOf(4, 8, 5, 8, 2, 2, 2)),
        QOption("C", "救他然后借机套取组织情报", listOf(6, 4, 5, 2, 8, 6, 4)),
        QOption("D", "检查他身上是否有重要文件", listOf(2, 2, 5, 6, 6, 4, 6))
    )),
    Question(14, "情景 · 拾肆", "队友犯了致命错误导致全队陷入险境，你？", listOf(
        QOption("A", "先保全任务目标，后续再追究责任", listOf(6, 6, 6, 8, 4, 6, 6)),
        QOption("B", "立即调整策略弥补漏洞", listOf(4, 4, 4, 6, 8, 8, 4)),
        QOption("C", "不责怪但沉默地扛下更多责任", listOf(8, 8, 2, 4, 2, 4, 2)),
        QOption("D", "就地找到掩护，重新评估局势", listOf(2, 2, 8, 2, 6, 2, 8))
    )),
    Question(15, "情景 · 拾伍", "如果可以选择一种力量为你所用，你选择？", listOf(
        QOption("A", "理解一切真相的智慧", listOf(6, 2, 6, 8, 8, 6, 6)),
        QOption("B", "在任何困境中找到出路的变通", listOf(4, 8, 4, 6, 6, 8, 4)),
        QOption("C", "直面一切阻碍的勇气", listOf(2, 6, 8, 4, 4, 4, 8)),
        QOption("D", "连接他人内心的共鸣力", listOf(8, 4, 2, 2, 2, 2, 2))
    )),
    Question(16, "情景 · 拾陆", "你更认同以下哪句话作为你的人生信条？", listOf(
        QOption("A", "宁愿直面一切，也不在沉默中等待", listOf(4, 6, 8, 6, 6, 6, 8)),
        QOption("B", "刀尖上跳舞，才能活得更精彩", listOf(2, 4, 6, 4, 8, 8, 6)),
        QOption("C", "誓言一旦许下，便是永恒", listOf(6, 8, 4, 2, 4, 4, 4)),
        QOption("D", "理解比战胜更重要", listOf(8, 2, 2, 8, 2, 2, 2))
    )),
    Question(17, "情景 · 拾柒", "你发现一宗走私狂厄制品的案件涉及高层，你？", listOf(
        QOption("A", "收集全部证据再动手，不打草惊蛇", listOf(6, 8, 6, 8, 6, 6, 6)),
        QOption("B", "联合可信的同伴一起介入调查", listOf(8, 6, 4, 6, 4, 4, 4)),
        QOption("C", "直接闯入他们的据点一探究竟", listOf(4, 4, 8, 4, 2, 2, 8)),
        QOption("D", "假设对方已知道你，于是反向设局", listOf(2, 2, 2, 2, 8, 8, 2))
    )),
    Question(18, "情景 · 拾捌", "一条关键线索在最后关头中断了，你？", listOf(
        QOption("A", "回溯所有经过的路径寻找遗漏", listOf(6, 4, 5, 8, 6, 4, 5)),
        QOption("B", "退一步思考，换一个方向重新出发", listOf(4, 8, 5, 6, 4, 8, 5)),
        QOption("C", "不甘心，反复推敲线索中的每处细节", listOf(2, 6, 5, 4, 8, 2, 5)),
        QOption("D", "相信直觉，试试之前忽略的那条岔路", listOf(8, 2, 5, 2, 2, 6, 5))
    )),
    Question(19, "情景 · 拾玖", "你需要在两个同样值得信任的同伴之间选一个去执行生死任务，你？", listOf(
        QOption("A", "选择能力更强完成几率更高的一方", listOf(4, 8, 5, 8, 8, 6, 5)),
        QOption("B", "选择更需要被证明自己的那一方", listOf(8, 6, 5, 4, 4, 4, 5)),
        QOption("C", "问他们谁愿意去", listOf(6, 2, 5, 6, 2, 2, 5)),
        QOption("D", "同时派出两人，一个明一个暗", listOf(2, 4, 5, 2, 6, 8, 5))
    )),
    Question(20, "情景 · 贰拾", "大批暴徒正在冲击MBCC的入口，你只有不到三分钟部署，你？", listOf(
        QOption("A", "立刻安排所有能战人员封锁入口", listOf(6, 8, 8, 6, 4, 6, 8)),
        QOption("B", "快速判断暴徒的薄弱环节重点打击", listOf(4, 6, 6, 4, 8, 8, 6)),
        QOption("C", "先疏散非战斗人员至安全区", listOf(8, 4, 4, 2, 2, 4, 2)),
        QOption("D", "启动应急防御系统，智取而非硬拼", listOf(2, 2, 2, 8, 6, 2, 4))
    )),
    Question(21, "情景 · 廿壹", "一个被狂厄侵蚀的人对你说\"带我走\"，你注意到他意识尚存，但随时会失控，你？", listOf(
        QOption("A", "伸出手拉住他——即使有风险也不能放弃", listOf(6, 6, 6, 6, 6, 5, 6)),
        QOption("B", "先镇定地评估他的侵蚀程度再做决定", listOf(4, 4, 4, 8, 8, 5, 4)),
        QOption("C", "用温和但坚定的语气让他保持清醒", listOf(8, 8, 2, 4, 4, 5, 2)),
        QOption("D", "后退一步，准备好随时压制他", listOf(2, 2, 8, 2, 2, 5, 8))
    )),
    Question(22, "情景 · 廿贰", "你知道某个人在暗中帮助你，但从不露面。你最可能？", listOf(
        QOption("A", "设下一个局，让他不得不现身", listOf(6, 4, 5, 8, 8, 8, 5)),
        QOption("B", "尊重他的选择，但默默为他留一条退路", listOf(8, 6, 5, 4, 4, 4, 5)),
        QOption("C", "从每次帮助的蛛丝马迹中拼出他的身份", listOf(4, 2, 5, 6, 6, 2, 5)),
        QOption("D", "顺其自然，保持警觉但不刻意追寻", listOf(2, 8, 5, 2, 2, 6, 5))
    )),
    Question(23, "情景 · 廿叁", "审讯一名拒不开口的禁闭者，你选择哪种方式？", listOf(
        QOption("A", "用人性化的对话建立信任关系", listOf(8, 4, 5, 6, 5, 5, 5)),
        QOption("B", "用逻辑和证据链条使其无法否认", listOf(4, 6, 5, 8, 5, 5, 5)),
        QOption("C", "制造心理压力让他自己崩溃", listOf(2, 8, 5, 4, 5, 5, 5)),
        QOption("D", "用同理心感知他沉默背后的真实动机", listOf(6, 2, 5, 2, 5, 5, 5))
    )),
    Question(24, "情景 · 廿肆", "你的计划被意想不到的突发情况完全打乱，你？", listOf(
        QOption("A", "迅速重新制定新计划", listOf(5, 6, 6, 6, 8, 6, 5)),
        QOption("B", "在混乱中寻找可以利用的新机会", listOf(5, 4, 4, 4, 6, 8, 5)),
        QOption("C", "坚持原计划的核心目标，调整方法", listOf(5, 8, 8, 2, 2, 4, 5)),
        QOption("D", "暂时停下来收集更多情报再做下一步", listOf(5, 2, 2, 8, 4, 2, 5))
    )),
    Question(25, "情景 · 廿伍", "你必须在极短时间内学会一项陌生技能才能脱困，你如何学？", listOf(
        QOption("A", "直接上手尝试，在实践中快速掌握", listOf(6, 8, 8, 4, 4, 8, 5)),
        QOption("B", "先弄懂核心原理再动手做", listOf(4, 4, 6, 6, 6, 6, 5)),
        QOption("C", "向身边有经验的人请教", listOf(8, 2, 4, 8, 2, 4, 5)),
        QOption("D", "拆解成小步骤，逐一攻克", listOf(2, 6, 2, 2, 8, 2, 5))
    )),
    Question(26, "情景 · 廿陆", "你收到一封来自狂厄核心的神秘来信，信上只有一个符号，你？", listOf(
        QOption("A", "将这个符号与所有已知档案比对", listOf(4, 4, 5, 8, 8, 6, 6)),
        QOption("B", "相信这个符号一定有深层含义，直觉解读它", listOf(8, 2, 5, 6, 6, 8, 4)),
        QOption("C", "试着画出这个符号感知它的能量", listOf(6, 6, 5, 4, 4, 4, 2)),
        QOption("D", "销毁它，不做任何回应", listOf(2, 8, 5, 2, 2, 2, 8))
    )),
    Question(27, "情景 · 廿柒", "你的同伴被敌方俘虏，敌方开出条件让你单独赴约，你？", listOf(
        QOption("A", "赴约但不按对方的规则来，自己准备一套", listOf(6, 6, 6, 6, 8, 8, 6)),
        QOption("B", "拒绝单独赴约，组织精锐力量突击救人", listOf(8, 8, 8, 4, 2, 4, 8)),
        QOption("C", "赴约并带上隐藏的援助", listOf(4, 4, 4, 2, 6, 6, 4)),
        QOption("D", "用情报交换人质", listOf(2, 2, 2, 8, 4, 2, 2))
    )),
    Question(28, "情景 · 廿捌", "你发现自己的双手开始出现狂厄侵蚀的迹象，你第一反应是？", listOf(
        QOption("A", "隐瞒迹象，加速完成任务", listOf(5, 8, 8, 4, 4, 6, 8)),
        QOption("B", "立即上报并接受隔离检查", listOf(5, 6, 6, 6, 2, 4, 6)),
        QOption("C", "尝试自己控制侵蚀的扩散", listOf(5, 4, 4, 2, 6, 8, 4)),
        QOption("D", "记录下身体的每一丝变化作为研究数据", listOf(5, 2, 2, 8, 8, 2, 2))
    )),
    Question(29, "情景 · 廿玖", "你看见一个孩子被困在燃烧的废墟中，但你本身也负有重要使命，你？", listOf(
        QOption("A", "毫不犹豫先救孩子——使命可以重来", listOf(8, 6, 6, 6, 6, 4, 8)),
        QOption("B", "在保证使命可完成的前提下设法施救", listOf(2, 8, 4, 4, 8, 2, 4)),
        QOption("C", "寻找附近的其他人帮忙救助", listOf(6, 2, 2, 8, 4, 8, 2)),
        QOption("D", "用最快速度冲进去救出孩子再追使命", listOf(4, 4, 8, 2, 2, 6, 6))
    )),
    Question(30, "情景 · 叁拾", "你赢得了一场重要胜利，但手段有违你一贯的原则，你？", listOf(
        QOption("A", "接受结果——生存本身就是最大的原则", listOf(2, 8, 5, 6, 8, 5, 5)),
        QOption("B", "内心煎熬，但不会让任何人看出来", listOf(6, 4, 5, 4, 4, 5, 5)),
        QOption("C", "重新审视自己的原则是否需要调整", listOf(4, 6, 5, 8, 6, 5, 5)),
        QOption("D", "弥补自己的过错，找到两全之道", listOf(8, 2, 5, 2, 2, 5, 5))
    )),
    Question(31, "情景 · 卅壹", "你怀疑最亲近的同伴暗中被狂厄操控了，但你没有任何证据，你？", listOf(
        QOption("A", "暗中观察他的所有行动寻找破绽", listOf(6, 6, 5, 8, 6, 6, 5)),
        QOption("B", "直接与他开诚布公地谈你的怀疑", listOf(8, 4, 5, 4, 4, 4, 5)),
        QOption("C", "设计一次试探来判断他的反应", listOf(4, 2, 5, 2, 8, 8, 5)),
        QOption("D", "暂时不动作，但暗中做好最坏的准备", listOf(2, 8, 5, 6, 2, 2, 5))
    )),
    Question(32, "情景 · 卅贰", "你认同哪一种对\"力量\"的描述？", listOf(
        QOption("A", "力量是责任——拥有越多背负越多", listOf(8, 6, 6, 6, 4, 5, 6)),
        QOption("B", "力量是工具——关键在于如何使用", listOf(6, 4, 4, 8, 8, 5, 4)),
        QOption("C", "力量是本能——压抑它不如驾驭它", listOf(4, 2, 8, 4, 2, 5, 8)),
        QOption("D", "力量是契约——得到多少就要付出多少", listOf(2, 8, 2, 2, 6, 5, 2))
    )),
    Question(33, "情景 · 卅叁", "你醒来时发现自己躺在陌生的实验台上，记忆一片空白，你第一件事是？", listOf(
        QOption("A", "保持躺姿不动，用余光扫视周围获取信息", listOf(4, 8, 6, 6, 8, 8, 6)),
        QOption("B", "挣脱束缚，攻击视线内的第一个目标", listOf(2, 6, 8, 4, 4, 4, 8)),
        QOption("C", "大声呼救试探这里是否有其他人", listOf(8, 2, 4, 2, 2, 6, 4)),
        QOption("D", "闭上眼睛仔细回忆最后记得的画面", listOf(6, 4, 2, 8, 6, 2, 2))
    )),
    Question(34, "情景 · 卅肆", "黑环吞噬了半个城区，残存信号显示一位禁闭者被困在震中，你？", listOf(
        QOption("A", "组织精英小队强行突入震中救援", listOf(6, 8, 8, 4, 4, 5, 8)),
        QOption("B", "分析黑环扩张模式，寻找安全路径切入", listOf(4, 4, 4, 8, 8, 5, 6)),
        QOption("C", "先与被困禁闭者建立精神链接确认状态", listOf(8, 2, 2, 2, 6, 5, 4)),
        QOption("D", "在外围建立阵地，一步步向核心推进", listOf(2, 6, 6, 6, 2, 5, 2))
    )),
    Question(35, "情景 · 卅伍", "你发现自己的记忆被多次篡改过，真正的过去可能非常黑暗。面对真相，你？", listOf(
        QOption("A", "不惜一切代价找回所有失去的记忆", listOf(4, 8, 5, 6, 6, 4, 5)),
        QOption("B", "过去的我不等于现在的我——接受就够了", listOf(8, 6, 5, 4, 2, 2, 5)),
        QOption("C", "从旁人的碎片叙述中拼凑真实的历史", listOf(6, 2, 5, 8, 4, 8, 5)),
        QOption("D", "追踪篡改记忆的源头，直接找到幕后黑手", listOf(2, 4, 5, 2, 8, 6, 5))
    )),
    Question(36, "情景 · 卅陆", "你面对的敌人拥有镜像一切攻击的能力。你如何击败他？", listOf(
        QOption("A", "先观察他镜像能力的范围和延迟再出手", listOf(5, 8, 6, 6, 8, 6, 6)),
        QOption("B", "用他自己无法预测的方式进行攻击", listOf(5, 6, 4, 4, 4, 8, 4)),
        QOption("C", "放弃攻击，寻找能力本身的逻辑缺陷", listOf(5, 4, 2, 8, 6, 4, 2)),
        QOption("D", "用无差别的范围伤害让他的镜像失去意义", listOf(5, 2, 8, 2, 2, 2, 8))
    )),
    Question(37, "情景 · 卅柒", "一个狂厄的信徒对你说\"黑暗才是归宿\"。你如何回应？", listOf(
        QOption("A", "黑暗不是归宿，而是我们正在穿越的隧道", listOf(4, 6, 5, 8, 8, 5, 6)),
        QOption("B", "或许他说得对——但我的路该由我自己选", listOf(2, 8, 5, 6, 4, 5, 8)),
        QOption("C", "沉默——黑暗不需要回应，只需要被照亮", listOf(8, 4, 5, 4, 2, 5, 4)),
        QOption("D", "问他为什么相信黑暗——他的理由比结论重要", listOf(6, 2, 5, 2, 6, 5, 2))
    )),
    Question(38, "情景 · 卅捌", "你被迫和一个曾经背叛过你的人并肩作战。你？", listOf(
        QOption("A", "利用他的能力但不给予任何信任", listOf(4, 6, 5, 5, 8, 8, 5)),
        QOption("B", "既然选择了合作就暂时放下过去", listOf(8, 4, 5, 5, 2, 6, 5)),
        QOption("C", "时刻保持距离，把自己的后背交给墙壁", listOf(2, 8, 5, 5, 6, 4, 5)),
        QOption("D", "在他的行动中观察他是否值得第二次机会", listOf(6, 2, 5, 5, 4, 2, 5))
    )),
    Question(39, "情景 · 卅玖", "一切迹象都指向你身边的某个人是内鬼，但你的直觉告诉你不是他。你？", listOf(
        QOption("A", "相信证据——直觉在事实面前不值一提", listOf(6, 6, 5, 8, 8, 4, 5)),
        QOption("B", "相信直觉——证据可能被人故意布置", listOf(8, 2, 5, 4, 6, 6, 5)),
        QOption("C", "同时追查证据的来源和直觉的根源", listOf(4, 8, 5, 6, 4, 2, 5)),
        QOption("D", "装作已经认定是他，观察所有人的反应", listOf(2, 4, 5, 2, 2, 8, 5))
    )),
    Question(40, "情景 · 肆拾", "你拥有一项能力：能看到每个人身上最深的恐惧。你会如何使用？", listOf(
        QOption("A", "用恐惧的根源去抚慰而非刺痛他人", listOf(8, 8, 5, 4, 4, 5, 5)),
        QOption("B", "了解对手的恐惧来掌握谈判的主动权", listOf(2, 6, 5, 6, 6, 5, 5)),
        QOption("C", "尽量不去看——每个人都有保留恐惧的权利", listOf(6, 4, 5, 2, 2, 5, 5)),
        QOption("D", "记录和分析恐惧的共性来理解人性本质", listOf(4, 2, 5, 8, 8, 5, 5))
    )),
    Question(41, "情景 · 肆壹", "你收到了一个来自\"未来的自己\"的警告信息。你怀疑这是陷阱，但信息中提到了一个只有你自己知道的秘密。你？", listOf(
        QOption("A", "按信息中的指示去做——未来的自己不会害现在的自己", listOf(6, 8, 5, 4, 2, 8, 5)),
        QOption("B", "反向行动——如果这是陷阱，反着来就是对的", listOf(4, 6, 5, 2, 6, 6, 5)),
        QOption("C", "分析信息的来源、加密方式、笔迹和心理特征", listOf(2, 4, 5, 8, 8, 4, 5)),
        QOption("D", "回信给未来的自己，要求更多证据", listOf(8, 2, 5, 6, 4, 2, 5))
    )),
    Question(42, "情景 · 肆贰", "狂厄风暴中，你听到已故战友的声音在呼唤你。你清楚他不在了，但声音如此真实。你？", listOf(
        QOption("A", "寻找声音的来源——即使知道是幻觉也不愿放弃", listOf(8, 6, 6, 6, 6, 5, 6)),
        QOption("B", "闭上眼睛驻足片刻，与他做最后的告别", listOf(4, 8, 4, 4, 4, 5, 4)),
        QOption("C", "分析声音的频率和方向来定位狂厄风暴的眼", listOf(2, 4, 2, 8, 8, 5, 2)),
        QOption("D", "大声回应他——让他知道你没有忘记", listOf(6, 2, 8, 2, 2, 5, 8))
    )),
    Question(43, "情景 · 肆叁", "你的敌人给了你一个选择：放弃原则可以拯救所有人的性命。你？", listOf(
        QOption("A", "放弃原则——人命比原则更重", listOf(8, 4, 5, 4, 4, 5, 6)),
        QOption("B", "不放弃原则——没有原则的世界与深渊无异", listOf(6, 8, 5, 8, 2, 5, 4)),
        QOption("C", "另寻第三条路——敌人给出选择一定有他的目的", listOf(4, 6, 5, 6, 8, 5, 2)),
        QOption("D", "放弃原则但记下这笔账——之后让他加倍偿还", listOf(2, 2, 5, 2, 6, 5, 8))
    )),
    Question(44, "情景 · 肆肆", "你发现自己正在变成曾经最痛恨的那种人。你察觉到了这个趋势。你？", listOf(
        QOption("A", "停下来——重新审视自己的每一步是否走偏", listOf(6, 8, 6, 6, 6, 5, 5)),
        QOption("B", "继续前进——只有走到终点才有资格评判自己", listOf(4, 6, 8, 4, 4, 5, 5)),
        QOption("C", "告诉身边信任的人，让他们在你失控时拉住你", listOf(8, 4, 4, 2, 2, 5, 5)),
        QOption("D", "分析\"成为那种人\"的具体行为特征并逐条纠正", listOf(2, 2, 2, 8, 8, 5, 5))
    )),
    Question(45, "情景 · 肆伍", "有一个选择可以让你获得巨大的力量但代价是失去一段最重要的记忆。你？", listOf(
        QOption("A", "绝不交换——记忆定义了你是谁", listOf(8, 8, 5, 4, 4, 5, 6)),
        QOption("B", "接受——忘记才能更好地前行", listOf(6, 6, 5, 2, 2, 5, 4)),
        QOption("C", "询问更多细节再做决定", listOf(4, 2, 5, 8, 6, 5, 2)),
        QOption("D", "寻找不用牺牲记忆也能获得力量的替代方案", listOf(2, 4, 5, 6, 8, 5, 8))
    )),
    Question(46, "情景 · 肆陆", "全城陷入混乱，所有人都在朝一个方向奔逃，而你听到了反方向有微弱的求救声。你？", listOf(
        QOption("A", "逆着人流朝求救声的方向跑去", listOf(8, 8, 8, 6, 4, 2, 8)),
        QOption("B", "先确认求救位置是否安全再决定", listOf(4, 6, 6, 4, 8, 8, 4)),
        QOption("C", "拦住几个人一起去救助", listOf(6, 2, 4, 8, 2, 6, 6)),
        QOption("D", "观察人群奔逃的方向判断真正的危险来源", listOf(2, 4, 2, 2, 6, 4, 2))
    )),
    Question(47, "情景 · 肆柒", "一个从未输过的对手站在你面前。没有任何已知的弱点。你？", listOf(
        QOption("A", "制造他从未遇到过的情况，逼他露出破绽", listOf(6, 8, 6, 6, 6, 8, 6)),
        QOption("B", "从他的过往所有战斗记录中寻找隐藏模式", listOf(4, 2, 4, 8, 8, 6, 4)),
        QOption("C", "正面挑战他——不败的传说只有一个办法终结", listOf(2, 6, 8, 4, 4, 4, 8)),
        QOption("D", "试图谈判——这样的对手不必成为敌人", listOf(8, 4, 2, 2, 2, 2, 2))
    )),
    Question(48, "情景 · 肆捌", "你察觉到自己正在被狂厄改变，而这种改变有时会带来意想不到的好处。你如何面对这种变化？", listOf(
        QOption("A", "警惕每一次改变——任何好处都有隐藏的代价", listOf(6, 8, 5, 8, 6, 5, 5)),
        QOption("B", "接受改变但保持自我的核心不变", listOf(8, 6, 5, 2, 2, 5, 5)),
        QOption("C", "利用好处的同时寻找逆转侵蚀的方法", listOf(4, 4, 5, 6, 4, 5, 5)),
        QOption("D", "观察其他被改变者来预测自己的未来", listOf(2, 2, 5, 4, 8, 5, 5))
    )),
    Question(49, "情景 · 肆玖", "最终抉择前夜，你独自站在天台上俯瞰狄斯城。这座城市让你想到什么？", listOf(
        QOption("A", "一座巨大的牢笼——但也可能是每个人的庇护所", listOf(6, 8, 6, 6, 4, 6, 5)),
        QOption("B", "一个等待被打破的茧——新的世界将在废墟上生长", listOf(4, 6, 8, 4, 2, 8, 5)),
        QOption("C", "无数个互相依赖也互相伤害的生命", listOf(8, 2, 4, 2, 8, 4, 5)),
        QOption("D", "一盘复杂的棋局——而每一步都在改变它的走向", listOf(2, 4, 2, 8, 6, 2, 5))
    )),
    Question(50, "情景 · 伍拾", "一切尘埃落定之后，你希望别人如何记住你？", listOf(
        QOption("A", "一个在黑暗中点燃火把的人", listOf(8, 6, 6, 6, 4, 6, 5)),
        QOption("B", "一个改变了游戏规则的人", listOf(6, 4, 4, 8, 8, 4, 5)),
        QOption("C", "一个从未背叛过自己的人", listOf(4, 8, 8, 4, 2, 2, 5)),
        QOption("D", "一个让不可能成为可能的人", listOf(2, 2, 2, 2, 6, 8, 5))
    ))
)

// ===== 狂厄共鸣测试 · 12题（对齐 HTML RESONANCE_QUESTIONS） =====
val RESONANCE_QUESTIONS: List<ResQuestion> = listOf(
    ResQuestion(1, "零镜模拟 · 壹", "深夜你独自穿行在狄斯城的废弃街区，一阵诡谲的低语从暗巷深处传来。你的第一反应是？", listOf(
        ResOption("A", "停下脚步，释放感知，试图感受周围的情绪波动与生命气息", mapOf("enlighten" to 3)),
        ResOption("B", "循声而去——你被一种莫名的直觉牵引，认为那里有你需要的东西", mapOf("mystic" to 3)),
        ResOption("C", "握紧拳头，压低重心，径直朝声音来源走去", mapOf("ferocious" to 3)),
        ResOption("D", "仔细辨别声音的方向、频率和性质，判断是否构成真实威胁", mapOf("precise" to 3))
    )),
    ResQuestion(2, "零镜模拟 · 贰", "你发现自己的梦境正在侵蚀现实——昨晚梦到的场景，今天在真实世界中重现了。你？", listOf(
        ResOption("A", "尝试控制梦境，将噩梦一步步改写成你想要的图景", mapOf("anomaly" to 3)),
        ResOption("B", "详细记录每一次梦魇，寻找其中的规律与预警信息", mapOf("precise" to 3)),
        ResOption("C", "跟随梦境的指引——你的直觉不会无缘无故地为你展示这些", mapOf("mystic" to 3)),
        ResOption("D", "接纳它——你的狂厄正在觉醒，与其对抗不如学会共存", mapOf("ferocious" to 3))
    )),
    ResQuestion(3, "零镜模拟 · 叁", "你的同伴被狂厄感染，正在失控异化。他恳求你在彻底失控前杀了他。你？", listOf(
        ResOption("A", "拒绝——只要他还有一丝意识，你就绝不会放弃寻找解救的方法", mapOf("tenacious" to 3)),
        ResOption("B", "尝试用自己的精神频率介入他的狂厄波动，将他从深渊拉回", mapOf("enlighten" to 3)),
        ResOption("C", "仔细观察他的异化过程，寻找狂厄侵蚀的可逆规律", mapOf("precise" to 3)),
        ResOption("D", "闭上眼睛完成他的请求——但你会记住这一刻，并让制造这一切的人付出代价", mapOf("ferocious" to 3))
    )),
    ResQuestion(4, "零镜模拟 · 肆", "你被诬陷犯下了重罪，证据确凿，百口莫辩。你的应对是？", listOf(
        ResOption("A", "安静地承受一切——清者自清，时间会证明你的坚韧", mapOf("tenacious" to 3)),
        ResOption("B", "在庭上释放你的精神力量，让在场所有人直视你记忆中的真相", mapOf("anomaly" to 3)),
        ResOption("C", "逐条分析指控材料中的漏洞，用无可辩驳的逻辑反击", mapOf("precise" to 3)),
        ResOption("D", "越狱——清白的人不需要在牢笼里证明任何事", mapOf("ferocious" to 3))
    )),
    ResQuestion(5, "零镜模拟 · 伍", "你在废墟中发现了一名昏迷的禁闭者，她醒来后对你充满敌意和猜忌。你如何取得她的信任？", listOf(
        ResOption("A", "用温和的态度和耐心的陪伴让她感受到你的善意", mapOf("enlighten" to 3)),
        ResOption("B", "用轻松诙谐的方式打破紧张——让她明白你并非威胁", mapOf("mystic" to 3)),
        ResOption("C", "向她展示前方的危险与你手中的资源——让她明白你需要彼此", mapOf("tenacious" to 3)),
        ResOption("D", "保持距离，用行动而非言语证明你的可信", mapOf("precise" to 3))
    )),
    ResQuestion(6, "零镜模拟 · 陆", "你最好的朋友突然对你刀剑相向，他的眼中没有任何你熟悉的光芒。你？", listOf(
        ResOption("A", "不闪躲也不还手——你相信他的内心深处还有未被磨灭的自我", mapOf("enlighten" to 3)),
        ResOption("B", "正面迎战——如果战斗是唤醒他的唯一方式，你会打到他清醒为止", mapOf("ferocious" to 3)),
        ResOption("C", "在他攻击的间隙中寻找规律，判断他是被操控还是彻底异化", mapOf("precise" to 3)),
        ResOption("D", "在交手中感知他身上的异常频率，追溯这股力量的源头", mapOf("mystic" to 3))
    )),
    ResQuestion(7, "零镜模拟 · 柒", "你发现了关于这个世界的可怕真相——你所认知的现实只是冰山一角，水面之下是无穷的混沌。你的反应是？", listOf(
        ResOption("A", "接受混沌——你早就隐隐感觉世界不该如此简单明了", mapOf("mystic" to 3)),
        ResOption("B", "用你的意志重新编织认知体系——真实与否由你定义", mapOf("anomaly" to 3)),
        ResOption("C", "无论真相如何，你依然会选择守护眼前这个真实的世界", mapOf("tenacious" to 3)),
        ResOption("D", "系统性验证这个\"真相\"——确认它是否真的是全部事实", mapOf("precise" to 3))
    )),
    ResQuestion(8, "零镜模拟 · 捌", "你的内心住着一个声音，不断劝你放弃、妥协、屈服于狂厄。你？", listOf(
        ResOption("A", "与它对话——也许它想告诉你的不是放弃，而是另一条被你忽略的路", mapOf("mystic" to 3)),
        ResOption("B", "用更强大的意志压制它——你的心志比它的低语更响亮", mapOf("tenacious" to 3)),
        ResOption("C", "接纳它作为自己的一部分——你不需要对抗自己", mapOf("enlighten" to 3)),
        ResOption("D", "仔细分析这个声音的来源和目的——它的存在一定有某种意义", mapOf("precise" to 3))
    )),
    ResQuestion(9, "零镜模拟 · 玖", "一场激战之后，你发现敌方首领有一个致命弱点。但攻击这个弱点会波及无辜。你？", listOf(
        ResOption("A", "寻找另一条结束战斗的路径——不该有无辜者为此牺牲", mapOf("enlighten" to 3)),
        ResOption("B", "利用这个信息作为谈判筹码，逼迫对方妥协", mapOf("tenacious" to 3)),
        ResOption("C", "精密计算伤害范围，以最小代价完成致命一击", mapOf("precise" to 3)),
        ResOption("D", "攻击弱点——战争的残酷本就没有完美选项，犹豫会害死更多人", mapOf("ferocious" to 3))
    )),
    ResQuestion(10, "零镜模拟 · 拾", "你获得了一面传说中能映照出灵魂真实形态的镜子。你在镜中看到了什么？", listOf(
        ResOption("A", "一片不断变化的混沌星云，形态流转不定", mapOf("anomaly" to 3)),
        ResOption("B", "一道穿透黑暗的锐利光束，所过之处一切无所遁形", mapOf("precise" to 3)),
        ResOption("C", "一面破碎但仍在努力拼合自己的影像", mapOf("tenacious" to 3)),
        ResOption("D", "你无法描述你看到的景象——语言在它面前彻底失去了意义", mapOf("mystic" to 3))
    )),
    ResQuestion(11, "零镜模拟 · 拾壹", "在狂厄风暴的中心，你听到了一段只有你能感知到的奇异频率。你？", listOf(
        ResOption("A", "尝试与这段频率共鸣——它在用某种语言向你传递信息", mapOf("enlighten" to 3)),
        ResOption("B", "分析频率的结构和波形，解码其中隐藏的信息", mapOf("precise" to 3)),
        ResOption("C", "让频率引导你的行动——它知道你应该去哪里", mapOf("mystic" to 3)),
        ResOption("D", "释放你自己的频率，与它对抗——你绝不会被任何力量操控", mapOf("ferocious" to 3))
    )),
    ResQuestion(12, "零镜模拟 · 拾贰", "最终抉择——你站在狂厄深渊的边缘，下方是无尽黑暗。你听到一个声音说：\"跳下来，你将获得真正的自由。\"你？", listOf(
        ResOption("A", "纵身跃入——真正的自由属于敢于拥抱未知的人", mapOf("ferocious" to 3)),
        ResOption("B", "在坠落中重塑自己的形态——既然选择了深渊，就让它为你所用", mapOf("anomaly" to 3)),
        ResOption("C", "留在原地——真正的力量不在于征服深渊，而在于不被它吞噬", mapOf("tenacious" to 3)),
        ResOption("D", "倾听深渊的回音——在你做出选择之前，你需要理解它究竟是什么", mapOf("mystic" to 3))
    ))
)

// ===== 成就（对齐 HTML ACHIEVEMENTS，20 条，含死条目 basic 原样保留；icon = Lucide 图标名，见 ui/icons/LucideIcons.kt） =====
val ACHIEVEMENTS: List<Achievement> = listOf(
    Achievement("first", "clipboard-check", "初次鉴定", "完成第一次人格鉴定"),
    Achievement("basic", "target", "基础鉴定", "完成一次16题基础鉴定"),
    Achievement("advanced", "microscope", "深度鉴定", "完成一次32题深度鉴定"),
    Achievement("resonance_m", "waves", "狂厄共振", "完成一次50题狂厄共振鉴定"),
    Achievement("char_juzhang", "flame", "希望之火", "鉴定结果匹配到局长"),
    Achievement("char_serin", "grid-3x3", "执棋者", "鉴定结果匹配到瑟琳"),
    Achievement("char_zoya", "swords", "不灭战意", "鉴定结果匹配到卓娅"),
    Achievement("char_hella", "cat", "野猫的直觉", "鉴定结果匹配到海拉"),
    Achievement("char_hecate", "moon", "深渊誓言", "鉴定结果匹配到赫卡蒂"),
    Achievement("char_baiyi", "venetian-mask", "千面人", "鉴定结果匹配到白逸"),
    Achievement("char_irina", "crown", "王座之下", "鉴定结果匹配到伊琳娜"),
    Achievement("char_langley", "network", "蛛网中心", "鉴定结果匹配到兰利"),
    Achievement("scholar", "graduation-cap", "人格学者", "完成 3 次鉴定"),
    Achievement("collector", "trophy", "全收集者", "解锁全部 8 名禁闭者"),
    Achievement("omniscient", "eye", "全知者", "解锁全部禁闭者与全部狂厄特化"),
    Achievement("first_res", "radio", "初次共鸣", "完成第一次狂厄共鸣测试"),
    Achievement("master_res", "infinity", "特化大师", "解锁全部 6 种狂厄特化"),
    Achievement("customizer", "brush", "个人定制", "设置头像、昵称与个性签名"),
    Achievement("colorist", "palette", "色彩学家", "更换过主题配色"),
    Achievement("test_frenzy", "zap", "鉴定狂热", "完成 10 次鉴定")
)

// ===== 公告（对齐 HTML ANNOUNCEMENTS；只增不删，最新 id 在前） =====
val ANNOUNCEMENTS: List<Announcement> = listOf(
    Announcement(
        8, "2026.11.03",
        "【无期：人格鉴定 · v1.5.0 后续停更公告】\n" +
            "软件里的七维数据出自官方美术设定集第一册。目前官方只出到第一册，所以能覆盖的只有前三周年的角色；" +
            "之后新增的角色没有官方数据可以参照，也就没有继续加。现在角色库一共九十多位，基本到这里了。\n" +
            "测试这类东西的兴趣都停在最初的一两次——答案揭晓、好奇满足，之后就不太会再打开。这大概是这类玩法本身的节奏。" +
            "另一方面，角色数据只能以官方设定集为依据，没有新的数据可用。\n" +
            "所以经过这段时间的考虑，v1.5.0 会是最后一个版本：后续停止更新，同时开源。\n" +
            "停更之前，我们把两种需求都照顾了一下：\n" +
            "· 保留了严格模式（30 / 40 / 50 题），给认真测一次、想要相对准确结果的人；\n" +
            "· 新增了娱乐模式（12 / 20 题），会优先补全你还没收集到的角色，给想集角色的人。\n" +
            "如果以后官方出了第二册设定集，也许还会有继续做下去的可能。只是到那时候，可能不会再是现在这个软件，而是以另一种形式出现。" +
            "如果真有那一天，希望还能再见。\n" +
            "感谢每一位用过它的人。\n" +
            "—— 海与燕制作组",
        "海与燕制作组"
    ),
    Announcement(
        7, "2026.11.03",
        "【v1.5.0 · 禁闭者图鉴扩充】\n" +
            "这一次，狄斯城更多的档案被解锁了：新增 78 名禁闭者，全库共 98 名（含局长）。\n" +
            "图鉴新增「狂厄级 / 高危级 / 普管级 / 特殊」四种等级，可按等级筛选查找。\n" +
            "新增「局长档案」：完成任意一次鉴定后，自动生成属于你自己的七维画像。\n" +
            "鉴定新增「严格 / 娱乐」两栏共五档，另新增 6 套主题配色与深色模式。\n" +
            "完整改动请看「全部设置 → 关于 → 更新日志」。",
        "海与燕制作组"
    ),
    Announcement(
        6, "2026.10.02",
        "【v1.4.0 · 迄今为止最大的一次更新】\n" +
            "这一次，底层进行了重大更新，也是《无期：人格鉴定》发布以来最大的一次版本升级。\n" +
            "整套界面由内到外重新打造：全新配色、全新动画、全新结果页与保存图，更清晰也更有质感。\n" +
            "从立项到落地，历经一个多月的反复打磨，也投入了不少资金与精力，只为让这台「鉴定仪」更好用一点。\n" +
            "同时新增「反馈中心」与「崩溃日志」，并修复了大家反馈的多个问题。\n" +
            "感谢每一位还在使用的你——这一次，请重新认识它。\n" +
            "完整改动请看「我的 → 关于 → 更新日志」。",
        "海与燕制作组"
    ),
    Announcement(4, "2026.07.05", "我们新增了一些有趣的功能，欢迎体验 请在更新日志查看完整内容", "海与燕制作组")
)

// ===== 档位与共鸣分数常量 =====
/** 档位题数：2=深度鉴定32题，3=狂厄共振50题（对齐 HTML TIER_LIMITS） */
/**
 * 档位题数（v1.5.2 五档，分「严格 / 娱乐」两个 tab）：
 *  - 严格（纯人格匹配，不做收集补全）：30 / 40 / 50 题
 *  - 娱乐（含收集补全）：12 / 20 题
 * 题量上限 = 题库总数（50）；再往上加题需要先扩充题库。
 */
val TIER_LIMITS = mapOf(
    1 to 12,   // 娱乐 · 快速
    2 to 20,   // 娱乐 · 标准
    3 to 30,   // 严格 · 标准
    4 to 40,   // 严格 · 加强
    5 to 50    // 严格 · 完整（题库上限）
)

/** 狂厄共鸣每维满分 36（12题×3分，对齐 HTML showResonanceResult maxScore） */
const val RESONANCE_MAX_SCORE = 36
