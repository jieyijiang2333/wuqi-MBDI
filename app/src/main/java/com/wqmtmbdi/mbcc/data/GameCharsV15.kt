package com.wqmtmbdi.mbcc.data

/**
 * ===== v1.5.0 全角色扩充（78 名新增）=====
 * 数据来源：《角色数据.txt》官方七维；等级按记账本角色池口径：狂厄级 / 高危级 / 普管级。
 * ★ 内置 20 名老角色的数据一字未动（GameData.kt）；重名角色一律以老数据为准，不再重复收录。
 * ★ 清单由 .workbuddy/tmp/mbdi_gen_v15.py 生成；要改单条文案可直接改这里，重跑脚本会覆盖。
 */

/** v1.5.0 新增角色七维（顺序：[同理心, 意志力, 体质, 教育程度, 智商, 敏捷, 力量]） */
val CHR_STATS_V15: Map<String, List<Int>> = mapOf(
    "xiayin" to listOf(2, 10, 2, 10, 10, 3, 2),
    "tang" to listOf(7, 9, 9, 6, 8, 9, 9),
    "dimeng" to listOf(6, 9, 10, 7, 7, 6, 8),
    "hesitiya" to listOf(1, 10, 4, 3, 9, 6, 5),
    "wanila" to listOf(1, 10, 10, 8, 8, 7, 9),
    "kexi" to listOf(7, 8, 6, 5, 8, 9, 7),
    "duya" to listOf(8, 8, 6, 8, 7, 6, 7),
    "xiangmuxia" to listOf(7, 7, 3, 5, 8, 6, 5),
    "deleiya" to listOf(1, 9, 1, 9, 1, 8, 9),
    "LL" to listOf(3, 7, 2, 6, 7, 3, 2),
    "duruo" to listOf(8, 8, 3, 9, 9, 3, 3),
    "aowu" to listOf(5, 8, 7, 1, 6, 6, 7),
    "yao" to listOf(4, 8, 9, 8, 4, 10, 10),
    "leiwen" to listOf(7, 5, 4, 7, 6, 6, 5),
    "bianka" to listOf(2, 8, 7, 7, 8, 7, 7),
    "yifu" to listOf(4, 8, 2, 6, 7, 2, 4),
    "enfeier" to listOf(7, 7, 4, 7, 7, 5, 6),
    "adailai" to listOf(4, 7, 5, 6, 6, 8, 7),
    "naqia" to listOf(3, 8, 6, 5, 7, 8, 6),
    "nuokesi" to listOf(9, 10, 9, 7, 6, 7, 8),
    "dudu" to listOf(7, 5, 4, 3, 7, 6, 4),
    "yigeni" to listOf(7, 6, 5, 6, 7, 5, 4),
    "kamiliyan" to listOf(10, 8, 4, 9, 9, 5, 5),
    "aien" to listOf(6, 9, 7, 9, 9, 7, 7),
    "puxila" to listOf(7, 5, 4, 6, 8, 9, 7),
    "taitela" to listOf(4, 7, 7, 8, 9, 8, 7),
    "langhuan" to listOf(5, 8, 8, 1, 7, 9, 7),
    "fukesixiansheng" to listOf(2, 6, 5, 8, 9, 6, 4),
    "wendi" to listOf(1, 8, 6, 3, 6, 5, 7),
    "jiushijiu" to listOf(4, 7, 7, 2, 6, 7, 9),
    "weiduoliya" to listOf(2, 8, 2, 3, 6, 2, 1),
    "jin" to listOf(8, 6, 7, 8, 8, 9, 7),
    "qieerxibojue" to listOf(2, 7, 5, 7, 6, 7, 6),
    "lamiya" to listOf(2, 8, 4, 3, 7, 7, 5),
    "heluo" to listOf(6, 8, 7, 5, 6, 6, 9),
    "aolifu" to listOf(8, 8, 3, 3, 8, 6, 3),
    "kelisidina" to listOf(5, 9, 7, 6, 7, 7, 7),
    "airuier" to listOf(9, 8, 3, 7, 5, 5, 4),
    "youni" to listOf(3, 8, 3, 7, 8, 7, 4),
    "shuluma" to listOf(4, 7, 4, 6, 7, 7, 6),
    "pajiaqian" to listOf(5, 9, 8, 7, 8, 5, 8),
    "chensha" to listOf(7, 8, 8, 8, 8, 6, 8),
    "yanyan" to listOf(6, 8, 6, 3, 6, 8, 6),
    "duoli" to listOf(7, 8, 8, 3, 7, 7, 7),
    "luweiya" to listOf(6, 6, 5, 7, 6, 5, 5),
    "kasiluo" to listOf(4, 8, 6, 3, 8, 9, 7),
    "kawakawa" to listOf(6, 9, 8, 8, 8, 5, 7),
    "meisi" to listOf(5, 9, 6, 7, 8, 7, 7),
    "liwei" to listOf(9, 5, 7, 7, 9, 5, 7),
    "an" to listOf(7, 8, 5, 7, 8, 4, 5),
    "maikun" to listOf(7, 5, 6, 7, 8, 7, 6),
    "youlika" to listOf(5, 8, 6, 7, 7, 8, 8),
    "laita" to listOf(7, 6, 3, 6, 7, 3, 3),
    "ruisi" to listOf(4, 8, 5, 7, 8, 6, 7),
    "jialuofanuo" to listOf(9, 8, 6, 7, 8, 7, 6),
    "aike" to listOf(6, 8, 6, 3, 6, 7, 7),
    "kaxiya" to listOf(2, 8, 5, 6, 7, 6, 6),
    "jian" to listOf(4, 8, 7, 7, 7, 8, 8),
    "ji" to listOf(8, 7, 6, 6, 6, 8, 6),
    "luliaika" to listOf(3, 9, 5, 5, 7, 7, 4),
    "lin" to listOf(8, 3, 5, 6, 6, 9, 2),
    "ninuo" to listOf(8, 4, 6, 6, 6, 6, 6),
    "madierda" to listOf(7, 8, 6, 8, 7, 7, 6),
    "xiaoen" to listOf(5, 8, 6, 6, 6, 7, 4),
    "gelan" to listOf(4, 7, 6, 6, 6, 6, 8),
    "leibinisi" to listOf(7, 7, 7, 7, 7, 7, 7),
    "aimipan" to listOf(7, 5, 4, 5, 7, 6, 5),
    "demoli" to listOf(3, 8, 8, 4, 5, 6, 8),
    "fuluola" to listOf(4, 8, 5, 6, 6, 2, 7),
    "koukou" to listOf(7, 8, 8, 5, 6, 5, 7),
    "qiong" to listOf(6, 8, 6, 6, 6, 7, 7),
    "hujiao" to listOf(6, 5, 7, 3, 4, 6, 7),
    "che" to listOf(8, 7, 6, 7, 8, 8, 6),
    "lisha" to listOf(4, 8, 2, 8, 8, 4, 1),
    "tan" to listOf(4, 8, 5, 3, 6, 8, 7),
    "peiji" to listOf(5, 7, 6, 6, 7, 6, 7),
    "maqiduo" to listOf(8, 5, 3, 7, 8, 5, 4),
    "kaierwen" to listOf(5, 5, 4, 7, 6, 4, 6),
)

