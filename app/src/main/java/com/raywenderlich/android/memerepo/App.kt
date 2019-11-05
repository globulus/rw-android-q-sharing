package com.raywenderlich.android.memerepo

import android.app.Application
import com.raywenderlich.android.memerepo.storage.MemeRepo

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    MemeRepo.load(this)
  }
}