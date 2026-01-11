package dev.sagi.georgethevetrinator.services

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.sagi.georgethevetrinator.R

class ImageLoader(context: Context) {
    private val appContext = context.applicationContext

    fun loadImage(url: String, imageView: ImageView) {
        Glide.with(appContext)
            .load(url)
            .placeholder(R.drawable.img_glide_placeholder) // Image shown while loading
            .error(R.drawable.img_glide_error)       // Image shown if URL fails
            .transition(DrawableTransitionOptions.withCrossFade()) // Smooth fade-in
            .centerCrop()
            .into(imageView)
    }

    fun loadImage(resourceId: Int, imageView: ImageView) {
        Glide.with(appContext)
            .load(resourceId)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}