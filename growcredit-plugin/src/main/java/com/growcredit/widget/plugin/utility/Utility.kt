package com.growcredit.widget.plugin.utility

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.growcredit.widget.plugin.interfaces.DialogCallback
import java.security.SecureRandom
import java.util.*

object Utility {
    private const val testWidgetBaseUrl = "https://test.onboard.growcredit.com/"
    private const val widgetBaseUrl = "https://onboard.growcredit.com/"
    private const val testApiBaseUrl = "https://testapi.growcredit.com/"
    private const val apiBaseUrl = "https://api.growcredit.com/"
    var userAccRefId = ""
    var partnerToken = ""
    var userToken = ""
    var custCred = ""
    var isTestEnvironment = false

    const val BRB_PRIVACY_URL = "https://growcredit-assets.s3.us-west-2.amazonaws.com/Agreements/BRB+Privacy-Disclosure.23.06.30.14.33.14.pdf";
    const val BRB_ELCTRONIC_FUNDS_TRANSFER_URL = "https://growcredit-assets.s3.us-west-2.amazonaws.com/Agreements/BRB+EFT+Auth+7.2023_final.pdf";
    const val MRV_ELECRONIC_FUNDS_TRANSFER_URL = "https://growcredit-assets.s3.us-west-2.amazonaws.com/Agreements/MRV+EFT+Auth+7.2023_final.pdf";
    const val SUTTON_BANK_PRIVACY_URL = "https://www.suttonbank.com/_/kcms-doc/85/49033/WK-Privacy-Disclosure-1218.pdf";

    fun generateRandomUid(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun getQueryParam(url: String, param: String): String? {
        val uri = java.net.URI(url)
        val query = uri.query
        val pairs = query.split("&")

        for (pair in pairs) {
            val keyValue = pair.split("=")
            if (keyValue[0] == param) {
                return keyValue[1]
            }
        }
        return null
    }

    fun decodeBase64(encodedString: String): String {
        val decodedBytes = Base64.getDecoder().decode(encodedString)
        return String(decodedBytes)
    }

    fun encodeToBase64(input: String): String {
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(input.toByteArray())
    }

    fun getApiUrl() : String{
        return if (isTestEnvironment) {
            testApiBaseUrl
        } else {
            apiBaseUrl
        }
    }

    fun getWidgetUrl() : String{
        return if (isTestEnvironment) {
            testWidgetBaseUrl
        } else {
            widgetBaseUrl
        }
    }
    fun showAlertDialog(context: Context, title: String, message:String, pButton:String, callback: DialogCallback) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(pButton) { dialog, which ->
                // Call the callback method
                callback.onPositiveButtonClick()
            }
        val alertDialog = builder.create()
        alertDialog.show()
}
}