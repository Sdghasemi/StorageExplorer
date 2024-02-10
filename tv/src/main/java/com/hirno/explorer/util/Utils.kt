package com.hirno.explorer.util

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

fun String.substringAfter(phrase: String, caseInsensitive: Boolean = false): String {
    return substring(indexOf(phrase, ignoreCase = caseInsensitive) + phrase.length)
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

val <T> Collection<T>?.size: Int
    get() = this?.size ?: 0