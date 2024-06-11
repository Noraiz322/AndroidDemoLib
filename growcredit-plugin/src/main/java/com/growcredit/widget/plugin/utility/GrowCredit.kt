package com.growcredit.widget.plugin.utility

import android.content.Context
import android.content.Intent
import com.growcredit.widget.plugin.activities.BaseActivty

object GrowCredit {
    fun invokeWidget(context: Context, partnerToken: String, testEnvironment : Boolean = false) {
        val intent = Intent(context, BaseActivty::class.java)
        Utility.isTestEnvironment = testEnvironment
        intent.putExtra("PARTNER_TOKEN", partnerToken)
        intent.putExtra("FUNCTION", "initOnBoarding")
        context.startActivity(intent)
    }

    fun invokeBankWidget(context: Context, partnerToken: String, accountReferenceId: String, testEnvironment : Boolean = false) {
        Utility.custCred = "" // if custCred are in memory earlier then need to clear it for plaid only widget to finish the process after plaid connectivity
        val intent = Intent(context, BaseActivty::class.java)
        Utility.isTestEnvironment = testEnvironment
        intent.putExtra("PARTNER_TOKEN", partnerToken)
        intent.putExtra("REF_ID", accountReferenceId)
        intent.putExtra("FUNCTION", "plaidWidget")
        context.startActivity(intent)
    }

    fun resumeWidget(context: Context, partnerToken: String, accountReferenceId: String, testEnvironment : Boolean = false) {
        val intent = Intent(context, BaseActivty::class.java)
        Utility.isTestEnvironment = testEnvironment
        intent.putExtra("PARTNER_TOKEN", partnerToken)
        intent.putExtra("REF_ID", accountReferenceId)
        intent.putExtra("FUNCTION", "resumeWidget")
        context.startActivity(intent)
    }

}