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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.raywenderlich.android.memerepo.R
import com.raywenderlich.android.memerepo.model.Meme
import com.raywenderlich.android.memerepo.storage.MemeRepo

class MemeCollectionFragment : Fragment() {

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_main, container, false)
    val category =  arguments?.getSerializable(ARG_CATEGORY)
        ?: throw IllegalStateException("Category must be passed!")
    val adapter = MemesAdapter(root.context)
    root.findViewById<GridView>(R.id.gridView).adapter = adapter
    MemeRepo.allMemes.observe(this, Observer<List<Meme>> {
      adapter.memes = it.filter { meme -> meme.category == category }
    })
    return root
  }

  companion object {
    private const val ARG_CATEGORY = "category"

    fun newInstance(category: Meme.Category): MemeCollectionFragment {
      return MemeCollectionFragment().apply {
        arguments = Bundle().apply {
          putSerializable(ARG_CATEGORY, category)
        }
      }
    }
  }
}
