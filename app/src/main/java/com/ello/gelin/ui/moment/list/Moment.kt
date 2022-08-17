package com.ello.gelin.ui.moment.list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
@Parcelize
data class Moment(
    val id: Int,
    val displayName: String?,
    val content: Content?,
    val headImage:String?,
    val className:String?,
    val typeStr:String?,
    val resource: List<Resource>,
    val timeDiff: String?,
) :Parcelable{
    val isVideo: Boolean
        get() = resource.isNotEmpty() && !resource.first().compressUrl.isNullOrEmpty()

    @Parcelize
    data class Content(
        val text: String?
    ):Parcelable

    @Parcelize
    data class Resource(
        val url: String?,
        @SerializedName("compress_url")
        val compressUrl: String?,
        @SerializedName("cover_img")
        val coverImg: String?,
        @SerializedName("cover_img_width")
        val coverImgWidth: Int,
        @SerializedName("cover_img_height")
        val coverImgHeight: Int
    ):Parcelable
}

data class PageWrapper<T>(
    val list: List<T>
)
