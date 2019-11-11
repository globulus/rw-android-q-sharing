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

package com.raywenderlich.android.memerepo.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Meme
import com.raywenderlich.android.memerepo.util.ShareUtil
import com.squareup.picasso.Picasso

class MemesAdapter(private val context: Context) : BaseAdapter() {

  var memes = emptyList<Meme>()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    val cv = convertView
        ?: LayoutInflater.from(context).inflate(R.layout.item_meme, parent, false).apply {
      tag = ViewHolder(findViewById(R.id.image), findViewById(R.id.title))
    }
    val viewHolder = cv.tag as ViewHolder
    viewHolder.bind(cv, memes[position])
    return cv
  }

  override fun getItem(position: Int): Any? {
    return null
  }

  override fun getItemId(position: Int): Long {
    return 0L
  }

  override fun getCount(): Int {
    return memes.size
  }

  class ViewHolder(private val image: ImageView,
                   private val title: TextView) {
    fun bind(view: View, meme: Meme) {
      title.text = meme.title
      Picasso.get().load(meme.url).into(image)
      view.setOnClickListener {
        ShareUtil.shareMeme(view.context, meme)
      }
    }
  }
}
