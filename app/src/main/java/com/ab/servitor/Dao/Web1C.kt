package com.ab.servitor.Dao

import android.content.Context
import android.widget.Toast
import com.ab.servitor.Data.ProdItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Web1C {
    //Retrofit - для формирования запросов HTTP
    //Moshi - преобразование json в обьекты Kotlin
    //Room - ORM библиотека
    //Dagger - библиотека внедрения зависимостей
    lateinit var productCatalog: List<ProdItem>
    private val api: UtAPI = UtAPI.create()

    fun getProductCatalog(context: Context){
        api.loadProductCatalog()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                productCatalog = result
                Toast.makeText(context, "Загружено ${result.size} штрихкодов", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            })
    }

    fun getProductByGTIN(gtin: String?): ProdItem? {
        return productCatalog.find { prodItem -> prodItem.GTIN == gtin }
    }
}