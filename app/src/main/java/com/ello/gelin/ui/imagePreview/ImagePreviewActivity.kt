package com.ello.gelin.ui.imagePreview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ello.gelin.R
import com.ello.gelin.databinding.ActivityImagePreviewBinding
import com.ello.gelin.ui.moment.list.Moment
import com.ello.gelin.utils.UmengUtil
import com.gyf.immersionbar.ktx.immersionBar
import com.lxj.xpopup.photoview.PhotoView
import com.shop.base.common.BaseDbActivity
import com.shop.base.common.BaseViewModel
import com.shop.base.ext.Params
import com.shop.base.ext.click
import com.shop.base.ext.toastCenter

/**
 * 照片详情查看
 * @author dxl
 * @date 2021/10/13
 */
@SuppressLint("NotifyDataSetChanged, SetTextI18n")
class ImagePreviewActivity :
    BaseDbActivity<BaseViewModel, ActivityImagePreviewBinding>(R.layout.activity_image_preview) {

    private val moment by Params<Moment>(KEY_MOMENT)
    private val index by Params<Int>(KEY_ALBUM_INDEX)

    companion object {
        const val KEY_ALBUM_INDEX = "key_album_index"
        const val KEY_MOMENT = "key_moment"


        fun startMoment(context: Context, moment: Moment, index: Int) {
            context.startActivity(Intent(context, ImagePreviewActivity::class.java).apply {
                putExtra(KEY_MOMENT, moment)
                putExtra(KEY_ALBUM_INDEX, index)
            })
        }
    }

    //当前页面索引
    private var currentPage = 0
        set(value) {
            field = value
            vb.tvCurrentPage.text = "${value + 1}/${vb.recyclerView.adapter?.itemCount}"
        }


    //页面适配器
    private val pagerAdapter by lazy { AlbumPagerAdapter() }


    //小图缩略图
    private val recyclerViewAdapter by lazy {
        RecyclerViewAdapter().apply {
            setOnItemClickListener { _, _, position ->
                currentPage = position
                notifyDataSetChanged()
                vb.viewPager.currentItem = currentPage
            }
        }
    }


    override fun init(savedInstanceState: Bundle?) = initView()


    private fun initView() {
        val resource = moment?.resource
        if (resource.isNullOrEmpty()) {
            "无相册".toastCenter()
            return
        }

        vb.viewPager.adapter = pagerAdapter.apply { setList(resource) }

        vb.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                vb.recyclerView.smoothScrollToPosition(position)
                vb.recyclerView.adapter?.notifyDataSetChanged()
            }
        })
        vb.viewPager.offscreenPageLimit = 2

        vb.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        vb.recyclerView.adapter = recyclerViewAdapter.apply { setList(resource) }

        currentPage = intent.getIntExtra(KEY_ALBUM_INDEX, 0)
        vb.viewPager.setCurrentItem(currentPage, false)

        vb.ivClose.click { onBackPressed() }
        vb.ivMore.click { onMoreClicked() }


    }

    override fun initImmersionBar() {
        //设置沉浸式样式
        immersionBar {
            keyboardEnable(true, getKeyboardMode())
            statusBarDarkFont(false)
            navigationBarColor(R.color.white)
            navigationBarDarkIcon(true)
        }
    }

    //大图适配器
    class AlbumPagerAdapter : BaseQuickAdapter<Moment.Resource, BaseViewHolder>(0) {

        override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

            val imageView = PhotoView(context)
            imageView.setBackgroundColor(Color.BLACK)
            imageView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

            return createBaseViewHolder(imageView)
        }

        override fun convert(holder: BaseViewHolder, item: Moment.Resource) {
            val imageView = holder.itemView as ImageView

            Glide.with(context).load(item.url).into(imageView)
        }

    }


    //小缩略图
    inner class RecyclerViewAdapter() :
        BaseQuickAdapter<Moment.Resource, BaseViewHolder>(R.layout.item_check_album_small) {

        override fun convert(holder: BaseViewHolder, item: Moment.Resource) {
            Glide.with(context).load(item.url).into(holder.getView(R.id.ivImage))
            val drawable = holder.itemView.background

            val checked = holder.bindingAdapterPosition == currentPage

            if (drawable is GradientDrawable) {
                drawable.setColor(if (checked) Color.WHITE else Color.GRAY)
            }

            holder.getView<ImageView>(R.id.ivImage).alpha = if (checked) 1.0f else 0.7f


        }

    }

    //点击更多
    private fun onMoreClicked() {
        //分享
        moment?:return
        val url = moment?.resource?.get(vb.viewPager.currentItem)?.url?:return

        UmengUtil.shareImageToWechat(
            this,
            moment!!.content?.text,
            "",
            url
        )

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_anim, R.anim.a3)
    }

}