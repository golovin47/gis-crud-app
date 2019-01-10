package com.gis.utils.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.gis.utils.domain.ImageLoader

@BindingAdapter(
  "android:loadImg",
  "android:loadPlaceholder",
  "android:imgLoader", requireAll = true
)
fun ImageView.loadImg(imgUrl: String, placeholder: Int, imgLoader: ImageLoader) {
  imgLoader.loadImg(this, imgUrl, placeholder)
}