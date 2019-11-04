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

package com.raywenderlich.android.memerepo.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Meme

object MemeRepo {
    private const val KEY = "allMemes"
    private val DEFAULTS = mutableListOf(
        Meme("Classic", "https://i.imgflip.com/2jcns7.jpg", Meme.Category.CLASSIC),
        Meme("Dank", "https://pics.me.me/when-you-show-her-your-dank-meme-collection-for-the-14571318.png", Meme.Category.DANK)
    )
    private lateinit var prefs: SharedPreferences
    private val memes = MutableLiveData<List<Meme>>()
    private val gson = Gson()
    private val type = object : TypeToken<MutableList<Meme>>() { }.type

    fun load(context: Context) {
        prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        memes.value = gson.fromJson(prefs.getString(KEY, null), type) ?: DEFAULTS
    }

    fun add(meme: Meme) {
        memes.value = (memes.value ?: DEFAULTS) + meme
        prefs.edit().putString(KEY, gson.toJson(memes.value, type)).apply()
    }

    val allMemes: LiveData<List<Meme>> = memes
}
