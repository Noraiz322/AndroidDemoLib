package com.growcredit.widget.plugin.utility

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.growcredit.widget.plugin.api.MainViewModel

class WebAppInterface(var webview: WebView, var viewModel: MainViewModel) {
    @JavascriptInterface
     fun invokePlaid(token: String) {
        viewModel.webViewSessionStorage(webview)
    }
}