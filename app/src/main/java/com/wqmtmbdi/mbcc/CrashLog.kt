package com.wqmtmbdi.mbcc

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 单条崩溃记录（相同堆栈 5 分钟内自动合并计数） */
data class CrashRecord(
    val time: Long,
    val count: Int,
    val stack: String
)

/**
 * 崩溃日志存储（第 7 批新增，口径对齐记账本 / 局长终端）：
 *  - 独立 SharedPreferences 保存（**不碰 AppStorage 存档**，崩溃时更安全）；
 *  - 最新在前，最多 60 条；相同内容 5 分钟内自动合并成一条并计数（×N）；
 *  - 写入用 commit() 同步落盘，避免崩溃后进程被杀导致记录丢失。
 */
object CrashLogStore {
    private const val PREF = "crash_records"
    private const val KEY = "records"
    private const val MAX = 60
    private const val MERGE_WINDOW_MS = 5 * 60 * 1000L
    private val gson = Gson()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    /** 全部崩溃记录（新的在前） */
    fun records(context: Context): List<CrashRecord> {
        val raw = prefs(context).getString(KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<CrashRecord>>() {}.type
            gson.fromJson<List<CrashRecord>>(raw, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun hasRecords(context: Context): Boolean = records(context).isNotEmpty()

    /** 记录一次崩溃；相同堆栈且 5 分钟内 → 合并计数 */
    fun record(context: Context, stack: String) {
        try {
            val now = System.currentTimeMillis()
            val list = records(context).toMutableList()
            val head = list.firstOrNull()
            if (head != null && head.stack == stack && now - head.time < MERGE_WINDOW_MS) {
                list[0] = head.copy(time = now, count = head.count + 1)
            } else {
                list.add(0, CrashRecord(now, 1, stack))
            }
            val trimmed = list.take(MAX)
            prefs(context).edit().putString(KEY, gson.toJson(trimmed)).commit()
        } catch (_: Exception) {
            // 崩溃记录本身失败也不影响正常崩溃流程
        }
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY).commit()
    }

    /** 单条导出成 txt 的全文（含设备信息，用户可直接把文件发给作者） */
    fun fileText(context: Context, record: CrashRecord): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return buildString {
            appendLine("【崩溃日志】无期：人格鉴定")
            appendLine("软件版本：${BuildConfig.VERSION_NAME}")
            appendLine("系统版本：Android ${Build.VERSION.RELEASE}（API ${Build.VERSION.SDK_INT}）")
            appendLine("机型代号：${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("崩溃时间：${fmt.format(Date(record.time))}")
            appendLine("重复次数：${record.count} 次")
            appendLine("==================================")
            appendLine()
            append(record.stack)
        }
    }

    /** 一次导出全部记录（下载全部时用） */
    fun allFileText(context: Context): String {
        val list = records(context)
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return buildString {
            appendLine("【崩溃日志】无期：人格鉴定 · 共 ${list.size} 条")
            appendLine("软件版本：${BuildConfig.VERSION_NAME}")
            appendLine("系统版本：Android ${Build.VERSION.RELEASE}（API ${Build.VERSION.SDK_INT}）")
            appendLine("机型代号：${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine()
            list.forEachIndexed { i, r ->
                appendLine("──────── 第 ${i + 1} 条 · ${fmt.format(Date(r.time))}（×${r.count}）────────")
                appendLine(r.stack)
                appendLine()
            }
        }
    }
}

/**
 * 全局崩溃捕获：每次闪退自动留档。
 * 只负责记录，不拦截崩溃流程（记录后继续交给系统默认 handler）。
 * 在 MainActivity.onCreate 最前面调用（先于一切初始化）。
 */
object CrashCatcher {
    fun install(context: Context) {
        val app = context.applicationContext
        val default = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                CrashLogStore.record(app, Log.getStackTraceString(throwable))
            } catch (_: Exception) {
            }
            default?.uncaughtException(thread, throwable)
        }
    }
}