package com.ello.gelin.ui.moment.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ello.gelin.R
import com.ello.gelin.databinding.ItemMomentImageBinding
import com.ello.gelin.databinding.ItemMomentVideoBinding
import com.ello.gelin.ui.imagePreview.ImagePreviewActivity
import com.ello.gelin.ui.moment.detail.MomentDetailActivity
import com.shop.base.ext.ImageOptions
import com.shop.base.ext.clickAnim
import com.shop.base.ext.lllog
import com.shop.base.ext.loadImage
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
class MomentListAdapter @Inject constructor(@ActivityContext val context: Context) :
    PagingDataAdapter<Moment, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val PAYLOAD_UPDATE_SINGER_FOLLOW_STATE = "payload_update_singer_follow_state"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Moment>() {
            override fun areItemsTheSame(oldItem: Moment, newItem: Moment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Moment, newItem: Moment): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemViewType = getItemViewType(position)
        if (itemViewType == 0) {
            //视频
            (holder as MomentVideoViewHolder).bind(item)
        } else {
            //图片
            (holder as MomentImageViewHolder).bind(item)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            //视频
            return MomentVideoViewHolder(
                ItemMomentVideoBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
        //图片
        return MomentImageViewHolder(
            ItemMomentImageBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        lllog("当前判断：${item?.resource?.firstOrNull()}")
        if (item?.isVideo == true) {
            return 0
        }

        return 1
    }

    inner class MomentImageViewHolder(private val vb: ItemMomentImageBinding) :
        RecyclerView.ViewHolder(vb.root) {
        fun bind(moment: Moment) {
            vb.ivAvatar.loadImage(url = moment.headImage)
            vb.tvTitle.text = moment.content?.text
            setIsRecyclable(false)
            vb.tvDescription.text = "${moment.className} | ${moment.typeStr} | ${moment.timeDiff}"
            vb.rvImages.adapter = object : BaseQuickAdapter<Moment.Resource, BaseViewHolder>(
                R.layout.item_moment_single_image,
                moment.resource.toMutableList()
            ) {
                override fun convert(holder: BaseViewHolder, item: Moment.Resource) {
                    holder.getView<ImageView>(R.id.imageView)
                        .loadImage(context = context, url = item.url)
                }
            }.apply {
                setOnItemClickListener { adapter, view, position ->
                    ImagePreviewActivity.start(context, data, position)
                }
            }
        }
    }

    inner class MomentVideoViewHolder(private val vb: ItemMomentVideoBinding) :
        RecyclerView.ViewHolder(vb.root) {
        fun bind(moment: Moment) {
            val resource = moment.resource.firstOrNull() ?: return
            vb.ivAvatar.loadImage(url = moment.headImage, imageOptions = ImageOptions())
            vb.tvTitle.text = moment.content?.text
            vb.tvDescription.text = "${moment.className} | ${moment.typeStr} | ${moment.timeDiff}"
            vb.ivCover.loadImage(url = resource.coverImg)
            listOf(vb.ivCover, vb.ivPlay).forEach {
                it.clickAnim { MomentDetailActivity.start(context, moment) }
            }
        }
    }


}