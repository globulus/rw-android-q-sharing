package com.raywenderlich.android.memerepo.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.Person
import androidx.core.content.FileProvider
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.activity.MemeActivity
import com.raywenderlich.android.memerepo.model.Meme
import java.io.File

object ShareUtil {

  private const val FILE_PROVIDER = "com.raywenderlich.android.memerepo.FileProvider"
  private const val SHARE_CATEGORY = "com.raywenderlich.android.memerepo.category.MEME_STORE_TARGET"

  fun shareMeme(context: Context, meme: Meme) {
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "image/jpeg"
      putExtra(Intent.EXTRA_TITLE, meme.title)
      val uri = Uri.parse(meme.url)
      putExtra(Intent.EXTRA_STREAM, uri)
      clipData = ClipData.newUri(context.contentResolver, "SMTH",
          FileProvider.getUriForFile(context, FILE_PROVIDER, File(meme.url)))
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(Intent.createChooser(intent, null))
  }

  fun publishMemeShareShortcuts(context: Context) {
    ShortcutManagerCompat.addDynamicShortcuts(context, Meme.Category.values()
        .take(ShortcutManagerCompat.getMaxShortcutCountPerActivity(context))
        .map { it.toShortcut(context) }
    )
  }

  fun unPublishMemeShareShortcuts(context: Context) {
    ShortcutManagerCompat.removeAllDynamicShortcuts(context)
  }

  private fun Meme.Category.toShortcut(context: Context): ShortcutInfoCompat {
    return ShortcutInfoCompat.Builder(context, id)
        .setShortLabel(name)
        .setLongLabel(name + "LONG")
        .setPerson(Person.Builder()
            .setName(name)
            .setKey(id)
            .build())
        .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
        .setCategories(setOf(SHARE_CATEGORY))
        .setIntent(Intent(context, MemeActivity::class.java).apply {
          action = Intent.ACTION_SEND
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            putExtra(Intent.EXTRA_SHORTCUT_ID, id)
          }
        })
        .setLongLived(true)
        .setRank(0)
        .build()
  }
}
