package com.shop.base.view.cityPicker

import android.content.Context
import android.view.View
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ResourceUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.shop.base.R
import com.shop.base.databinding.DialogCityPickerBinding
import com.shop.base.ext.click
import com.shop.base.view.wheelView.adapter.WheelAdapter

/**
 * 通用选择省市县弹框
 * @author dxl
 * @date 2022-08-08  周一
 * @param defaultSelectedCode 默认选择，编码
 * @param onSelected 选中回调
 */
class CityPickerDialog(
    context: Context,
    private val defaultSelectedCode: List<String> = emptyList(),
    private val onSelected: ((Province, Province.City?, Province.City.Area?) -> Unit)? = null
) : BottomPopupView(context) {

    constructor(context: Context) : this(context, emptyList(), null)

    private lateinit var vb: DialogCityPickerBinding

    companion object {
        fun show(
            context: Context,
            defaultSelectedCode: List<String> = emptyList(),
            onSelected: ((Province, Province.City?, Province.City.Area?) -> Unit)? = null
        ) {
            XPopup.Builder(context)
                .dismissOnBackPressed(true)
                .dismissOnTouchOutside(true)
                .asCustom(CityPickerDialog(context, defaultSelectedCode, onSelected))
                .show()
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_city_picker
    }

    override fun onCreate() {
        super.onCreate()
        vb = DialogCityPickerBinding.bind(popupImplView)
        vb.btnCancel.click { dismiss() }

        //解析数据
        val json = ResourceUtils.readAssets2String("province.json")

        val provinces = JSON.parseArray(json, Province::class.java)
        //默认省份索引
        var defaultProvinceIndex =
            provinces.indexOfFirst { it.code == defaultSelectedCode.getOrNull(0) }

        if (defaultProvinceIndex < 0) {
            defaultProvinceIndex = 0
        }
        setProvinces(provinces, defaultProvinceIndex)

        val cities = provinces.getOrNull(defaultProvinceIndex)?.cityList ?: emptyList()
        //默认市索引
        var defaultCityIndex = cities.indexOfFirst { it.code == defaultSelectedCode.getOrNull(1) }
        if (defaultCityIndex < 0) {
            defaultCityIndex = 0
        }
        setCities(cities, defaultCityIndex)

        val areas = cities.getOrNull(defaultCityIndex)?.areaList ?: emptyList()
        //默认县索引
        var defaultAreaIndex = areas.indexOfFirst { it.code == defaultSelectedCode.getOrNull(2) }
        if (defaultAreaIndex < 0) {
            defaultAreaIndex = 0
        }
        setAreas(areas, defaultAreaIndex)

        vb.btnConfirm.click {
            val province = vb.wvProvince.adapter.getItem(vb.wvProvince.currentItem) as Province
            val city = vb.wvCity.adapter.getItem(vb.wvCity.currentItem) as Province.City?
            val area = vb.wvArea.adapter.getItem(vb.wvArea.currentItem) as Province.City.Area?
            onSelected?.invoke(province, city, area)
            dismiss()
        }

    }


    private fun setProvinces(provinces: List<Province>, selected: Int) {
        vb.wvProvince.adapter = object : WheelAdapter<Province> {
            override fun getItemsCount() = provinces.size

            override fun getItem(index: Int) = provinces[index]

            override fun indexOf(o: Province?) = provinces.indexOf(o)
        }
        vb.wvProvince.currentItem = selected
        vb.wvProvince.setCyclic(false)

        vb.wvProvince.setOnItemSelectedListener {
            val province = provinces[it]
            setCities(province.cityList, 0)
            setAreas(province.cityList[0].areaList, 0)
        }

    }


    private fun setCities(cityList: List<Province.City>, selected: Int) {
        vb.wvCity.adapter = object : WheelAdapter<Province.City> {
            override fun getItemsCount() = cityList.size

            override fun getItem(index: Int) = cityList[index]

            override fun indexOf(o: Province.City?) = cityList.indexOf(o)
        }
        vb.wvCity.currentItem = selected
        vb.wvCity.setCyclic(false)

        vb.wvCity.setOnItemSelectedListener {
            val city = cityList[it]
            setAreas(city.areaList, 0)
        }
    }


    private fun setAreas(areaList: List<Province.City.Area>, selected: Int) {
        vb.wvArea.adapter = object : WheelAdapter<Province.City.Area> {
            override fun getItemsCount() = areaList.size

            override fun getItem(index: Int) = areaList[index]

            override fun indexOf(o: Province.City.Area?) = areaList.indexOf(o)
        }
        vb.wvArea.currentItem = selected
        vb.wvArea.setCyclic(false)

    }


}