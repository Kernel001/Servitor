package com.ab.servitor.dao

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.ab.servitor.data.ProdItem
import com.ab.servitor.data.ProtocolMessage
import com.ab.servitor.data.ScanResult
import com.ab.servitor.util.GlobalStatus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

object Web1C {
    private var productCatalog: List<ProdItem>? = null
    private var api: UtAPI? = null
    private var srvAddr = ""
    private var dbName = ""

    fun init(context: Context) {
        srvAddr = PreferenceManager.getDefaultSharedPreferences(context).getString("c1_addr", "ut")?:""
        dbName = PreferenceManager.getDefaultSharedPreferences(context).getString("c1_db", "ut")?:""

        api = UtAPI.create(srvAddr)
    }

    fun getProductCatalog(context: Context){
        api?.loadProductCatalog(dbName)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ result ->
                productCatalog = result
                Toast.makeText(context, "Загружено ${result.size} штрихкодов", Toast.LENGTH_SHORT).show()
                GlobalStatus.lastError.set("")
                GlobalStatus.http1CConnectio.set(true)
            }, { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                GlobalStatus.lastError.set(error.message)
                GlobalStatus.http1CConnectio.set(false)
            })
    }

    fun getProductByGTIN(gtin: String?): ProdItem? {
        return productCatalog?.find { prodItem -> prodItem.GTIN == gtin }
    }

    fun postProtocolMessage(context:Context, message:String){
        val deviceId = "${PreferenceManager.getDefaultSharedPreferences(context).getString("terminalPrefix", "")}-${PreferenceManager.getDefaultSharedPreferences(context).getString("terminalID", "")}"
        val protocolMes = ProtocolMessage(deviceId, message)
        api?.postProtocolMessage(dbName, protocolMes)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe ({
                Log.d("Web1C", "Protocol log sended...")
                GlobalStatus.lastError.set("ОК")
                GlobalStatus.http1CConnectio.set(true)
            },
                {
                    error -> GlobalStatus.lastError.set(error.message)
                    GlobalStatus.http1CConnectio.set(false)
                })
    }

    fun postScanResult(context: Context, scanResult: ScanResult): Observable<ResponseBody>? {
        return api?.postScanResult(dbName, scanResult)?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
    }
}