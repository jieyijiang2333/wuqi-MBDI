package com.wqmtmbdi.mbcc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 * 头像存取：保存为 filesDir/avatar_<key>.png
 * key 可以是角色 id（juzhang / serin / …）或 个人主页 "profile"
 */
object AvatarStore {
    const val PROFILE_KEY = "profile"

    private fun file(context: Context, key: String): File =
        File(context.filesDir, "avatar_$key.png")

    /** 保存头像，返回是否成功 */
    fun saveAvatar(context: Context, key: String, bmp: Bitmap): Boolean = try {
        val f = file(context, key)
        FileOutputStream(f).use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out) }
        true
    } catch (e: Exception) {
        false
    }

    /** 读取头像，不存在返回 null */
    fun loadAvatar(context: Context, key: String): Bitmap? = try {
        val f = file(context, key)
        if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
    } catch (e: Exception) {
        null
    }

    /** 删除头像（恢复默认占位） */
    fun clearAvatar(context: Context, key: String) {
        try { file(context, key).delete() } catch (_: Exception) {}
    }
}

/** 从相册 Uri 解码位图：自动应用 EXIF 方向，限制最大边 1600px 防 OOM */
fun decodeImage(context: Context, uri: Uri): Bitmap? = try {
    if (Build.VERSION.SDK_INT >= 28) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, info, _ ->
            val m = max(info.size.width, info.size.height)
            if (m > 1600) {
                decoder.setTargetSize(info.size.width * 1600 / m, info.size.height * 1600 / m)
            }
        }
    } else {
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    }
} catch (e: Exception) {
    null
}
