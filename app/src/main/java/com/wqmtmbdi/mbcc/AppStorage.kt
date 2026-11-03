package com.wqmtmbdi.mbcc

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import com.google.gson.Gson

/** 玩家自定义资料（头像存 AvatarStore 文件 key，昵称限6字、签名限12字） */
data class CustomProfile(
    var avatar: String? = null,
    var name: String? = null,
    var sig: String? = null
)

/** 鉴定记录（对齐 HTML userData.records，unshift + 最多50条；prob 不存，1:1 保留原版怪癖） */
data class Record(
    var type: String = "",
    var title: String = "",
    var detail: String = "",
    var date: String = "",
    var time: String = "",
    var charId: String? = null,
    var tier: Int? = null
)

/** 玩家存档（对齐 HTML userData，localStorage → SharedPreferences） */
data class UserData(
    var unlockedChars: MutableList<String> = mutableListOf(),
    var testCount: Int = 0,
    var achievements: MutableList<String> = mutableListOf(),
    var theme: String = "default",
    var readAnnouncements: MutableList<Int> = mutableListOf(),
    var customAvatars: MutableMap<String, String> = mutableMapOf(),
    var customProfile: CustomProfile? = null,
    var unlockedResonance: MutableList<String> = mutableListOf(),
    var records: MutableList<Record> = mutableListOf(),
    /**
     * 上次选择的鉴定档位（0 = 还没选过）。
     * 档位选择页用它给老玩家标一句「上次选的是这个」。
     * ★ 新增字段只增不改：Gson 反序列化时老存档缺这个键 → 取默认值 0，不会丢数据。
     */
    var lastTier: Int = 0,
    /**
     * ★ v1.5.0「局长档案」= 玩家本人的七维画像（0–10 浮点），**独立于内置角色数据**。
     * 完成任意一档人格鉴定（深度鉴定 / 狂厄共振）后由 TestTabScreen 自动写入/更新；
     * null = 还没做过鉴定（图鉴的局长详情页会提示去做一次）。
     * 同样遵守「新增字段只增不改」：老存档缺键 → null，不会丢数据。
     */
    var juzhangStats: MutableList<Double>? = null,
    var juzhangTier: Int? = null,
    var juzhangDate: String? = null,
    /**
     * ★ v1.5.1：是否深色模式（主题 = 配色家族 + 明暗两版）。
     * null = 老存档没这个键 → 按深色处理（保持「零镜仪器」原有的观感），不会让老用户突然变浅色。
     */
    var darkMode: Boolean? = null,
    /**
     * ★ v1.5.2：深色模式是否跟随系统。**默认关闭**（null/false = 不跟随，用上面的 darkMode）。
     * 开启后由 `AppStorage.systemDark`（MainActivity 实时同步的系统状态）决定明暗。
     */
    var followSystemDark: Boolean? = null
)

/** 数据存储单例（SharedPreferences + Gson JSON，模式对齐 Jizhangben AppStorage） */
object AppStorage {
    private const val PREFS = "mbdi_data"
    private const val KEY_DATA = "user_data"
    private const val KEY_PRIVACY = "privacy_agreed_version"
    private const val KEY_ANNOUNCE = "announce_agreed_version"
    private const val KEY_CALIB = "mbdi_dcpm_calib_v3"
    private const val KEY_SEEN_UNLOCKS = "gallery_seen_unlocks"

    /** 隐私条款版本号；隐私内容更新时 +1，已读用户会再次收到弹窗（v3 = 2026-10-01 换成完整版十七节协议） */
    const val PRIVACY_VERSION = 3

    /** 公告版本号；公告内容更新时 +1（v2 = 2026-10-14 新增 v1.5.0 公告） */
    const val ANNOUNCE_VERSION = 2

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    lateinit var data: UserData
        private set

    /** 主题切换状态：递增使所有读取主题的 Composable 立即重组 */
    private val themeTick = mutableIntStateOf(0)

    /** 当前主题色（读取时订阅 themeTick，随切换即时变色） */
    /** 运行时缓存的系统深色状态（由 MBDITheme 每帧同步，不存档） */
    var systemDark: Boolean = true

    fun currentMBDITheme(): ThemeColors {
        themeTick.intValue
        // 主题 = 配色家族（data.theme）+ 明暗：
        // 跟随系统（默认关闭）→ 用 systemDark；否则用 data.darkMode（老存档缺键 → 深色）
        val dark = if (data.followSystemDark == true) systemDark else (data.darkMode ?: true)
        return MBDIThemes.resolve(data.theme, dark)
    }