/** v1.5.0 新增角色档案（代码 / 释义 / 描述） */
val CHARACTERS_V15: List<GameChar> = listOf(
    GameChar(
        id = "xiayin", name = "夏音", code = "ARIA", avatarColor = "#8a3a6a",
        stats = CHR_STATS_V15["xiayin"]!!,
        meanings = listOf(
            Meaning("A", "咏叹（Aria）"), Meaning("R", "声浪（Ripple）"),
            Meaning("I", "偶像（Idol）"), Meaning("A", "仰慕（Adore）")
        ),
        desc = "你生来就属于聚光灯。你知道一段旋律能撬动多少人心，也知道在舞台的背面，掌声从来都有价格。你把最锋利的话唱成最动听的歌——没有人能分辨，也没有人愿意分辨。你就是夏音。"
    ),
    GameChar(
        id = "tang", name = "唐", code = "DICE", avatarColor = "#7a6a3a",
        stats = CHR_STATS_V15["tang"]!!,
        meanings = listOf(
            Meaning("D", "骰子（Dice）"), Meaning("I", "潇洒（Impassioned）"),
            Meaning("C", "胆识（Courage）"), Meaning("E", "从容（Easy）")
        ),
        desc = "你把人生当成一场豪赌，筹码永远全押。输赢对你不重要——重要的是翻牌那一刻的心跳。你笑得漫不经心，出手却从不含糊。规矩是给别人定的，你的世界里只有成与败两种结果。你就是唐。"
    ),
    GameChar(
        id = "dimeng", name = "迪蒙", code = "BULK", avatarColor = "#7a4a2a",
        stats = CHR_STATS_V15["dimeng"]!!,
        meanings = listOf(
            Meaning("B", "蛮力（Brute）"), Meaning("U", "不屈（Unyielding）"),
            Meaning("L", "忠诚（Loyal）"), Meaning("K", "刚健（Keen）")
        ),
        desc = "你的世界很简单：谁对你以诚相待，你就把命交给谁。你不擅长花言巧语，拳头就是你最诚实的语言。别人说你头脑简单，你从不辩解——因为在关键时刻，站在最前面的永远是你。宽厚的背脊扛得起一切。你就是迪蒙。"
    ),
    GameChar(
        id = "hesitiya", name = "赫斯提亚", code = "REBL", avatarColor = "#8a2a3a",
        stats = CHR_STATS_V15["hesitiya"]!!,
        meanings = listOf(
            Meaning("R", "反抗（Rebel）"), Meaning("E", "激进（Extreme）"),
            Meaning("B", "桀骜（Bold）"), Meaning("L", "破格（Loose）")
        ),
        desc = "你从不对任何人低头，规则存在的意义就是让你踩碎。别人眼里的深渊，是你眼里唯一诚实的地方。你不怕被讨厌，也不屑被理解——你只是一直往前走，直到整个世界为你让路。你就是赫斯提亚。"
    ),
    GameChar(
        id = "wanila", name = "瓦尼菈", code = "VALR", avatarColor = "#3a4a7a",
        stats = CHR_STATS_V15["wanila"]!!,
        meanings = listOf(
            Meaning("V", "勇武（Valor）"), Meaning("A", "誓言（Avow）"),
            Meaning("L", "冲锋（Lance）"), Meaning("R", "坚壁（Rampart）")
        ),
        desc = "你把誓言刻在骨头上，把战场当家。怜悯是奢侈品，迟疑是致命伤——所以你把心锻造成钢铁，只为守护你认定的旗帜。敌人畏惧你的冲锋，同伴信赖你的背影。你不需要被爱，你只需要一个值得献身的战场。你就是瓦尼菈。"
    ),
    GameChar(
        id = "kexi", name = "科希", code = "PACE", avatarColor = "#3a5a5a",
        stats = CHR_STATS_V15["kexi"]!!,
        meanings = listOf(
            Meaning("P", "步调（Pace）"), Meaning("A", "机警（Alert）"),
            Meaning("C", "专注（Concentrate）"), Meaning("E", "高效（Efficient）")
        ),
        desc = "你的眼睛永远在记录，你的脚步永远比局势快半拍。你不冲动，也不犹豫——每一件事都被你拆成可以完成的步骤，然后一步不差地执行完。有人觉得你冷淡，其实你只是把热情都留给了工作本身。你就是科希。"
    ),
    GameChar(
        id = "duya", name = "渡鸦", code = "NEWS", avatarColor = "#4a3a5a",
        stats = CHR_STATS_V15["duya"]!!,
        meanings = listOf(
            Meaning("N", "追踪（Navigate）"), Meaning("E", "笔录（Editorial）"),
            Meaning("W", "机智（Wit）"), Meaning("S", "爆料（Scoop）")
        ),
        desc = "这座城市的一切秘密，最终都会落在你的笔记本里。你端着咖啡谈笑风生，耳朵却从不放过任何一条线索。你相信真相是有价码的——而你，恰好是那个开价的人。别人追逐传闻，你贩卖事实。你就是渡鸦。"
    ),
    GameChar(
        id = "xiangmuxia", name = "橡木匣", code = "RELC", avatarColor = "#5a4a3a",
        stats = CHR_STATS_V15["xiangmuxia"]!!,
        meanings = listOf(
            Meaning("R", "遗存（Relic）"), Meaning("E", "慧眼（Examine）"),
            Meaning("L", "珍藏（Locket）"), Meaning("C", "传承（Chronicle）")
        ),
        desc = "你收藏的每一件旧物，背后都藏着一段无人记得的往事。你不急不躁，因为你比谁都清楚——时间会替有价值的东西说话。旁人把你看作守旧的收藏家，却不明白你守护的从来不是物件，而是记忆本身。你就是橡木匣。"
    ),
    GameChar(
        id = "deleiya", name = "德雷雅", code = "STLK", avatarColor = "#5a1f2a",
        stats = CHR_STATS_V15["deleiya"]!!,
        meanings = listOf(
            Meaning("S", "潜行（Stalk）"), Meaning("T", "撕裂（Tear）"),
            Meaning("L", "野性（Lupine）"), Meaning("K", "猎杀（Kill）")
        ),
        desc = "你听不懂大道理，也不想听。世界对你而言只有两种东西：猎物，以及挡路的猎物。最纯粹的暴力里没有犹豫，也没有怜悯——你追逐的从来不是杀意，而是本能得到满足那一刻的安静。你就是德雷雅。"
    ),
    GameChar(
        id = "LL", name = "L.L.", code = "DOSE", avatarColor = "#7a3a5a",
        stats = CHR_STATS_V15["LL"]!!,
        meanings = listOf(
            Meaning("D", "剂量（Dose）"), Meaning("O", "痴迷（Obsession）"),
            Meaning("S", "手术（Surgery）"), Meaning("E", "狂喜（Ecstasy）")
        ),
        desc = "你把伤害当作治疗，把疼痛当作良药。你微笑着推着治疗车靠近，哼着歌——谁也分不清你是天使还是噩梦。你从不觉得自己残忍，因为在你眼里，所有人都病得不轻，而你只是想让大家都好起来。你就是 L.L.。"
    ),
    GameChar(
        id = "duruo", name = "杜若", code = "SAGE", avatarColor = "#3a5a6a",
        stats = CHR_STATS_V15["duruo"]!!,
        meanings = listOf(
            Meaning("S", "睿智（Sage）"), Meaning("A", "审慎（Austere）"),
            Meaning("G", "缜密（Grind）"), Meaning("E", "案卷（Evidence）")
        ),
        desc = "你习惯站在故事的外侧，安静地看，仔细地记。你不声张，是因为你知道真正的力量从不靠音量证明。复杂的线索在你手里会变成清晰的表格，混沌的头绪在你笔下会归成整齐的条目。温文中自有锋芒。你就是杜若。"
    ),
    GameChar(
        id = "aowu", name = "嗷呜", code = "HOWL", avatarColor = "#6a4a2a",
        stats = CHR_STATS_V15["aowu"]!!,
        meanings = listOf(
            Meaning("H", "嚎叫（Howl）"), Meaning("O", "野放（Outdoors）"),
            Meaning("W", "亲密（Warmth）"), Meaning("L", "忠诚（Loyal）")
        ),
        desc = "你不习惯穿鞋，也不喜欢别人对你指手画脚。开心就撒欢，不爽就呲牙——你的情绪从不需要翻译。有人说你不通世故，可你比谁都清楚谁是真心对你好。野性不代表野蛮，而是最坦率的温柔。你就是嗷呜。"
    ),
    GameChar(
        id = "yao", name = "曜", code = "SOLE", avatarColor = "#8a5a1a",
        stats = CHR_STATS_V15["yao"]!!,
        meanings = listOf(
            Meaning("S", "骄阳（Sol）"), Meaning("O", "孤高（Only）"),
            Meaning("L", "迅烈（Lightning）"), Meaning("E", "极致（Edge）")
        ),
        desc = "你把身体练成一件兵器，把每一次出拳都当作和昨天的自己决斗。你话不多，因为你相信行动是唯一诚实的语言。烈日灼心，也灼志——越是刺眼的光，越让你兴奋。所有汗水滴落的地方，都是你的道场。你就是曜。"
    ),
    GameChar(
        id = "leiwen", name = "雷温", code = "PRES", avatarColor = "#7a2a5a",
        stats = CHR_STATS_V15["leiwen"]!!,
        meanings = listOf(
            Meaning("P", "公关（Press）"), Meaning("R", "声望（Repute）"),
            Meaning("E", "交际（Engage）"), Meaning("S", "手腕（Stratagem）")
        ),
        desc = "你深谙话术的价值：一句恰到好处的问候，能省下十份冷冰冰的公文。你在名利场里游刃有余，笑容是职业装的一部分。别人以为你在觥筹交错间享乐，只有你知道，每一场应酬都是一次精密的谈判。言语可以杀人，也可以救人。你就是雷温。"
    ),
    GameChar(
        id = "bianka", name = "毕安卡", code = "CLUE", avatarColor = "#3a6a7a",
        stats = CHR_STATS_V15["bianka"]!!,
        meanings = listOf(
            Meaning("C", "推理（Conclude）"), Meaning("L", "线索（Lead）"),
            Meaning("U", "敏锐（Untangle）"), Meaning("E", "探求（Explore）")
        ),
        desc = "你痴迷于「为什么」。每一条不起眼的线索都让你兴奋，每一个说不通的细节都在召唤你。你像追逐蝴蝶的孩子一样追逐真相，全然不顾沿途的尖锐与泥泞。有人说好奇心会害死猫——可你是那个总能揪住答案尾巴的人。你就是毕安卡。"
    ),
    GameChar(
        id = "yifu", name = "伊芙", code = "STAT", avatarColor = "#6a6a8a",
        stats = CHR_STATS_V15["yifu"]!!,
        meanings = listOf(
            Meaning("S", "雕塑（Statue）"), Meaning("T", "静默（Tranquil）"),
            Meaning("A", "美感（Art）"), Meaning("E", "寓言（Epilogue）")
        ),
        desc = "你安静得像一座还没完成的雕像，把所有的喧闹留在自己之外。你不争辩，也不解释，只是安静地看着这个世界——然后在无人注意的地方，把它雕刻成你喜欢的样子。最柔软的手，往往握着最坚硬的凿子。你就是伊芙。"
    ),
    GameChar(
        id = "enfeier", name = "恩菲尔", code = "FILM", avatarColor = "#7a4a6a",
        stats = CHR_STATS_V15["enfeier"]!!,
        meanings = listOf(
            Meaning("F", "胶片（Film）"), Meaning("I", "执镜（Imagine）"),
            Meaning("L", "灯光（Light）"), Meaning("M", "调度（Montage）")
        ),
        desc = "在你的镜头里，世界必须按你的剧本运转。你偏执地追求每一个画面的完美，哪怕要重来一百次。你不在意别人说你难伺候——毕竟平庸才是你唯一无法容忍的罪名。所有人都想成为主角，而你是决定谁是主角的人。你就是恩菲尔。"
    ),
    GameChar(
        id = "adailai", name = "阿黛拉", code = "JEST", avatarColor = "#8a5a6a",
        stats = CHR_STATS_V15["adailai"]!!,
        meanings = listOf(
            Meaning("J", "滑稽（Jester）"), Meaning("E", "欢腾（Exult）"),
            Meaning("S", "抛接（Somersault）"), Meaning("T", "惊险（Thrill）")
        ),
        desc = "你把每一次惊险的抛接都当作和重力开的玩笑。别人尖叫着闭眼，你却在半空中笑出了声。悲伤被你折成纸飞机，笑声是你抛向观众的彩带。高空之下没有安全网——所以你学会了自己接住自己。你就是阿黛拉。"
    ),
    GameChar(
        id = "naqia", name = "娜恰", code = "SERP", avatarColor = "#5a2a4a",
        stats = CHR_STATS_V15["naqia"]!!,
        meanings = listOf(
            Meaning("S", "蛇行（Serpentine）"), Meaning("E", "魅眼（Enchant）"),
            Meaning("R", "缠绕（Rope）"), Meaning("P", "毒信（Poison）")
        ),
        desc = "你的舞步像一条丝绸，滑过所有人的目光。你说话时总是微微歪着头，让人分不清那是天真还是算计。你从不解释自己是好人还是坏人——蛇从来不需要向猎物解释，它只需要让对方盯着自己的眼睛。你就是娜恰。"
    ),
    GameChar(
        id = "nuokesi", name = "诺克斯", code = "NOCT", avatarColor = "#2a2a4a",
        stats = CHR_STATS_V15["nuokesi"]!!,
        meanings = listOf(
            Meaning("N", "夜行（Nocturne）"), Meaning("O", "守望（Oversee）"),
            Meaning("C", "肃穆（Calm）"), Meaning("T", "终焉（Terminal）")
        ),
        desc = "你走在夜色里，安静得像一段无人诵读的祷文。你见过太多告别，因此比任何人都懂得温柔的重量。你不畏惧黑暗——因为你就是那个在黑暗里，替别人守最后一盏灯的人。无论多么漫长的夜，你都会等到黎明。你就是诺克斯。"
    ),
    GameChar(
        id = "dudu", name = "嘟嘟", code = "DOLL", avatarColor = "#8a5a7a",
        stats = CHR_STATS_V15["dudu"]!!,
        meanings = listOf(
            Meaning("D", "人偶（Doll）"), Meaning("O", "装乖（Obedient）"),
            Meaning("L", "丝线（Ligature）"), Meaning("L", "表演（Lilt）")
        ),
        desc = "你像一只精致的提线木偶，笑得乖巧，动作轻柔。可别忘了——线在谁手里，从来不是你能决定的事，除非，你悄悄学会了剪断它。你享受猎物放下戒备的那一刻，因为那意味着，这场戏由你说了算。你就是嘟嘟。"
    ),
    GameChar(
        id = "yigeni", name = "伊格尼", code = "IGNI", avatarColor = "#8a3a1a",
        stats = CHR_STATS_V15["yigeni"]!!,
        meanings = listOf(
            Meaning("I", "引燃（Ignite）"), Meaning("G", "灰烬（Glow）"),
            Meaning("N", "涅槃（Nirvana）"), Meaning("I", "冲动（Impulse）")
        ),
        desc = "你迷恋火焰舔舐一切时的声音。别人绕着火光走，你却忍不住伸手去碰——疼也无所谓，那至少证明你还活着。你把不该烧的都烧了，把不该留的都留下。灰烬之上，就是你重新开始的地方。你就是伊格尼。"
    ),
    GameChar(
        id = "kamiliyan", name = "卡米利安", code = "CARE", avatarColor = "#4a7a6a",
        stats = CHR_STATS_V15["kamiliyan"]!!,
        meanings = listOf(
            Meaning("C", "关怀（Care）"), Meaning("A", "倾听（Attend）"),
            Meaning("R", "仁心（Remedy）"), Meaning("E", "恒常（Endure）")
        ),
        desc = "你相信伤口是可以被治愈的——无论那伤口在皮肤上，还是在心里的某个角落。你习惯先听懂别人没说出口的话，再递出那只手。世界对你来说没有真正的坏人，只有没被好好爱过的病人。温柔而有力量。你就是卡米利安。"
    ),
    GameChar(
        id = "aien", name = "艾恩", code = "ATOM", avatarColor = "#4a5a7a",
        stats = CHR_STATS_V15["aien"]!!,
        meanings = listOf(
            Meaning("A", "钻研（Atom）"), Meaning("T", "定理（Theorem）"),
            Meaning("O", "秩序（Order）"), Meaning("M", "机巧（Mechanism）")
        ),
        desc = "你对原理的痴迷近乎偏执——不弄清楚齿轮怎么咬合，你就睡不着觉。你把混乱拆成零件，再把零件装成答案。别人惊叹于你的效率，其实你只是拒绝了所有含糊。世界在你眼里是一张精密的图纸。你就是艾恩。"
    ),
    GameChar(
        id = "puxila", name = "普希拉", code = "FANS", avatarColor = "#8a4a6a",
        stats = CHR_STATS_V15["puxila"]!!,
        meanings = listOf(
            Meaning("F", "狂粉（Fanatic）"), Meaning("A", "痴心（Adore）"),
            Meaning("N", "追踪（Nab）"), Meaning("S", "私藏（Secrete）")
        ),
        desc = "你在人群里永远站在第一排，举着最亮的灯牌，喊出最响的声音。喜欢对你来说从来不是浅浅的情绪，而是一整个世界的重量。你收集关于 TA 的一切，把每一次见面都当成节日。有人说你疯，你只觉得他们不懂。你就是普希拉。"
    ),
    GameChar(
        id = "taitela", name = "泰特拉", code = "MAGI", avatarColor = "#5a3a7a",
        stats = CHR_STATS_V15["taitela"]!!,
        meanings = listOf(
            Meaning("M", "魔术（Magic）"), Meaning("A", "障眼（Amaze）"),
            Meaning("G", "灵巧（Gesture）"), Meaning("I", "戏法（Illusion）")
        ),
        desc = "你手上的牌永远洗得比别人的眼睛快。你把玩着硬币、纸牌和所有人的注意力——让他们看左边，再从右边拿走想要的东西。生活对你来说是一场巨大的魔术秀，而你喜欢在谢幕时微笑：看，其实一直没人真正看清过我。你就是泰特拉。"
    ),
    GameChar(
        id = "langhuan", name = "狼獾", code = "FANG", avatarColor = "#5a4a2a",
        stats = CHR_STATS_V15["langhuan"]!!,
        meanings = listOf(
            Meaning("F", "獠牙（Fang）"), Meaning("A", "伏击（Ambush）"),
            Meaning("N", "嗅觉（Nose）"), Meaning("G", "凶悍（Grind）")
        ),
        desc = "你的嗅觉比理智更早醒来。你不识字，也懒得懂道理，但你认得出血的味道、陷阱的走向和猎物心跳加速的声音。你在荒野里活下来的每一课都刻进骨头：快、狠、准，然后活下去。丛林从未对你温柔，你也不打算对谁温柔。你就是狼獾。"
    ),
    GameChar(
        id = "fukesixiansheng", name = "福克斯先生", code = "CUNN", avatarColor = "#7a5a2a",
        stats = CHR_STATS_V15["fukesixiansheng"]!!,
        meanings = listOf(
            Meaning("C", "狡黠（Cunning）"), Meaning("U", "绅士（Urbane）"),
            Meaning("N", "周旋（Negotiate）"), Meaning("N", "藏拙（Nonchalant）")
        ),
        desc = "你在帽檐的阴影里打量世界，用最得体的礼节说最滑不留手的话。有人以为逮住了你的尾巴，回头才发现被牵走的是他口袋里的东西。你从不贪心，也从不吃亏——这中间的尺度，就是你的道。你就是福克斯先生。"
    ),
    GameChar(
        id = "wendi", name = "温蒂", code = "RIOT", avatarColor = "#7a2a2a",
        stats = CHR_STATS_V15["wendi"]!!,
        meanings = listOf(
            Meaning("R", "暴走（Riot）"), Meaning("I", "冲动（Impulse）"),
            Meaning("O", "独行（Outcast）"), Meaning("T", "锋利（Trenchant）")
        ),
        desc = "你的耐心很小，你的刀很快。有人多看你一眼，你能瞪回去；有人多说一句话，你的手已经摸到了刀柄。你不在乎什么大道理——敢惹你，就是敌人；对你好，就是朋友。简单，粗暴，但是真实。你就是温蒂。"
    ),
    GameChar(
        id = "jiushijiu", name = "九十九", code = "GLAD", avatarColor = "#6a3a2a",
        stats = CHR_STATS_V15["jiushijiu"]!!,
        meanings = listOf(
            Meaning("G", "角斗（Gladiator）"), Meaning("L", "蛮勇（Lunge）"),
            Meaning("A", "逞强（Assert）"), Meaning("D", "硬撼（Duel）")
        ),
        desc = "你习惯用最直接的方式解决所有问题：撞过去。撞不动，就再来一次。别人动脑子的时候你在热身，别人讲道理的时候你在蓄力。你信的东西很少：拳头、呼吸、还有下一次站起来。不聪明，但从不认输。你就是九十九。"
    ),
    GameChar(
        id = "weiduoliya", name = "维多利亚", code = "NOBL", avatarColor = "#6a5a7a",
        stats = CHR_STATS_V15["weiduoliya"]!!,
        meanings = listOf(
            Meaning("N", "门第（Noble）"), Meaning("O", "端庄（Ornate）"),
            Meaning("B", "柔弱（Brittle）"), Meaning("L", "骄傲（Ladyship）")
        ),
        desc = "你出生在钟鸣鼎食的家庭，却被困在一副连风都能吹倒的躯壳里。你学会用规矩当铠甲，用仪态当地图——因为除此之外，你没有别的武器。所有人都以为你只是需要被保护的瓷娃娃，没人注意到你眼里那簇从不熄灭的火。你就是维多利亚。"
    ),
    GameChar(
        id = "jin", name = "堇", code = "SILK", avatarColor = "#6a4a7a",
        stats = CHR_STATS_V15["jin"]!!,
        meanings = listOf(
            Meaning("S", "轻丝（Silk）"), Meaning("I", "隐锋（Insinuate）"),
            Meaning("L", "花姿（Lithe）"), Meaning("K", "一击（Knock）")
        ),
        desc = "你像一株安静生长的花——直到开花的那一刻，所有人才发现花瓣下面藏着的是刃。你说话轻柔，脚步更轻，连呼吸都练得悄无声息。你不喜欢正面交锋，因为最漂亮的一击，总是从背后、在最松懈的瞬间到达。你就是堇。"
    ),
    GameChar(
        id = "qieerxibojue", name = "切尔西伯爵", code = "LORD", avatarColor = "#5a5a3a",
        stats = CHR_STATS_V15["qieerxibojue"]!!,
        meanings = listOf(
            Meaning("L", "爵位（Lord）"), Meaning("O", "做派（Ostentatious）"),
            Meaning("R", "周旋（Round）"), Meaning("D", "底气（Dignity）")
        ),
        desc = "你的一举一动都像从油画里走出来的绅士——领结的弧度、说话的停顿、举杯的高度，全部精准到毫米。没有人怀疑你的爵位，就像没有人拒绝你的邀请。真贵族未必有你像贵族，而这正好够用。你就是切尔西伯爵。"
    ),
    GameChar(
        id = "lamiya", name = "拉弥亚", code = "LAMI", avatarColor = "#6a2a4a",
        stats = CHR_STATS_V15["lamiya"]!!,
        meanings = listOf(
            Meaning("L", "魅影（Lamia）"), Meaning("A", "蛊惑（Allure）"),
            Meaning("M", "缠绵（Mingle）"), Meaning("I", "本能（Instinct）")
        ),
        desc = "你的声音有催眠的质感，靠近你的人都说像掉进温水里——舒服得忘了挣扎。你不急着捕猎，因为你知道耐心是最好的毒。被你盯上的人从来跑不掉，道理很简单：没有人抗拒得了温柔本身。你就是拉弥亚。"
    ),
    GameChar(
        id = "heluo", name = "赫罗", code = "RAID", avatarColor = "#8a4a2a",
        stats = CHR_STATS_V15["heluo"]!!,
        meanings = listOf(
            Meaning("R", "冲阵（Raid）"), Meaning("A", "悍勇（Audacity）"),
            Meaning("I", "血性（Instinct）"), Meaning("D", "狂战（Derring）")
        ),
        desc = "你天生是战场上的那把锤子——哪里坚固，就砸哪里。你享受对手脸上写着「这家伙不会停」的表情。伤痕在你身上不是勋章，是作息表。有人劝你爱惜身体，你咧嘴一笑：身体不就是拿来用的吗。你就是赫罗。"
    ),
    GameChar(
        id = "aolifu", name = "奥利弗", code = "TWIS", avatarColor = "#3a4a4a",
        stats = CHR_STATS_V15["aolifu"]!!,
        meanings = listOf(
            Meaning("T", "诡计（Twist）"), Meaning("W", "机变（Wily）"),
            Meaning("I", "窥探（Inspect）"), Meaning("S", "阴影（Shadow）")
        ),
        desc = "你太清楚自己的弱小，所以早早学会了借力打力。你躲在角落里观察每一个人，把他们的弱点记在心里的小本子上。你从不正面硬碰硬——毕竟让敌人互相撕咬，远比亲自出手划算。小小一只，却让人背后发凉。你就是奥利弗。"
    ),
    GameChar(
        id = "kelisidina", name = "克里斯蒂娜", code = "OATH", avatarColor = "#3a5a4a",
        stats = CHR_STATS_V15["kelisidina"]!!,
        meanings = listOf(
            Meaning("O", "誓言（Oath）"), Meaning("A", "守序（Aegis）"),
            Meaning("T", "枪线（Target）"), Meaning("H", "坚毅（Hold）")
        ),
        desc = "你的腰带永远系得比谁都紧，枪擦得比谁都亮。你把承诺看得比生命重，把纪律看得比情绪重。别人偶尔偷懒的时候，你的字典里没有这个词——不是不会，而是不想。你站在那里，本身就是一句让人安心的宣言。你就是克里斯蒂娜。"
    ),
    GameChar(
        id = "airuier", name = "艾瑞尔", code = "HYMN", avatarColor = "#6a7a5a",
        stats = CHR_STATS_V15["airuier"]!!,
        meanings = listOf(
            Meaning("H", "圣歌（Hymn）"), Meaning("Y", "慈悲（Yearn）"),
            Meaning("M", "抚慰（Mercy）"), Meaning("N", "静祷（Novena）")
        ),
        desc = "你相信善意是一件可以传递的事，所以你把微笑分给过路的每个人。你不擅长争斗，也没有锋利的野心——但你身上有一种让人卸下防备的安宁。有人说那是软弱，可真正被生活磨过的人知道：温柔地活着，比凶狠地活着更需要力气。你就是艾瑞尔。"
    ),
    GameChar(
        id = "youni", name = "尤尼", code = "DATA", avatarColor = "#2a4a6a",
        stats = CHR_STATS_V15["youni"]!!,
        meanings = listOf(
            Meaning("D", "数据（Data）"), Meaning("A", "分析（Analyze）"),
            Meaning("T", "终端（Terminal）"), Meaning("A", "读取（Access）")
        ),
        desc = "世界在你眼里是一串串流动的数据。你在屏幕前安静地敲着键盘，却比谁都更接近事件的中心。你不太会待人接物，但机器从不辜负你——输入足够精确，输出就一定诚实。人的世界很吵，你的世界很干净。你就是尤尼。"
    ),
    GameChar(
        id = "shuluma", name = "舒鲁玛", code = "SOFT", avatarColor = "#5a5a5a",
        stats = CHR_STATS_V15["shuluma"]!!,
        meanings = listOf(
            Meaning("S", "温吞（Soft）"), Meaning("O", "随和（Oblige）"),
            Meaning("F", "静谧（Feather）"), Meaning("T", "平凡（Tame）")
        ),
        desc = "你没什么惊天动地的特长，也不打算有。你像城市里最普通的一盏路灯——不耀眼，但总在需要的时候亮着。你把日子过得慢吞吞，却把朋友照顾得妥帖。喧嚣的狄斯城里，能守住一份普通，本身就是了不起的技能。你就是舒鲁玛。"
    ),
    GameChar(
        id = "pajiaqian", name = "帕加茜", code = "SHLD", avatarColor = "#4a4a3a",
        stats = CHR_STATS_V15["pajiaqian"]!!,
        meanings = listOf(
            Meaning("S", "壁垒（Shield）"), Meaning("H", "守护（Harbor）"),
            Meaning("L", "韧性（Last）"), Meaning("D", "抵挡（Deflect）")
        ),
        desc = "你站在哪，哪就是防线。你不需要人们记住你的名字，只需要他们安全地待在身后。你话少，但每一句都算数。危险来袭的时候，所有人的第一反应都是回头找你——而你，从来没有让谁失望过。你就是帕加茜。"
    ),
    GameChar(
        id = "chensha", name = "辰砂", code = "CORE", avatarColor = "#8a4a3a",
        stats = CHR_STATS_V15["chensha"]!!,
        meanings = listOf(
            Meaning("C", "中正（Core）"), Meaning("O", "沉稳（Orderly）"),
            Meaning("R", "可靠（Reliant）"), Meaning("E", "基准（Equipoise）")
        ),
        desc = "你是那种让人一见就觉得「靠谱」的人。计划永远有备份，承诺永远有余量，情绪永远在刻度之内。你不追求惊心动魄，因为你知道真正难的是天长日久地把每件小事做好。千锤百炼，方成辰砂。你就是辰砂。"
    ),
    GameChar(
        id = "yanyan", name = "烟烟", code = "HAZE", avatarColor = "#5a5a4a",
        stats = CHR_STATS_V15["yanyan"]!!,
        meanings = listOf(
            Meaning("H", "烟行（Haze）"), Meaning("A", "机敏（Agile）"),
            Meaning("Z", "狡捷（Zigzag）"), Meaning("E", "散漫（Ease）")
        ),
        desc = "你在城市的褶皱里长大，比谁都熟悉哪条巷子能抄近路、哪扇窗户背后住着什么人。你滑不溜手，谁也抓不住——但真到朋友有难的时候，你永远是第一个出现的身影。烟雾一样的人，火一样的心。你就是烟烟。"
    ),
    GameChar(
        id = "duoli", name = "多莉", code = "MARI", avatarColor = "#7a5a6a",
        stats = CHR_STATS_V15["duoli"]!!,
        meanings = listOf(
            Meaning("M", "提线（Marionette）"), Meaning("A", "生动（Animate）"),
            Meaning("R", "巧手（Render）"), Meaning("I", "戏偶（Idol）")
        ),
        desc = "你的两手能赋予木头以呼吸，让沉默的玩偶开口说话。你享受把小东西们排成一排、为它们安排人生的时刻——毕竟现实太难改写，但舞台可以。别分不清戏里戏外哦，悄悄说：有时候，你自己也分不清。你就是多莉。"
    ),
    GameChar(
        id = "luweiya", name = "露薇娅·蕾", code = "LARK", avatarColor = "#6a5a6a",
        stats = CHR_STATS_V15["luweiya"]!!,
        meanings = listOf(
            Meaning("L", "轻盈（Lark）"), Meaning("A", "亲和（Affable）"),
            Meaning("R", "韧劲（Resilience）"), Meaning("K", "细语（Kindle）")
        ),
        desc = "你眼睛里有种安静的亮，像清晨窗户上落下的第一缕光。你习惯把位置让给别人，把话说得轻一点、再轻一点。可当身边的人真的陷入麻烦时，你会发现这个平时温吞的人，骨头比谁都硬。温柔从来不是你的弱点。你就是露薇娅·蕾。"
    ),
    GameChar(
        id = "kasiluo", name = "卡斯洛", code = "DRAW", avatarColor = "#6a5a1a",
        stats = CHR_STATS_V15["kasiluo"]!!,
        meanings = listOf(
            Meaning("D", "拔枪（Draw）"), Meaning("R", "浪客（Rover）"),
            Meaning("A", "准头（Aim）"), Meaning("W", "机变（Wit）")
        ),
        desc = "你走路带风，说话带笑，从不解释自己从哪来、要到哪去。腰间的家伙只有在必要时才会响——但一旦响，就必定终结谈话。你信奉最古老的规矩：快者生存。至于其他的，交给风去说。你就是卡斯洛。"
    ),
    GameChar(
        id = "kawakawa", name = "卡瓦卡瓦", code = "KEEP", avatarColor = "#3a4a2a",
        stats = CHR_STATS_V15["kawakawa"]!!,
        meanings = listOf(
            Meaning("K", "守护（Keep）"), Meaning("A", "憨厚（Amity）"),
            Meaning("W", "坚壁（Wall）"), Meaning("A", "暖意（Agape）")
        ),
        desc = "你总是慢半拍才笑，慢半拍才开口，却从来没有慢半拍地赶到需要你的人身边。你的大手能修好漏水的屋顶，也能轻轻捏起一块饼干。别被你的体格骗了——温柔和力量，在你身上从来不冲突。你就是卡瓦卡瓦。"
    ),
    GameChar(
        id = "meisi", name = "莓丝", code = "TART", avatarColor = "#7a4a4a",
        stats = CHR_STATS_V15["meisi"]!!,
        meanings = listOf(
            Meaning("T", "巧味（Tart）"), Meaning("A", "细腻（Aroma）"),
            Meaning("R", "勤恳（Rise）"), Meaning("T", "火候（Timing）")
        ),
        desc = "你对「恰到好处」有近乎苛刻的执念——多一分太甜，少一分则寡。你把对生活全部的理解，都揉进了手里的每一份作品。别人以为你在做甜点，其实你在做精确的爱的表达。能让人吃了笑起来的东西，从来都不简单。你就是莓丝。"
    ),
    GameChar(
        id = "liwei", name = "李维", code = "LEVY", avatarColor = "#5a4a4a",
        stats = CHR_STATS_V15["liwei"]!!,
        meanings = listOf(
            Meaning("L", "谦和（Lenient）"), Meaning("E", "圆融（Equable）"),
            Meaning("V", "慧眼（Vision）"), Meaning("Y", "情义（Yoke）")
        ),
        desc = "你把生意做得像交朋友，把朋友交得像做学问。你信奉和气生财，也信奉有借有还——所以你开口的时候，大家都愿意坐下听完。看人下菜碟的事你不做，因为你早就看明白：人心这东西，收得越急，丢得越快。你就是李维。"
    ),
    GameChar(
        id = "an", name = "安", code = "SAFE", avatarColor = "#5a5a6a",
        stats = CHR_STATS_V15["an"]!!,
        meanings = listOf(
            Meaning("S", "恬静（Serene）"), Meaning("A", "倾听（Attend）"),
            Meaning("F", "分寸（Fence）"), Meaning("E", "日常（Everyday）")
        ),
        desc = "你像一杯温度刚好的水：不出声，不起眼，却在每个疲惫的时刻被需要。你记得每个人的口味，也记得谁今天心情不好。你不争抢聚光灯，因为你知道，让一个房间变得温暖的东西，从来不是灯光。有事就来找你——大家都这么说。你就是安。"
    ),
    GameChar(
        id = "maikun", name = "麦昆", code = "MACK", avatarColor = "#6a3a6a",
        stats = CHR_STATS_V15["maikun"]!!,
        meanings = listOf(
            Meaning("M", "风云（Mack）"), Meaning("A", "应变（Adapt）"),
            Meaning("C", "时髦（Chic）"), Meaning("K", "手腕（Knack）")
        ),
        desc = "你天生是人群的节拍器：走到哪，哪里的聊天就有了节拍。你懂得用玩笑化解尴尬，用自嘲卸下戒备，等你把场子热好了，正经事也就谈成了。有人把这叫做圆滑，你笑而不语——能把人和人连起来，本身就是一种本事。你就是麦昆。"
    ),
    GameChar(
        id = "youlika", name = "优利卡", code = "SWIF", avatarColor = "#3a6a4a",
        stats = CHR_STATS_V15["youlika"]!!,
        meanings = listOf(
            Meaning("S", "迅捷（Swift）"), Meaning("W", "干练（Work）"),
            Meaning("I", "直进（Initiative）"), Meaning("F", "果断（Firm）")
        ),
        desc = "你讨厌拖泥带水。事情来了就做，做就做到位——这是你全部的处事哲学。你的动作干净利落，说话也从不绕弯：行就是行，不行就改到行为止。别人还在权衡利弊的时候，你已经把结果摆在了桌上。你就是优利卡。"
    ),
    GameChar(
        id = "laita", name = "莱塔", code = "SKET", avatarColor = "#5a4a5a",
        stats = CHR_STATS_V15["laita"]!!,
        meanings = listOf(
            Meaning("S", "素描（Sketch）"), Meaning("K", "灵感（Kaleido）"),
            Meaning("E", "色彩（Etch）"), Meaning("T", "乡愁（Tender）")
        ),
        desc = "你的口袋永远装着半截铅笔，随时准备把打动你的画面留下来。你力气小、胆子也小，唯独在画画这件事上固执得不像话。有人说艺术不能当饭吃，你点点头，然后继续画——毕竟世界已经够粗糙了，总得有人负责把它描得温柔一点。你就是莱塔。"
    ),
    GameChar(
        id = "ruisi", name = "瑞思", code = "RACE", avatarColor = "#6a4a4a",
        stats = CHR_STATS_V15["ruisi"]!!,
        meanings = listOf(
            Meaning("R", "跑道（Race）"), Meaning("A", "争先（Against）"),
            Meaning("C", "节律（Cadence）"), Meaning("E", "终点（Endeavor）")
        ),
        desc = "你的对手从来不是别人，是秒表上的那个自己。别人练到累就停了，你会再加一组——因为你知道，想赢的人很多，愿意多流汗的人很少。你不擅长煽情，也不需要掌声：发令枪响的那一刻，世界就只剩下跑道和呼吸。你就是瑞思。"
    ),
    GameChar(
        id = "jialuofanuo", name = "加洛法诺", code = "GUAR", avatarColor = "#7a5a5a",
        stats = CHR_STATS_V15["jialuofanuo"]!!,
        meanings = listOf(
            Meaning("G", "守护（Guard）"), Meaning("U", "温厚（Upright）"),
            Meaning("A", "怜惜（Amity）"), Meaning("R", "担当（Responsible）")
        ),
        desc = "你看起来温和得像一束花，却总在危险靠近时第一个站到别人身前。你相信守护不需要声张——把伞递给淋雨的人，把外套给冷的人，就这么简单。有人把你的好当作理所当然，你也不计较，只是继续做。世界欠温柔的人一个拥抱。你就是加洛法诺。"
    ),
    GameChar(
        id = "aike", name = "艾可", code = "ECHO", avatarColor = "#8a6a2a",
        stats = CHR_STATS_V15["aike"]!!,
        meanings = listOf(
            Meaning("E", "回声（Echo）"), Meaning("C", "顽皮（Childish）"),
            Meaning("H", "热情（Hooray）"), Meaning("O", "机灵（Opportunist）")
        ),
        desc = "你像回音一样无处不在：这个屋顶有你，那条巷子也有你。你喊得出的外号比谁都多，闯的祸也不少，但你就是有本事让生气的人也忍不住笑出来。别小看你——跑得最快的信使、最灵通的消息，都在你的口袋里。你就是艾可。"
    ),
    GameChar(
        id = "kaxiya", name = "卡西娅", code = "CALM", avatarColor = "#3a3a4a",
        stats = CHR_STATS_V15["kaxiya"]!!,
        meanings = listOf(
            Meaning("C", "冷静（Calm）"), Meaning("A", "自持（Anchor）"),
            Meaning("L", "低调（Low-key）"), Meaning("M", "淡漠（Mild）")
        ),
        desc = "你总是一副波澜不惊的样子：天塌下来，你也只是抬眼看看，然后继续手上在做的事。你不太表达感情，也不轻易相信谁——但被你纳入「自己人」名单的人，会得到你最沉默也最可靠的照应。不喧哗，自有声。你就是卡西娅。"
    ),
    GameChar(
        id = "jian", name = "简", code = "JOUR", avatarColor = "#6a4a3a",
        stats = CHR_STATS_V15["jian"]!!,
        meanings = listOf(
            Meaning("J", "职人（Journey）"), Meaning("O", "利落（On the go）"),
            Meaning("U", "万能（Utility）"), Meaning("R", "可靠（Reliable）")
        ),
        desc = "哪里缺人手，哪里就有你。你什么活都接，什么苦都吃，把日子过得风风火火。有人说你没有原则，其实你的原则简单得很：靠自己双手吃饭，不丢人。风里雨里，你永远是那个按时把事办妥的人。你就是简。"
    ),
    GameChar(
        id = "ji", name = "蓟", code = "THRN", avatarColor = "#4a5a3a",
        stats = CHR_STATS_V15["ji"]!!,
        meanings = listOf(
            Meaning("T", "芒刺（Thorn）"), Meaning("H", "坚韧（Hardy）"),
            Meaning("R", "沉默（Restrain）"), Meaning("N", "生长（Nurture）")
        ),
        desc = "你话不多，笑起来却让人安心。你像野地里最不起眼的植物——踩上去会扎手，但活得比谁都顽强。你不挑土壤，不问风向，认定了要去的地方，就一步一步走过去。安静，但是扎扎实实。你就是蓟。"
    ),
    GameChar(
        id = "luliaika", name = "露莉艾卡", code = "PROM", avatarColor = "#7a3a3a",
        stats = CHR_STATS_V15["luliaika"]!!,
        meanings = listOf(
            Meaning("P", "诺言（Promise）"), Meaning("R", "执念（Resolve）"),
            Meaning("O", "倔强（Obstinate）"), Meaning("M", "守望（Monitor）")
        ),
        desc = "你说过的话，就算全世界都忘了，你也会记得。你从小、力气小、什么都很小，唯独骨气比谁都大。认准的事，九头牛都拉不回——哪怕摔得鼻青脸肿，你也会爬起来拍掉灰继续走。大人管这叫执拗，你管这叫说到做到。你就是露莉艾卡。"
    ),
    GameChar(
        id = "lin", name = "琳", code = "WHIM", avatarColor = "#8a6a5a",
        stats = CHR_STATS_V15["lin"]!!,
        meanings = listOf(
            Meaning("W", "随性（Whim）"), Meaning("H", "轻巧（Hover）"),
            Meaning("I", "机灵（Imp）"), Meaning("M", "贪玩（Mischief）")
        ),
        desc = "你的人生信条只有两个字：开心。今天想去哪就去哪，想不理谁就不理谁，谁也拦不住一只铁了心要晒太阳的猫。有人嫌你没正形——你眨眨眼，觉得他们太累。世界是座巨大的游乐场，而你，永远知道最好的滑梯在哪。你就是琳。"
    ),
    GameChar(
        id = "ninuo", name = "妮诺", code = "NICE", avatarColor = "#6a6a5a",
        stats = CHR_STATS_V15["ninuo"]!!,
        meanings = listOf(
            Meaning("N", "亲切（Nice）"), Meaning("I", "随和（Indulgent）"),
            Meaning("C", "体贴（Consider）"), Meaning("E", "陪伴（Escort）")
        ),
        desc = "你的字典里没有「拒绝」两个字。有人求到你面前，哪怕是为难的事，你也会先点头再想办法。你因此吃了不少亏，却从没想过改。因为你知道，被人需要的感觉，比什么都珍贵。愿世界善待每一个不会拒绝的人。你就是妮诺。"
    ),
    GameChar(
        id = "madierda", name = "玛蒂尔达", code = "TIDY", avatarColor = "#5a6a6a",
        stats = CHR_STATS_V15["madierda"]!!,
        meanings = listOf(
            Meaning("T", "整理（Tidy）"), Meaning("I", "教习（Instruct）"),
            Meaning("D", "档案（Dossier）"), Meaning("Y", "严谨（Yardstick）")
        ),
        desc = "你的人生是分门别类的：书按高度排，笔按颜色放，连计划都要做成三份备份。你相信秩序能治愈一切焦虑，所以你把身边的世界整理得井井有条。别在背后笑你较真——在乱成一团的日子里，谁不想身边有一个你。你就是玛蒂尔达。"
    ),
    GameChar(
        id = "xiaoen", name = "肖恩", code = "STED", avatarColor = "#4a5a5a",
        stats = CHR_STATS_V15["xiaoen"]!!,
        meanings = listOf(
            Meaning("S", "沉稳（Steady）"), Meaning("T", "守成（Tenable）"),
            Meaning("E", "踏实（Earnest）"), Meaning("D", "本分（Duty）")
        ),
        desc = "你不是最亮眼的那个，但永远是最后一个离开岗位的人。分给你的活，你从不挑肥拣瘦——大到扛货，小到守夜，交到你这儿，就等于画了句号。你说自己没什么本事，可让所有人放心的本事，本身就很稀有。你就是肖恩。"
    ),
    GameChar(
        id = "gelan", name = "戈蓝", code = "GRIT", avatarColor = "#5a3a3a",
        stats = CHR_STATS_V15["gelan"]!!,
        meanings = listOf(
            Meaning("G", "硬骨（Grit）"), Meaning("R", "仗义（Righteous）"),
            Meaning("I", "直性（Intrepid）"), Meaning("T", "扛鼎（Ton）")
        ),
        desc = "你的力气是老天赏饭吃，你也很清楚这一点——所以兄弟朋友的忙，你从来没推过。你嘴笨，讲义气的方式也笨：让你喝酒你就喝，让你挡刀你就挡。有人说这种活法太傻，你咧嘴笑笑：聪明人太多，总得有人当扛事的那个。你就是戈蓝。"
    ),
    GameChar(
        id = "leibinisi", name = "雷比尼斯", code = "AVRG", avatarColor = "#6a6a6a",
        stats = CHR_STATS_V15["leibinisi"]!!,
        meanings = listOf(
            Meaning("A", "均衡（Average）"), Meaning("V", "通用（Versatile）"),
            Meaning("R", "可靠（Reliable）"), Meaning("G", "踏实（Ground）")
        ),
        desc = "你是最标准的「正常人」：没有突出的短板，也没有夸张的天赋。七项指标平平整整，像用尺子量出来的一样。可正是这样的人，撑起了城市运转的每一天——准时上班、好好吃饭、认真生活。平凡得刚刚好。你就是雷比尼斯。"
    ),
    GameChar(
        id = "aimipan", name = "艾米潘", code = "BUSY", avatarColor = "#7a6a6a",
        stats = CHR_STATS_V15["aimipan"]!!,
        meanings = listOf(
            Meaning("B", "忙活（Busy）"), Meaning("U", "热心（Useful）"),
            Meaning("S", "琐碎（Small）"), Meaning("Y", "邻家（Yard）")
        ),
        desc = "你的一天由一百件小事组成：买菜、跑腿、帮邻居看门、给流浪猫留饭。你嘴上抱怨个不停，脚步却一刻没停。你做不成什么大事——但整条街的人都知道，有事找艾米潘准没错。人间烟火气，最抚凡人心。你就是艾米潘。"
    ),
    GameChar(
        id = "demoli", name = "德莫莉", code = "HAUL", avatarColor = "#7a5a3a",
        stats = CHR_STATS_V15["demoli"]!!,
        meanings = listOf(
            Meaning("H", "扛运（Haul）"), Meaning("A", "力气（Able）"),
            Meaning("U", "实诚（Unvarnished）"), Meaning("L", "劳碌（Labor）")
        ),
        desc = "你的肩膀能扛起整条巷子的搬家活儿。你不怕累，也不怕脏——怕的是闲着没事干。你把每一份力气都用在实处，挣的每一分钱都干干净净。你不太会说话，但工地上所有人都服你：因为你从不在活儿上偷懒。你就是德莫莉。"
    ),
    GameChar(
        id = "fuluola", name = "芙洛拉", code = "FLOR", avatarColor = "#6a7a4a",
        stats = CHR_STATS_V15["fuluola"]!!,
        meanings = listOf(
            Meaning("F", "花语（Flora）"), Meaning("L", "柔手（Laggard）"),
            Meaning("O", "暖意（Open）"), Meaning("R", "园艺（Raise）")
        ),
        desc = "你总在闯祸：浇水浇多了，剪枝剪错了，端花盆的手还老是打翻东西。可你种下的每一株苗都长得格外卖力，好像被你笨拙的温柔喂养过。你相信花开有时，也相信好事多磨——慢一点没关系，笨一点也没关系。你就是芙洛拉。"
    ),
    GameChar(
        id = "koukou", name = "蔻蔻", code = "COOK", avatarColor = "#8a5a3a",
        stats = CHR_STATS_V15["koukou"]!!,
        meanings = listOf(
            Meaning("C", "料理（Cook）"), Meaning("O", "热心（Oven）"),
            Meaning("K", "分享（Kitchen）"), Meaning("O", "烟火（Odor）")
        ),
        desc = "你的厨房永远飘着香味，你的锅里永远比别人多一点——因为饭是给人吃的，多一点才像样。你嗓门大、脾气直，手上的活儿却细得很：火候、咸淡全拿捏得刚好。吃饱了，天大的事都好商量。你就是蔻蔻。"
    ),
    GameChar(
        id = "qiong", name = "琼", code = "DUTY", avatarColor = "#4a4a4a",
        stats = CHR_STATS_V15["qiong"]!!,
        meanings = listOf(
            Meaning("D", "值守（Duty）"), Meaning("U", "勤勉（Utile）"),
            Meaning("T", "朴实（Taste）"), Meaning("Y", "日常（Yearn）")
        ),
        desc = "你做着最不起眼的工作，却做得比谁都认真。换班从不迟到，交办的事从不出错，连工具都摆得整整齐齐。你没有远大的野心，你对生活的期待朴素得很——把今天的事做好，把明天的事排好。可正是这样的人，让世界稳稳地转。你就是琼。"
    ),
    GameChar(
        id = "hujiao", name = "胡椒", code = "SPIC", avatarColor = "#7a3a2a",
        stats = CHR_STATS_V15["hujiao"]!!,
        meanings = listOf(
            Meaning("S", "呛口（Spicy）"), Meaning("P", "直冲（Plunge）"),
            Meaning("I", "憨劲（Impel）"), Meaning("C", "热血（Choler）")
        ),
        desc = "你的脑子转得不快，但拳头和脚步从不含糊。别人还在盘算的时候，你已经冲上去了——所以输也输得干脆，赢也赢得痛快。谁要是欺负了你在意的人，你连话都懒得讲，直接往他鼻子上招呼。小辣椒一样的人，够呛，也够真。你就是胡椒。"
    ),
    GameChar(
        id = "che", name = "澈", code = "PURE", avatarColor = "#4a6a6a",
        stats = CHR_STATS_V15["che"]!!,
        meanings = listOf(
            Meaning("P", "通透（Pure）"), Meaning("U", "清爽（Unerring）"),
            Meaning("R", "干净（Rinse）"), Meaning("E", "利落（Ease）")
        ),
        desc = "你喜欢把事情做得干干净净：借的东西按时还，答应的话不掺水，连用的刀都要擦得反光。你不喜欢纠缠，也不喜欢含糊——该断的事断得利索，该认的账认得痛快。活得清澈，是一种能力。你就是澈。"
    ),
    GameChar(
        id = "lisha", name = "丽莎", code = "PHIL", avatarColor = "#5a5a7a",
        stats = CHR_STATS_V15["lisha"]!!,
        meanings = listOf(
            Meaning("P", "藏书（Philology）"), Meaning("H", "推演（Hypothesis）"),
            Meaning("I", "专注（Insight）"), Meaning("L", "静好（Lull）")
        ),
        desc = "你的身体留不住风，你的脑子却装得下一整个世界。你从小在书堆里长大，把别人的冒险读了个遍，自己也悄悄在书页边上写满了批注。别催你出门——对有些人来说，最深的海和最远的路，都在纸上。你就是丽莎。"
    ),
    GameChar(
        id = "tan", name = "昙", code = "FLIT", avatarColor = "#6a6a7a",
        stats = CHR_STATS_V15["tan"]!!,
        meanings = listOf(
            Meaning("F", "掠影（Flit）"), Meaning("L", "轻疾（Lithe）"),
            Meaning("I", "瞬息（Instant）"), Meaning("T", "无常（Transient）")
        ),
        desc = "你像昙花，也像昙花旁边那阵风——来的时候没人察觉，走的时候只留下一句「刚才是不是有谁经过」。你做事讲究一个「快」和「净」：快得连影子都嫌慢，净得连痕迹都嫌多。存在感稀薄，是你最锋利的武器。你就是昙。"
    ),
    GameChar(
        id = "peiji", name = "佩姬", code = "CHAT", avatarColor = "#7a6a5a",
        stats = CHR_STATS_V15["peiji"]!!,
        meanings = listOf(
            Meaning("C", "串门（Chat）"), Meaning("H", "热心（Hearth）"),
            Meaning("A", "邻里（Around）"), Meaning("T", "家常（Tattle）")
        ),
        desc = "你认识这条街上的每一个人：谁家孩子几年级，谁家狗爱叫，你全都门儿清。你爱说话，也爱听人说话——别嫌你唠叨，家长里短里藏着的，可都是人情味。出了事，最先敲你们家门的，往往也是这些人。你就是佩姬。"
    ),
    GameChar(
        id = "maqiduo", name = "玛奇朵", code = "BREW", avatarColor = "#5a3a2a",
        stats = CHR_STATS_V15["maqiduo"]!!,
        meanings = listOf(
            Meaning("B", "煮制（Brew）"), Meaning("R", "暖意（Relish）"),
            Meaning("E", "闲逸（Ease）"), Meaning("W", "甘苦（Warmth）")
        ),
        desc = "你煮的咖啡记住每个人的习惯：谁要多糖，谁不要奶，谁熬夜了需要双份。你话不多，只是安静地听着别人的故事，然后把安慰煮进杯子里。有人说咖啡馆是城市的客厅——那你就是客厅里最沉稳的那盏灯。你就是玛奇朵。"
    ),
    GameChar(
        id = "kaierwen", name = "开尔文", code = "UNIT", avatarColor = "#4a5a6a",
        stats = CHR_STATS_V15["kaierwen"]!!,
        meanings = listOf(
            Meaning("U", "刻度（Unit）"), Meaning("N", "精确（Numeric）"),
            Meaning("I", "恒常（Invariable）"), Meaning("T", "温控（Thermostat）")
        ),
        desc = "你相信一切都可以被测量：温度、长度、心跳，甚至人心。你说话严谨得像在填校准表，做事精确到小数点后面。有人觉得你无趣，你倒觉得——世界能稳定运转，靠的就是这些「无趣」的细节。精度即品质。你就是开尔文。"
    ),
)

