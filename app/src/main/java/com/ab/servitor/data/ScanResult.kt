package com.ab.servitor.data

import android.util.Log
import androidx.databinding.*
import com.ab.servitor.BR
import com.squareup.moshi.Json
import java.util.*

class ScanResult: BaseObservable() {
    @Json (name = "oper") private var _oper: String = ""
    fun getOper():String = _oper
    @Bindable
    fun setOper(value:String?){
        if (_oper!=value && value!=null) {
            _oper = value
            notifyPropertyChanged(BR.oper)
        }
    }

    @Json(name = "deviceID") private var _deviceID: String = ""
    @Bindable
    fun getDeviceID(): String = _deviceID
    fun setDeviceID(value: String?){
        if (_deviceID!=value && value!=null){
            _deviceID = value
            notifyPropertyChanged(BR.deviceID)
        }
    }

    @Json(name = "devicePrefix") private var _devicePrefix: String = "eva0"
    @Bindable
    fun getDevicePrefix(): String = _devicePrefix
    fun setDevicePrefix(value: String?){
        if (value!= null && value!=_devicePrefix) {
            _devicePrefix = value
            notifyPropertyChanged(BR.devicePrefix)
        }
    }

    @Json(name = "gtin") private var _gtin: String = ""
    @Bindable
    fun getGtin(): String = _gtin
    fun setGtin(value: String?){
        if (value!= _gtin && value != null){
            _gtin=value
            notifyPropertyChanged(BR.gtin)
        }
    }

    @Json(name = "dateProd") private var _dateProd: Int = 1
    @Bindable
    fun getDateProd(): String = _dateProd.toString()
    fun setDateProd(value: String?){
        Log.d("[BD]", "setDateProd raw value: $value Converted: ${value?.toInt()}")
        if (value!=null && value.toInt()!=_dateProd)
            _dateProd = value.toInt()
    }
    fun setDateProd(value: Int){
        if (value!=_dateProd){
            _dateProd = value
            notifyPropertyChanged(BR.dateProd)
        }
    }

    @Json(name = "kol") private var _kol: Float = 0.0f
    @Bindable
    fun getKol(): String = _kol.toString()
    fun setKol(value: String?){
        Log.d("[BD]", "setKol raw value: $value Converted: ${value?.toFloat()}")
        if (value!= null && value.toFloat()!=_kol)
            _kol = value.toFloat()
    }

    fun setKol(value: Float){
        if (value!=_kol){
            _kol=value
            notifyPropertyChanged(BR.kol)
        }
    }

    @Json(name = "name")
    private var _name: String = ""
    @Bindable
    fun getName(): String = _name
    fun setName(value: String?){
        if (value!=null && value!=_name){
            _name = value
            notifyPropertyChanged(BR.name)
        }
    }

    private val _id: String = UUID.randomUUID().toString()
}