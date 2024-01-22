package com.hirno.explorer.util

import android.content.ContentResolver
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import java.io.File
import java.util.Locale


fun getMimeType(file: File): String? {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
        file.path
    )
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        fileExtension.lowercase(Locale.getDefault())
    )
}

inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> T): T {
    return if (condition) block(this) else this
}

inline fun RecyclerView.addScrollListener(crossinline scrollListener: RecyclerView.(dx: Int, dy: Int) -> Unit) = apply {
    addOnScrollListener(object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            scrollListener(dx, dy)
        }
    })
}

val View.inflater: LayoutInflater
    get() = LayoutInflater.from(context)