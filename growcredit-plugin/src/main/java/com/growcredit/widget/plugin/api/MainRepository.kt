package com.growcredit.widget.plugin.api

import android.webkit.WebView
import com.growcredit.widget.plugin.models.AccessUrlResponse
import com.growcredit.widget.plugin.models.DataResponse
import com.growcredit.widget.plugin.models.PlaidLinkResponse
import com.growcredit.widget.plugin.utility.Utility
import kotlinx.coroutines.delay


class MainRepository {
    suspend fun getPlaidLinkToken(body: Map<String, String?>): PlaidLinkResponse? {
        val response = ApiServiceFactory.apiService.plaidLinkToken(body)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getCustomerAccessUrl(body: Map<String, String?>): AccessUrlResponse? {
        val response = ApiServiceFactory.apiService.customerAccessUrl(body)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun addBank(body: Map<String, String?>): Boolean? {
        val response = ApiServiceFactory.apiService.addBank(body)
        return if (response) {
            response
        } else {
            null
        }
    }

    suspend fun getUserToken(body: Map<String, String?>): Boolean? {
        val response = ApiServiceFactory.apiService.addBank(body)
        return if (response) {
            response
        } else {
            null
        }
    }

    suspend fun metaDataLog(body: Map<String, Any>): DataResponse? {
            val response = ApiServiceFactory.apiService.metaDataLog(body)
            return if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        return null
    }

    suspend fun fetchWebViewSessionStorage(webView: WebView): Boolean {
        webView.post(kotlinx.coroutines.Runnable {
            webView.evaluateJavascript("(function() { return JSON.stringify(window.sessionStorage); })();") { value ->
                // Handle the result of JavaScript evaluation
                val sessionStorageJson = value?.replace("\"", "")
                val sessionStorageMap = mutableMapOf<String, String>()
                if (!sessionStorageJson.isNullOrBlank()) {
                    val keyValuePairs = sessionStorageJson.split(",")
                    for (pair in keyValuePairs) {
                        val keyValue = pair.split(":")
                        if (keyValue.size == 2) {
                            val key = keyValue[0]
                            val value = keyValue[1]
                            sessionStorageMap[key] = value
                        }
                    }
                }
                val utParts = (sessionStorageMap.getValue("\\user_token\\")).split("\\")
                Utility.userToken = utParts[1]
                val ccParts = (sessionStorageMap.getValue("\\cc\\")).split("\\")
                Utility.custCred = ccParts[1]
                val uidParts = (sessionStorageMap.getValue("\\uid\\")).split("\\")
                Utility.userAccRefId = uidParts[1]
            }
        })
        delay(30)
        return true
    }
}