package com.ab.servitor.Dao

import android.content.Context
import com.ab.servitor.Data.ProdItem
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.nio.charset.Charset

interface UtAPI {
    @GET("ut/hs/ServitorBeta/v1/getScanCodes")
    fun loadProductCatalog(): Observable<List<ProdItem>>

    companion object Factory {
        fun create(): UtAPI {
            val okClient = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor("КопыловАС", "Zx!33abz"))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://srv-1c-1/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okClient)
                .build()

            return retrofit.create(UtAPI::class.java)
        }
    }
}

class BasicAuthInterceptor(username: String, pass:String): Interceptor {
    private var credent: String = Credentials.basic(username, pass, Charset.forName("UTF-8"))

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credent).build()
        return chain.proceed(request)
    }
}