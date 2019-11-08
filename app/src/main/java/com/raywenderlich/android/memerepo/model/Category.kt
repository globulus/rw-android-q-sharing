package com.raywenderlich.android.memerepo.model

enum class Category {
  CLASSIC, DANK;

  val id = this::class.java.name + toString()

  companion object {
    fun positionFor(id: String) = values().indexOfFirst { it.id == id }
  }
}
