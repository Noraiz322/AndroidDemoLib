package com.growcredit.widget.plugin.api

import com.growcredit.widget.plugin.utility.Utility
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.bouncycastle.jce.provider.BrokenPBE.Util
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    lateinit var retrofit: Retrofit
    val dynamicHeaders = mapOf(
        "Content-Type" to "application/json",
        "Accept" to "application/json",
        "Authorization" to "Bearer " + Utility.partnerToken
    )
    val client = OkHttpClient.Builder()
        .addInterceptor(DynamicHeaderInterceptor(dynamicHeaders))
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()


    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utility.getApiUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    class DynamicHeaderInterceptor(private val headers: Map<String, String>) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val requestBuilder = chain.request().newBuilder()
            headers.forEach { (name, value) ->
                requestBuilder.addHeader(name, value)
            }
            return chain.proceed(requestBuilder.build())
        }
    }

}