    /** 由 MBDITheme 同步系统深色状态；变化时触发全屏重组 */
    fun updateSystemDark(dark: Boolean) {
        if (systemDark != dark) {
            systemDark = dark
            themeTick.intValue++
        }
    }

    /** 切换「深色模式跟随系统」 */
    fun setFollowSystemDark(follow: Boolean) {
        data.followSystemDark = follow
        themeTick.intValue++
        save()
    }

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_DATA, null)
        data = if (!json.isNullOrBlank()) {
            try {
                val d = gson.fromJson(json, UserData::class.java)
                if (d.unlockedChars == null) d.unlockedChars = mutableListOf()
                if (d.achievements == null) d.achievements = mutableListOf()
                if (d.readAnnouncements == null) d.readAnnouncements = mutableListOf()
                if (d.customAvatars == null) d.customAvatars = mutableMapOf()
                if (d.unlockedResonance == null) d.unlockedResonance = mutableListOf()
                if (d.records == null) d.records = mutableListOf()
                if (d.theme.isNullOrBlank()) d.theme = "default"
                d
            } catch (e: Exception) {
                UserData()
            }
        } else {
            UserData()
        }
        save()
    }

    fun save() {
        if (::prefs.isInitialized) {
            prefs.edit().putString(KEY_DATA, gson.toJson(data)).apply()
        }
    }

    /** 切换主题，持久化并触发全屏重组 */
    fun setTheme(id: String) {
        data.theme = id
        if (!data.achievements.contains("colorist")) data.achievements.add("colorist")
        themeTick.intValue++
        save()
    }

    /**
     * ★ v1.5.1：切换「配色 + 明暗」。
     * 配色传家族 id（如 "zero" / "baiye"），明暗传开关值；themeTick 递增触发全屏重组。
     */
    fun setAppearance(familyId: String, dark: Boolean) {
        data.theme = familyId
        data.darkMode = dark
        if (!data.achievements.contains("colorist")) data.achievements.add("colorist")
        themeTick.intValue++
        save()
    }

    /** 发放成就（幂等），对齐 HTML achievements.includes+push 语义 */
    fun grantAchievement(id: String) {
        if (!data.achievements.contains(id)) {
            data.achievements.add(id)
            save()
        }
    }

    /** 存档一条鉴定记录（unshift + 最多50条，对齐 HTML saveRecord） */
    fun saveRecord(type: String, title: String, detail: String, charId: String?, tier: Int?) {
        val d = java.util.Calendar.getInstance()
        val date = "%04d.%02d.%02d".format(d.get(java.util.Calendar.YEAR), d.get(java.util.Calendar.MONTH) + 1, d.get(java.util.Calendar.DAY_OF_MONTH))
        val time = "%02d:%02d".format(d.get(java.util.Calendar.HOUR_OF_DAY), d.get(java.util.Calendar.MINUTE))
        data.records.add(0, Record(type = type, title = title, detail = detail, date = date, time = time, charId = charId, tier = tier))
        if (data.records.size > 50) data.records = data.records.take(50).toMutableList()
        save()
    }

    /** 清空所有鉴定记录 */
    fun clearRecords() {
        data.records = mutableListOf()
        save()
    }

    /**
     * ★ 2026-09-28 新增（UI 重做第 4 批）：「我的」页的「重置全部数据」。
     *
     * 只清**玩家进度与自定义内容**；主题、公告已读状态保留
     * （公告已读不清，否则重置完又会弹一次公告）。
     */
    fun resetAllData() {
        data.unlockedChars = mutableListOf()
        data.unlockedResonance = mutableListOf()
        data.achievements = mutableListOf()
        data.records = mutableListOf()
        data.customAvatars = mutableMapOf()
        data.customProfile = null
        data.testCount = 0
        data.lastTier = 0
        // ★ v1.5.0：局长档案（玩家七维画像）也属于玩家进度，一并清空
        data.juzhangStats = null
        data.juzhangTier = null
        data.juzhangDate = null
        save()
    }

    /** 用户已同意的隐私版本号（0 = 从未同意） */
    fun privacyAgreedVersion(): Int =
        if (::prefs.isInitialized) prefs.getInt(KEY_PRIVACY, 0) else 0

    /** 记录用户已同意当前隐私版本 */
    fun setPrivacyAgreed() {
        if (::prefs.isInitialized) prefs.edit().putInt(KEY_PRIVACY, PRIVACY_VERSION).apply()
    }

    /** 用户已读的公告版本号（0 = 从未读过） */
    fun announcementAgreedVersion(): Int =
        if (::prefs.isInitialized) prefs.getInt(KEY_ANNOUNCE, 0) else 0

    /**
     * 记录公告已读。
     * ★ 2026-10-02 修复（用户反馈：看过公告红点还在）：
     *   红点判定读的是**存档里的 [data.readAnnouncements] 列表**，而这里以前只写了另一套
     *   SharedPreferences 版本号，两条链路不通 → 红点永远亮着。
     *   现在统一写入 readAnnouncements 并立即保存（旧的版本号写入保留，兼容历史数据）。
     */
    fun setAnnouncementRead() {
        val latest = com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS.maxOfOrNull { it.id }
        if (latest != null && !data.readAnnouncements.contains(latest)) {
            data.readAnnouncements.add(latest)
            save()
        }
        if (::prefs.isInitialized) prefs.edit().putInt(KEY_ANNOUNCE, ANNOUNCE_VERSION).apply()
    }

    /**
     * 是否有未读公告（最新公告 id 尚未读）。
     * ★ 第 13 批：公告「红点 / 自动弹窗」统一走这一个判定，避免多处各写一套又不一致。
     */
    fun isAnnouncementUnread(): Boolean =
        (data.readAnnouncements.maxOrNull() ?: 0) < (com.wqmtmbdi.mbcc.data.ANNOUNCEMENTS.maxOfOrNull { it.id } ?: 0)

    // ===== 图鉴「新解锁」标记（方案 5.5：解锁闪光只演示一次）=====

    /**
     * 上次打开图鉴时已经见过的解锁集合。id 带类型前缀：`c:` 禁闭者 / `r:` 狂厄特化 / `a:` 成就。
     * 打开图鉴时用「当前解锁集合 - 已见过集合」算出本次要闪光的新解锁。
     */
    fun seenUnlockSet(): Set<String> =
        if (::prefs.isInitialized) prefs.getStringSet(KEY_SEEN_UNLOCKS, null)?.toSet() ?: emptySet()
        else emptySet()

    /** 记录当前全部解锁（图鉴打开、闪光播完后调用） */
    fun markUnlocksSeen() {
        if (!::prefs.isInitialized) return
        val ids = buildSet {
            data.unlockedChars.forEach { add("c:$it") }
            data.unlockedResonance.forEach { add("r:$it") }
            data.achievements.forEach { add("a:$it") }
        }
        prefs.edit().putStringSet(KEY_SEEN_UNLOCKS, ids).apply()
    }

    // ===== DCPM 校准先验缓存（对齐 HTML localStorage mbdi_dcpm_calib_v3）=====

    /** 校准缓存数据类 */
    data class CalibCache(val sig: String, val p2: DoubleArray?, val p3: DoubleArray?)

    /**
     * 读取缓存的校准先验（v1.5.2 起为 5 档：12 / 20 / 30 / 40 / 50 题）。
     * sig 不匹配、缺档、或先验长度与当前角色数不符 → 返回 null（调用方会重算）。
     */
    fun loadCalib(sig: String): Map<Int, DoubleArray>? {
        if (!::prefs.isInitialized) return null
        return try {
            val json = prefs.getString(KEY_CALIB, null) ?: return null
            val o = com.google.gson.JsonParser.parseString(json).asJsonObject
            if (o.get("sig")?.asString != sig) return null
            val p = o.getAsJsonObject("p")
            val n = com.wqmtmbdi.mbcc.data.CHARACTERS.size
            val out = HashMap<Int, DoubleArray>()
            for (t in 1..5) {
                val arr = gson.fromJson(p.get("$t"), DoubleArray::class.java) ?: return null
                if (arr.size != n) return null
                out[t] = arr
            }
            out
        } catch (e: Exception) {
            null
        }
    }

    /** 保存校准先验（3 档；key 与 sig 对齐 HTML） */
    fun saveCalib(sig: String, priors: Map<Int, DoubleArray>) {
        if (!::prefs.isInitialized) return
        try {
            val root = com.google.gson.JsonObject()
            root.addProperty("sig", sig)
            val p = com.google.gson.JsonObject()
            for (t in 1..5) {
                val arr = priors[t] ?: return
                p.add("$t", gson.toJsonTree(arr))
            }
            root.add("p", p)
            prefs.edit().putString(KEY_CALIB, root.toString()).apply()
        } catch (e: Exception) {
        }
    }
}
