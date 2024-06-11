package com.growcredit.widget.plugin.api

import com.growcredit.widget.plugin.models.AccessUrlResponse
import com.growcredit.widget.plugin.models.DataResponse
import com.growcredit.widget.plugin.models.PlaidLinkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("v1/customer/plaid/bank-select/")
    suspend fun addBank(@Body body: Map<String, String?>): Boolean

    @POST("v1/customer/plaid/link-token/")
    suspend fun plaidLinkToken(@Body body: Map<String, String?>): Response<PlaidLinkResponse>

    @POST("v1/customer/access/url/")
    suspend fun customerAccessUrl(@Body body: Map<String, String?>): Response<AccessUrlResponse>

    @POST("v1/widget/api/request/")
    @JvmSuppressWildcards
    suspend fun metaDataLog(@Body body: Map<String, Any>): Response<DataResponse>
}