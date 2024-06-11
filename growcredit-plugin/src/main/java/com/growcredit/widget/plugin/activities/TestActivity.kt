package com.growcredit.widget.plugin.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.growcredit.widget.plugin.R
import com.growcredit.widget.plugin.utility.GrowCredit

class TestActivity : FragmentActivity() {
    lateinit var etPartnerToken: EditText
    lateinit var etReferenceId: EditText
    lateinit var btnInvokeCompleteWidget: Button
    lateinit var btnInvokePlaidWidget: Button
    lateinit var btnIncompleteWidget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)

        initializeViews()
        clickHandlers()
    }

    fun initializeViews() {
        etPartnerToken = findViewById(R.id.etPartnerToken)
        etReferenceId = findViewById(R.id.etReferenceId)
        btnInvokeCompleteWidget = findViewById(R.id.btnInvokeCompleteWidget)
        btnInvokePlaidWidget = findViewById(R.id.btnInvokePlaidWidget)
        btnIncompleteWidget = findViewById(R.id.btnIncompleteWidget)
    }

    fun clickHandlers() {
        btnInvokeCompleteWidget.setOnClickListener(View.OnClickListener {
            GrowCredit.invokeWidget(this, etPartnerToken.text.toString(), true)
        })
        btnInvokePlaidWidget.setOnClickListener(View.OnClickListener {
            GrowCredit.invokeBankWidget(this, etPartnerToken.text.toString(), etReferenceId.text.toString(), true)
        })
        btnIncompleteWidget.setOnClickListener(View.OnClickListener {
            GrowCredit.resumeWidget(this, etPartnerToken.text.toString(), etReferenceId.text.toString(), true)
        })
    }
}