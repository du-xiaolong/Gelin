package com.ello.gelin

import android.os.Bundle
import android.widget.RadioButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ello.gelin.databinding.ActivityMainBinding
import com.ello.gelin.ui.moment.list.MomentListFragment
import com.ello.gelin.user.UserManager
import com.shop.base.common.BaseDbActivity
import com.shop.base.common.BaseViewModel
import com.shop.base.ext.toastCenter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import dagger.hilt.android.AndroidEntryPoint

/**
 *
 */
@AndroidEntryPoint
class MainActivity : BaseDbActivity<BaseViewModel, ActivityMainBinding>(R.layout.activity_main) {

    override fun init(savedInstanceState: Bundle?) {

        UserManager.dlcCookie = "version=1; wx_appid=wx92851927c1361928; wx92851927c1361928_open_id=oAL_I0bZW7ORMnQ4auPyCdVI7w8s; wx92851927c1361928_uid=6393407; wx92851927c1361928_primaryIdentity=2; wx92851927c1361928_subIdentity=1; wx92851927c1361928_isCompleteChild=2; wx92851927c1361928_wx_school_id=11436; isWristBandTemSchool=0; cainfo={\"ca_s\":\"self\",\"ca_n\":\"self\",\"platform\":\"web\"}; 1d1d_session=v1NuOonlJDRb5WfnVYjL7PjgJNq5plygE2509kaG; wx92851927c1361928_role_id=10497664; wx92851927c1361928_student_id=6610944; wx92851927c1361928_encRoleid=ODhlMTcxYTFAfEAxMDQ5NzY2NA==; wx92851927c1361928_class_id=263706; wx92851927c1361928_pass_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdCI6InVzZXIiLCJnaWQiOiIxMDQ5NzY2NCIsInVhbiI6IiIsImx0IjozLCJpc3MiOiJZRFlEIiwic3RpZCI6NjYxMDk0NCwicmlkIjoxMDQ5NzY2NCwiZ3QiOjIsInNpZCI6MTE0MzYsInN2dCI6MSwidWlkIjo2MzkzNDA3LCJfc2QiOiIrTkJRRmtJK1pXLzdUV1FseEpYTmp6cG1QMk9FZCs1MkZ6eWUyMFluNXRJPSIsInNpIjoxLCJ1biI6IuadnOaZk-m-mSIsInBpIjoyLCJ0bSI6MTY1OTQ4NTU0OSwic24iOiLkuLTmsoLluILmrYzmnpflubzlhL_lm60iLCJybiI6IuadnOaZk-m-mSIsInBsIjoiaGVhZG1hc3RlciIsImV4cCI6MTY2MjA3NzU0OSwiY2lkIjoyNjM3MDZ9.YYgx8G7QPlnvYtBegzxdNRngj4YRqIySLZZ_McYttcGJzqebsL7iDXf7338tuHOJ49U4gaWRoSWL9F86arQWpA"
        UserManager.dytCookie = "version=1; wx_appid=wx92851927c1361928; wx92851927c1361928_open_id=oAL_I0bZW7ORMnQ4auPyCdVI7w8s; wx92851927c1361928_uid=6393407; wx92851927c1361928_primaryIdentity=2; wx92851927c1361928_subIdentity=1; wx92851927c1361928_isCompleteChild=2; wx92851927c1361928_wx_school_id=11436; isWristBandTemSchool=0; cainfo={\"ca_s\":\"self\",\"ca_n\":\"self\",\"platform\":\"web\"}; 1d1d_session=v1NuOonlJDRb5WfnVYjL7PjgJNq5plygE2509kaG; wx92851927c1361928_role_id=10497664; wx92851927c1361928_student_id=6610944; wx92851927c1361928_encRoleid=ODhlMTcxYTFAfEAxMDQ5NzY2NA==; wx92851927c1361928_class_id=263706; wx92851927c1361928_pass_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdCI6InVzZXIiLCJnaWQiOiIxMDQ5NzY2NCIsInVhbiI6IiIsImx0IjozLCJpc3MiOiJZRFlEIiwic3RpZCI6NjYxMDk0NCwicmlkIjoxMDQ5NzY2NCwiZ3QiOjIsInNpZCI6MTE0MzYsInN2dCI6MSwidWlkIjo2MzkzNDA3LCJfc2QiOiIrTkJRRmtJK1pXLzdUV1FseEpYTmp6cG1QMk9FZCs1MkZ6eWUyMFluNXRJPSIsInNpIjoxLCJ1biI6IuadnOaZk-m-mSIsInBpIjoyLCJ0bSI6MTY1OTQ4NTU0OSwic24iOiLkuLTmsoLluILmrYzmnpflubzlhL_lm60iLCJybiI6IuadnOaZk-m-mSIsInBsIjoiaGVhZG1hc3RlciIsImV4cCI6MTY2MjA3NzU0OSwiY2lkIjoyNjM3MDZ9.YYgx8G7QPlnvYtBegzxdNRngj4YRqIySLZZ_McYttcGJzqebsL7iDXf7338tuHOJ49U4gaWRoSWL9F86arQWpA"


        vb.viewPager.adapter = object : FragmentStateAdapter(this) {

            val data = vb.rg.children.map { it as RadioButton }.toList()

            override fun getItemCount() = data.size

            override fun createFragment(position: Int): Fragment {
                return MomentListFragment.newInstance(data[position].text.toString())
            }
        }
        vb.viewPager.isUserInputEnabled = false
        vb.rg.setOnCheckedChangeListener { group, checkedId ->
            var checked = -1
            group.children.forEachIndexed { index, view ->
                view as RadioButton
                if (checkedId == view.id) {
                    checked = index
                    view.textSize = 23f
                }else {
                    view.textSize = 18f
                }
            }
            vb.viewPager.currentItem = checked
        }
        vb.rg.check(vb.rg.getChildAt(0).id)
    }


    var lastBackTime = 0L
    override fun onBackPressed() {
        //视频如果全屏播放，退出全屏
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }

        if (System.currentTimeMillis() - lastBackTime > 1000L) {
            lastBackTime = System.currentTimeMillis()
            "再按一次返回键退出应用~".toastCenter()
        } else {
            lastBackTime = 0L
            super.onBackPressed()
        }
    }

}