package com.growcredit.widget.plugin.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.growcredit.widget.plugin.R
import com.growcredit.widget.plugin.api.MainViewModel
import com.growcredit.widget.plugin.interfaces.DialogCallback
import com.growcredit.widget.plugin.utility.Utility
import com.growcredit.widget.plugin.utility.WebAppInterface
import com.plaid.link.Plaid
import com.plaid.link.PlaidHandler
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResultHandler
import com.plaid.link.result.LinkSuccess

class BaseActivty : FragmentActivity(), DialogCallback {

    private lateinit var viewModel: MainViewModel
    private lateinit var webView: WebView
    private lateinit var progressOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val pt = intent.getStringExtra("PARTNER_TOKEN") ?: ""
        val refId = intent.getStringExtra("REF_ID") ?: ""
        val func = intent.getStringExtra("FUNCTION") ?: ""
        Utility.partnerToken = pt
        Utility.userAccRefId = refId

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initializeViews()
        initializeWebView()
        registerObservers()

        when (func) {
            "initOnBoarding" -> initOnBoarding()
            "plaidWidget" -> plaidWidget(refId)
            "resumeWidget" -> resumeWidget(refId)
        }
    }

    private fun registerObservers() {
        // callback for plaid link token
        viewModel.linkToken.observe(this, Observer { data ->
            invokePlaid(data.link_token)
            setViewsVisibility(false)
        })

        viewModel.accessUrl.observe(this, Observer { data ->
            webView.loadUrl(data.url)
        })

        viewModel.accessUrlPlaidOnly.observe(this, Observer { data ->
            val token = Utility.getQueryParam(data.url, "ut").toString()
            Utility.userToken = Utility.decodeBase64(token)
        })

        // callback for loading widget again in web-view after adding bank on GC Api server
        viewModel.addBankCompleteWidget.observe(this, Observer { data ->
            webView.loadUrl(
                Utility.getWidgetUrl() + "?pt=" + Utility.partnerToken + "&ut=" + Utility.encodeToBase64(
                    Utility.userToken
                )
            )
            setViewsVisibility(false)
        })

        // callback for adding bank on GC Api server for plaid widget only
        viewModel.addBankOnly.observe(this, Observer { data ->
            setViewsVisibility(false)
            Utility.showAlertDialog(
                this,
                "Success",
                "You have successfully linked your account",
                "Ok",
                this
            )
        })

        // callback after getting web-view session storage data of user
        viewModel.userData.observe(this, Observer { data ->
            setViewsVisibility(true)
            if (Utility.userAccRefId.isNotEmpty()) {
                plaidWidget(Utility.userAccRefId)
            }
        })
    }


    private fun initializeViews() {
        progressOverlay = findViewById(R.id.progressOverlay)
    }

    private fun initializeWebView() {
        webView = findViewById(R.id.webViewPlugin)

        webView.settings.setJavaScriptEnabled(true)
        webView.settings.domStorageEnabled = true
        webView.settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        webView.addJavascriptInterface(
            WebAppInterface(webView, viewModel),
            "Android"
        )
        WebView.setWebContentsDebuggingEnabled(true)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()

                // Decide whether to allow or block the navigation
                if (url == Utility.SUTTON_BANK_PRIVACY_URL ||
                    url == Utility.BRB_PRIVACY_URL ||
                    url == Utility.BRB_ELCTRONIC_FUNDS_TRANSFER_URL ||
                    url == Utility.MRV_ELECRONIC_FUNDS_TRANSFER_URL
                ) {
                    openBrowser(url)
                    // Block the navigation
                    return true

                } else {
                    return false // Allow the navigation
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                setViewsVisibility(false)
            }
        }
    }

    /* for complete onboarding widget */
    fun plaidWidget(accRefId: String) {
        setViewsVisibility(true)
        val plaidlinkBody = mapOf(
            "account_ref_id" to accRefId,
            "platform" to "android",
            "android_package_name" to "com.growcredit.widget"
        )
        viewModel.getLinkToken(plaidlinkBody)

        val accessUrlBody = mapOf(
            "account_ref_id" to accRefId,
        )
        viewModel.getCustomerAccessUrlPlaidOnly(accessUrlBody)
    }

    fun resumeWidget(accRefId: String) {
        val accessUrlBody = mapOf(
            "account_ref_id" to accRefId,
        )
        viewModel.getCustomerAccessUrl(accessUrlBody)
    }

    fun initOnBoarding() {
        val uid = Utility.generateRandomUid()
        webView.loadUrl(
            Utility.getWidgetUrl() + "?pt=" + Utility.partnerToken + "&uid=" + uid +
                    "&onboarding_source=android-web"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!linkResultHandler.onActivityResult(requestCode, resultCode, data)) {
            // Not handled by the LinkResultHandler
        }
    }

    val linkResultHandler = LinkResultHandler(
        onSuccess = { result: LinkSuccess ->

            setViewsVisibility(true)
            val publicToken = result.publicToken
            val metaDataJson = result.metadata.metadataJson
            val metadata = result.metadata
            val account = metadata.accounts[0]
            val accountId = account.id
            val accountName = account.name
            val accountMask = account.mask
            val accountSubType = account.subtype

            val institutionId = metadata.institution?.id
            val institutionName = metadata.institution?.name

            val body = mapOf(
                "account_ref_id" to Utility.userAccRefId,
                "plaid_public_token" to publicToken,
                "plaid_account_id" to accountId,
                "bank_account_mask" to accountMask,
                "bank_account_name" to accountName,
                "institution_name" to institutionName
            )

            if (Utility.custCred.isEmpty()) // this is for only plaid widget case. In this scenario web-view is not utilized and custCred will be empty
            {
                viewModel.addBankOnly(body)
            } else {
                viewModel.addBankForCompleteWidget(body)
            }
            sendMetaData(metaDataJson)

        },
        onExit = { it: LinkExit ->
            val metaDataJson = it.metadata.metadataJson
            if (metaDataJson != null) {
                sendMetaData(metaDataJson)
            }
            if (Utility.custCred.isEmpty()) // this is for only plaid widget case. In this scenario web-view is not utilized and custCred will be empty
            {
                onPositiveButtonClick()
            }
        },
    )

    private fun sendMetaData(metaData: String) {
        val gson = Gson()
        val payload = mapOf(
            "bank_metadata" to metaData
        )
        val metaDataPayload = gson.toJson(payload)
        val metaDatabody = mapOf(
            "payload" to metaDataPayload,
            "url" to "/api/post_register/bank/meta/log",
            "method" to "post",
            "token" to Utility.userToken
        )
        viewModel.logMetaData(metaDatabody)

    }

    private fun invokePlaid(linkToken: String) {
        val linkTokenConfiguration = linkTokenConfiguration {
            token = linkToken
        }

        val plaidHandler: PlaidHandler =
            Plaid.create(application, linkTokenConfiguration)

        plaidHandler.open(this)
    }

    override fun onPositiveButtonClick() {
        finish()
    }

    private fun setViewsVisibility(isPVisible: Boolean) {
        if (isPVisible) {
            progressOverlay.visibility = View.VISIBLE
        } else {
            progressOverlay.visibility = View.GONE
        }
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}