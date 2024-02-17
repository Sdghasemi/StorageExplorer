package com.hirno.explorer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Storage(
    val uuid: String = "",
    val path: File = File("/"),
    val description: String = "",
): Parcelable