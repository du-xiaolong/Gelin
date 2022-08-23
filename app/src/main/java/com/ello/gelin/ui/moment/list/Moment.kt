package com.ello.gelin.ui.moment.list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shop.base.ext.notNull
import com.shop.base.util.formatDateString
import com.shop.base.util.parseDate
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
    val headImage: String?,
    val className: String?,
    val typeStr: String?,
    val resource: List<Resource>,
    val timeDiff: String?,
    val createdAt: String?
) : Parcelable {

    val dateStr: String
        get() = createdAt?.parseDate("yyyy-MM-dd HH:mm:ss")?.formatDateString().notNull

    val isVideo: Boolean
        get() = resource.isNotEmpty() && !resource.first().compressUrl.isNullOrEmpty()

    @Parcelize
    data class Content(
        val text: String?
    ) : Parcelable

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
    ) : Parcelable
}

data class PageWrapper<T>(
    val list: List<T>
)