/** v1.5.0 新增角色等级（狂厄级 / 高危级 / 普管级） */
val CHAR_RANK_V15: Map<String, String> = mapOf(
    "xiayin" to "狂厄级",
    "tang" to "狂厄级",
    "dimeng" to "狂厄级",
    "hesitiya" to "狂厄级",
    "wanila" to "狂厄级",
    "kexi" to "狂厄级",
    "duya" to "狂厄级",
    "xiangmuxia" to "狂厄级",
    "deleiya" to "狂厄级",
    "LL" to "狂厄级",
    "duruo" to "狂厄级",
    "aowu" to "狂厄级",
    "yao" to "狂厄级",
    "leiwen" to "狂厄级",
    "bianka" to "狂厄级",
    "yifu" to "狂厄级",
    "enfeier" to "狂厄级",
    "adailai" to "狂厄级",
    "naqia" to "狂厄级",
    "nuokesi" to "狂厄级",
    "dudu" to "高危级",
    "yigeni" to "高危级",
    "kamiliyan" to "高危级",
    "aien" to "高危级",
    "puxila" to "高危级",
    "taitela" to "高危级",
    "langhuan" to "高危级",
    "fukesixiansheng" to "高危级",
    "wendi" to "高危级",
    "jiushijiu" to "高危级",
    "weiduoliya" to "高危级",
    "jin" to "高危级",
    "qieerxibojue" to "高危级",
    "lamiya" to "高危级",
    "heluo" to "高危级",
    "aolifu" to "高危级",
    "kelisidina" to "高危级",
    "airuier" to "高危级",
    "youni" to "高危级",
    "shuluma" to "高危级",
    "pajiaqian" to "高危级",
    "chensha" to "高危级",
    "yanyan" to "高危级",
    "duoli" to "高危级",
    "luweiya" to "高危级",
    "kasiluo" to "高危级",
    "kawakawa" to "高危级",
    "meisi" to "高危级",
    "liwei" to "高危级",
    "an" to "高危级",
    "maikun" to "高危级",
    "youlika" to "高危级",
    "laita" to "高危级",
    "ruisi" to "高危级",
    "jialuofanuo" to "高危级",
    "aike" to "高危级",
    "kaxiya" to "高危级",
    "jian" to "高危级",
    "ji" to "高危级",
    "luliaika" to "高危级",
    "lin" to "高危级",
    "ninuo" to "高危级",
    "madierda" to "高危级",
    "xiaoen" to "高危级",
    "gelan" to "高危级",
    "leibinisi" to "普管级",
    "aimipan" to "普管级",
    "demoli" to "普管级",
    "fuluola" to "普管级",
    "koukou" to "普管级",
    "qiong" to "普管级",
    "hujiao" to "普管级",
    "che" to "普管级",
    "lisha" to "普管级",
    "tan" to "普管级",
    "peiji" to "普管级",
    "maqiduo" to "普管级",
    "kaierwen" to "普管级",
)

