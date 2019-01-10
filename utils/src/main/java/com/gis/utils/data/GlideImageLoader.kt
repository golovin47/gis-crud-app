package com.gis.utils.data

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gis.utils.domain.ImageLoader

object GlideImageLoader : ImageLoader {

  override fun loadImg(iv: ImageView, url: String, placeholder: Int) {
    GlideApp.with(iv)
      .load(if (url.isNotBlank()) url else placeholder)
      .placeholder(placeholder)
      .error(placeholder)
      .fallback(placeholder)
      .into(iv)
  }
}