/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import com.raywenderlich.android.memerepo.model.Category
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
      clipData = ClipData.newUri(context.contentResolver,
          context.getString(R.string.app_name),
          FileProvider.getUriForFile(context, FILE_PROVIDER, File(meme.url)))
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(Intent.createChooser(intent, null))
  }

  fun publishMemeShareShortcuts(context: Context) {
    ShortcutManagerCompat.addDynamicShortcuts(context, Category.values()
        .take(ShortcutManagerCompat.getMaxShortcutCountPerActivity(context))
        .map { it.toShortcut(context) }
    )
  }

  fun unPublishMemeShareShortcuts(context: Context) {
    ShortcutManagerCompat.removeAllDynamicShortcuts(context)
  }

  private fun Category.toShortcut(context: Context): ShortcutInfoCompat {
    val label = context.getString(resId)
    return ShortcutInfoCompat.Builder(context, id)
        .setShortLabel(label)
        .setLongLabel(context.getString(R.string.new_meme, label))
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
