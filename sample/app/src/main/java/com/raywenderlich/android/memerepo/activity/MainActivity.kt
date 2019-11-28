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
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Category
import com.raywenderlich.android.memerepo.storage.MemeRepo
import com.raywenderlich.android.memerepo.util.ShareUtil
import com.squareup.picasso.Picasso
import net.globulus.kotlinui.applyOnView
import net.globulus.kotlinui.bindTo
import net.globulus.kotlinui.margins
import net.globulus.kotlinui.onClickListener
import net.globulus.kotlinui.widgets.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentTabs(
        Category.values().map { it.resId }.toTypedArray(),
        Category.values().toList()
    ) { category ->
      grid({ MemeRepo.memes.filter { it.category == category } }) { meme ->
        column {
          val img = image()
          Picasso.get().load(meme.url).into(img.view)
          text(meme.title).margins(10).applyOnView {
            gravity = Gravity.CENTER
          }
        }.onClickListener {
          ShareUtil.shareMeme(context, meme)
        }
      }.bindTo(MemeRepo, MemeRepo::memes)
    }.apply {
      toolbar.apply {
        title = getString(R.string.app_name)
        setSupportActionBar(this)
      }
      fab {
        startActivity(Intent(context, MemeActivity::class.java))
      }
    }

      ShareUtil.publishMemeShareShortcuts(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    ShareUtil.unPublishMemeShareShortcuts(this)
  }
}
