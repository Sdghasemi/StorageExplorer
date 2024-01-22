package com.hirno.explorer.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
@Entity(tableName = "Media", indices = [
    Index(
        value = ["lastUseMillis"],
        orders = [Index.Order.DESC]
    )
])
data class Media @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(name = "path")
    @SerializedName("path")
    var path: String = "",
    @ColumnInfo(name = "storageUuid")
    @SerializedName("storageUuid")
    var storageUuid: String = "",
    @ColumnInfo(name = "storageDescription")
    @SerializedName("storageDescription")
    var storageDescription: String = "",
    @ColumnInfo(name = "mimeType")
    @SerializedName("mimeType")
    var mimeType: String = "",
    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    var duration: Long? = null,
    @ColumnInfo(name = "width")
    @SerializedName("width")
    var width: Int? = null,
    @ColumnInfo(name = "height")
    @SerializedName("height")
    var height: Int? = null,
    @Ignore
    @SerializedName("slides")
    var slides: List<Slide>? = null,
) : Parcelable, Comparable<Media> {
    @Ignore
    @IgnoredOnParcel
    val file = File(path)
    @IgnoredOnParcel
    @ColumnInfo(name = "lastUseMillis")
    @SerializedName("lastUseMillis")
    var lastUseMillis: Long = System.currentTimeMillis()
    @Ignore
    @IgnoredOnParcel
    val trimmedPath = file.parentFile?.path?.run { substring(indexOf(storageUuid) + storageUuid.length) } ?: ""
    val isDirectory: Boolean
        get() = file.isDirectory
    val size: Long
        get() = file.length()
    val isImage: Boolean
        get() = mimeType.startsWith("image")
    val isVideo: Boolean
        get() = mimeType.startsWith("video")

    fun updateLastUse() {
        lastUseMillis = System.currentTimeMillis()
    }

    /*
     * To sort media based on file names in ascending order
     */
    override fun compareTo(other: Media) = file.name.compareTo(other.file.name)

    @Parcelize
    data class Slide(
        @PrimaryKey
        @ColumnInfo(name = "path")
        @SerializedName("mediaPath")
        val mediaPath: String,
        @ColumnInfo(name = "slide")
        @SerializedName("path")
        val path: String,
    ): Parcelable {
        @Ignore
        @IgnoredOnParcel
        val file = File(path)
    }
}