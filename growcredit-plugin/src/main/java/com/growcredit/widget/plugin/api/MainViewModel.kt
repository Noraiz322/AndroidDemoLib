package com.growcredit.widget.plugin.api

import android.webkit.WebView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.growcredit.widget.plugin.models.AccessUrlResponse
import com.growcredit.widget.plugin.models.DataResponse
import com.growcredit.widget.plugin.models.PlaidLinkResponse
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    private val _linkToken = MutableLiveData<PlaidLinkResponse>()
    val linkToken: LiveData<PlaidLinkResponse> get() = _linkToken

    private val _accessUrlPlaidOnly = MutableLiveData<AccessUrlResponse>()
    val accessUrlPlaidOnly: LiveData<AccessUrlResponse> get() = _accessUrlPlaidOnly

    private val _accessUrl = MutableLiveData<AccessUrlResponse>()
    val accessUrl: LiveData<AccessUrlResponse> get() = _accessUrl

    private val _addBankCompleteWidget = MutableLiveData<Boolean>()
    val addBankCompleteWidget: LiveData<Boolean> get() = _addBankCompleteWidget

    private val _addBankOnly = MutableLiveData<Boolean>()
    val addBankOnly: LiveData<Boolean> get() = _addBankOnly

    private val _logMetaData = MutableLiveData<DataResponse>()
    val logMetaData: LiveData<DataResponse> get() = _logMetaData

    private val _userData = MutableLiveData<Boolean>()
    val userData: LiveData<Boolean> get() = _userData

    fun getLinkToken(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.getPlaidLinkToken(body)
            result?.let {
                _linkToken.value = it
            }
        }
    }

    fun getCustomerAccessUrl(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.getCustomerAccessUrl(body)
            result?.let {
                _accessUrl.value = it
            }
        }
    }

    fun getCustomerAccessUrlPlaidOnly(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.getCustomerAccessUrl(body)
            result?.let {
                _accessUrlPlaidOnly.value = it
            }
        }
    }

    fun addBankForCompleteWidget(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.addBank(body)
            result?.let {
                _addBankCompleteWidget.value = it
            }
        }
    }

    fun getUserToken(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.getUserToken(body)
            result?.let {
                _addBankCompleteWidget.value = it
            }
        }
    }

    fun addBankOnly(body: Map<String, String?>) {
        viewModelScope.launch {
            val result = repository.addBank(body)
            result?.let {
                _addBankOnly.value = it
            }
        }
    }

    fun logMetaData(body: Map<String, Any>) {
        viewModelScope.launch {
            val result = repository.metaDataLog(body)
            result?.let {
                _logMetaData.value = it
            }
        }
    }

    fun webViewSessionStorage(webView: WebView) {
        viewModelScope.launch {
            val result = repository.fetchWebViewSessionStorage(webView)
            _userData.value = result

        }
    }
}