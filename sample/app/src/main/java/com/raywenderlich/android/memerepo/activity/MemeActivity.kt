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

package com.raywenderlich.android.memerepo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Category
import com.raywenderlich.android.memerepo.model.Meme
import com.raywenderlich.android.memerepo.storage.MemeRepo
import com.squareup.picasso.Picasso
import net.globulus.kotlinui.*
import net.globulus.kotlinui.widgets.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MemeActivity : AppCompatActivity() {

  private lateinit var box: Box

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    box = Box(this)
    setContentView(box)

    if (intent?.action == Intent.ACTION_SEND) {
      if (intent.type?.startsWith("image/") == true) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
          box.pasteUrl = it.toString()
          Picasso.get().load(it).into(box.image.view)
        }
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val id = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
        if (id?.isNotEmpty() == true) {
          box.dank = Category.positionFor(id) == 1
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.meme, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.addMeme -> {
        val saved = saveMeme()
        if (saved) {
          finish()
        }
        true
      }
      else -> false
    }
  }

  private fun saveMeme(): Boolean {
    val saved = saveJpeg()
    if (!saved) {
      box.urlInput.error(R.string.url_error)
      return false
    } else {
      box.urlInput.error(0)
    }
    if (box.title.isEmpty()) {
      box.titleInput.error(R.string.title_error)
      return false
    } else {
      box.titleInput.error(0)
    }
    val meme = Meme(box.title, box.uri!!, Category.values()[if (box.dank) 1 else 0])
    MemeRepo.add(meme)
    return true
  }

  private fun saveJpeg(): Boolean {
    return (box.image.view.drawable as? BitmapDrawable)?.bitmap?.let {
      try {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "meme_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        it.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.close()
        val bmpUri = Uri.fromFile(file)
        box.uri = bmpUri?.toString()
      } catch (e: IOException) {
        e.printStackTrace()
      }
      true
    } ?: false
  }

  class Box(owner: MemeActivity) : KViewBox(owner) {

    lateinit var titleInput: KMaterialTextField
    lateinit var urlInput: KMaterialTextField
    lateinit var image: KImage

    var title = ""
    var pasteUrl by state("")
    var url = ""
    var dank = false
    var uri: String? = null

    override val root = rootToolbarColumn {
      titleInput = materialTextField { textField(::title) }
          .hint(R.string.title)
          .margins(0, 0, 0, 8)
      urlInput = materialTextField { textField(::url) }
          .hint(R.string.url)
          .margins(0, 0, 0, 8)
      urlInput.textField.bindTo(::pasteUrl)
      image = image().margins(0, 0, 0, 8)
      checkBox(R.string.dank, ::dank)
      toolbar.apply {
        title = context.getString(R.string.title_activity_meme)
        owner.setSupportActionBar(this)
      }
    }
  }
}
