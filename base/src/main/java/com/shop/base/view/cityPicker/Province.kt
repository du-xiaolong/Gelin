package com.shop.base.view.cityPicker

import com.shop.base.view.wheelView.interfaces.IPickerViewData

/**
 * @author dxl
 * @date 2022-08-08  周一
 */
data class Province(
    val code: String,
    val name: String,
    val cityList: List<City> = emptyList()
) : IPickerViewData {
    data class City(
        val code: String,
        val name: String,
        val areaList: List<Area> = emptyList()
    ):IPickerViewData {

        override fun getPickerViewText(): String {
            return name
        }

        data class Area(
            val code: String,
            val name: String
        ):IPickerViewData {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Area

                if (code != other.code) return false

                return true
            }

            override fun hashCode(): Int {
                return code.hashCode()
            }

            override fun getPickerViewText(): String {
                return name
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as City

            if (code != other.code) return false

            return true
        }

        override fun hashCode(): Int {
            return code.hashCode()
        }


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Province

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun getPickerViewText(): String {
        return name
    }

}