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
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Meme
import com.raywenderlich.android.memerepo.storage.MemeRepo
import com.raywenderlich.android.memerepo.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun runOnIOThread(action: () -> Unit) {
  IO_EXECUTOR.execute(action)
}

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    MemeRepo.load(this)
    val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
    viewPager.adapter = sectionsPagerAdapter
    tabs.setupWithViewPager(viewPager)

    fab.setOnClickListener {
      startActivity(Intent(this, MemeActivity::class.java))
    }

    runOnIOThread {
      publishShareShortcuts()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    runOnIOThread {
      ShortcutManagerCompat.removeAllDynamicShortcuts(this)
    }
  }

  private fun publishShareShortcuts() {
    ShortcutManagerCompat.addDynamicShortcuts(this, Meme.Category.values().map(::categoryToShortcut))
  }

  private fun categoryToShortcut(category: Meme.Category): ShortcutInfoCompat {
    return ShortcutInfoCompat.Builder(this, category.id)
        .setShortLabel(category.name)
        .setLongLabel(category.name + "LONG")
        .setPerson(Person.Builder()
            .setName(category.name)
            .setKey(category.id)
            .build())
        .setIcon(IconCompat.createWithResource(this, R.drawable.ic_launcher_foreground))
        .setCategories(setOf("com.raywenderlich.android.memerepo.category.MEME_STORE_TARGET"))
        .setIntent(Intent(this, MemeActivity::class.java).apply {
          action = Intent.ACTION_SEND
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            putExtra(Intent.EXTRA_SHORTCUT_ID, category.id)
          }
        })
        .setLongLived()
        .setRank(0)
        .build()
  }
}