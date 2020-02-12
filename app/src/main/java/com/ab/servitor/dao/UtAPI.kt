package com.ab.servitor.dao

import com.ab.servitor.data.ProdItem
import com.ab.servitor.data.ProtocolMessage
import com.ab.servitor.data.ScanResult
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

interface UtAPI {
    @GET("{db}/hs/ServitorBeta/v1/getScanCodes")
    fun loadProductCatalog(@Path("db") db: String?): Observable<List<ProdItem>>

    @POST("{db}/hs/ServitorBeta/v1/postProtocolMessage")
    fun postProtocolMessage(@Path("db") db: String?, @Body mess: ProtocolMessage): Observable<ResponseBody>

    @POST("{db}/hs/ServitorBeta/v1/postScanResult")
    fun postScanResult(@Path("db") db: String?, @Body mess: ScanResult): Observable<ResponseBody>

    companion object Factory {
        fun create(srvAddr: String?, timeout: Long): UtAPI {
            val okClient = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor("КопыловАС", "Zx!33abz"))
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(srvAddr ?: "http://srv-1c-1/")
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