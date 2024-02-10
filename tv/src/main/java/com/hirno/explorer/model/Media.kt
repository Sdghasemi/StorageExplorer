package com.hirno.explorer.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName
import com.hirno.explorer.util.substringAfter
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
    @IgnoredOnParcel
    val file by lazy { File(path) }
    @IgnoredOnParcel
    @ColumnInfo(name = "lastUseMillis")
    @SerializedName("lastUseMillis")
    var lastUseMillis: Long = System.currentTimeMillis()
    @IgnoredOnParcel
    val trimmedPath by lazy { file.parentFile?.path?.substringAfter(storageUuid) ?: "" }
    @IgnoredOnParcel
    val nameWithoutExtension by lazy { file.nameWithoutExtension }
    @IgnoredOnParcel
    val extension by lazy { file.extension.uppercase() }
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
    @Entity(
        tableName = "Slides",
        indices = [
            Index(
                value = ["mediaPath", "path"],
                orders = [Index.Order.ASC, Index.Order.ASC]
            )
        ],
    )
    data class Slide(
        @ColumnInfo(name = "mediaPath")
        @SerializedName("mediaPath")
        val mediaPath: String,
        @PrimaryKey
        @ColumnInfo(name = "path")
        @SerializedName("path")
        val path: String,
    ): Parcelable {
        @Ignore
        @IgnoredOnParcel
        val file = File(path)
    }
}

data class MediaWithSlides(
    @Embedded val media: Media,
    @Relation(
        parentColumn = "path",
        entityColumn = "mediaPath"
    )
    val mediaSlides: List<Media.Slide>
) {
    fun toMedia() = media.apply {
        slides = mediaSlides.takeUnless { it.isEmpty() }
    }
}