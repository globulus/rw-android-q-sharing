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
    private const val KEY = "memes"
    private val DEFAULTS = mutableListOf(
        Meme("Classic", "https://i.imgflip.com/2jcns7.jpg", Meme.Category.CLASSIC),
        Meme("Dank", "https://pics.me.me/when-you-show-her-your-dank-meme-collection-for-the-14571318.png", Meme.Category.DANK)
    )
    private lateinit var prefs: SharedPreferences
    private val _memes = MutableLiveData<List<Meme>>()
    private val gson = Gson()
    private val type = object : TypeToken<MutableList<Meme>>() {}.type

    fun load(context: Context) {
        prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        _memes.value = gson.fromJson(prefs.getString(KEY, null), type) ?: DEFAULTS
    }

    fun addMeme(meme: Meme) {
        _memes.value = (_memes.value ?: DEFAULTS) + meme
        prefs.edit().putString(KEY, gson.toJson(_memes, type)).apply()
    }

    val memes: LiveData<List<Meme>> = _memes
}
