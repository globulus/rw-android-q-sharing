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
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Category
import com.raywenderlich.android.memerepo.model.Meme
import com.raywenderlich.android.memerepo.storage.MemeRepo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_meme.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MemeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_meme)
    setSupportActionBar(toolbar)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    memeUri.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (s?.isNotEmpty() == true) {
          Picasso.get().load(memeUri.text.toString()).into(imagePreview)
        }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
    })

    category.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
        Category.values().map { getString(it.resId) }).apply {
      setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
      (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
        memeUri.setText(it.toString())
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val id = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
        if (id?.isNotEmpty() == true) {
          category.setSelection(Category.positionFor(id))
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
      memeUrlLayout.error = getString(R.string.url_error)
      return false
    } else {
      memeUrlLayout.error = null
    }
    if (memeTitle.text.isEmpty()) {
      memeTitleLayout.error = getString(R.string.title_error)
      return false
    } else {
      memeTitleLayout.error = null
    }
    val meme = Meme(memeTitle.text.toString(), memeUri.tag.toString(), Category.values()[category.selectedItemPosition])
    MemeRepo.add(meme)
    return true
  }

  private fun saveJpeg(): Boolean {
    return (imagePreview.drawable as? BitmapDrawable)?.bitmap?.let {
      try {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "meme_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        it.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.close()
        val bmpUri = Uri.fromFile(file)
        memeUri.tag = bmpUri?.toString()
      } catch (e: IOException) {
        e.printStackTrace()
      }
      true
    } ?: false
  }
}
