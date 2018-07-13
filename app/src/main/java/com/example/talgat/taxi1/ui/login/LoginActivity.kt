package com.example.talgat.taxi1.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.talgat.taxi1.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginActivityViewModel by lazy {
        ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)
    }

    private val TAG: String = this.javaClass.simpleName
    private var codeSent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        viewModel.getState().observe(this, Observer {
            val value = it!!
            Log.e(TAG, "in observer " + value)
            when (value) {
                LoginActivityViewModel.OK -> {
                    Log.e(TAG, "succes")
                    finish()
                }

                LoginActivityViewModel.LOADING -> {
                    showProgressbar(true)
                    hideKeyboard()
                }

                LoginActivityViewModel.AUTH_ERROR -> {
                    showProgressbar(false)
                    verification_code.setError("Неправильно введен код")
                }

                LoginActivityViewModel.CODE_SENT -> {
                    showProgressbar(false)
                    codeSent = true
                    updateUI()
                }
            }
        })

        next_button.setOnClickListener(onNextClickListener)
        sign_in_button.setOnClickListener(onSignInButtonClicked)
    }

    fun validatePhoneNumber():Boolean{
        val phoneNumber = this.field_phone_number.text.toString()
        if (TextUtils.isEmpty(phoneNumber) ||phoneNumber.contains("-")) {
            return false
        }
        return true
    }

    private fun showProgressbar(show: Boolean) {
        login_window.visibility = if (show) View.GONE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        if (this.currentFocus != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
        }
    }


    private fun updateUI() {
        login_form.visibility = if (codeSent) View.GONE else View.VISIBLE
        confirm_form.visibility = if (codeSent) View.VISIBLE else View.GONE
    }


    private var onNextClickListener = View.OnClickListener {
        if (validatePhoneNumber()) {
            val phoneNumber: String = field_phone_number.text.toString()
            viewModel.startPhoneNumberVerification(phoneNumber, this)
        } else {
            field_phone_number.setError("invalid phone number")
        }
    }

    private var onSignInButtonClicked = View.OnClickListener {

        val SMScode: String = verification_code.text.toString()
        if (TextUtils.isEmpty(SMScode)) {
            verification_code.setError("Неправильно введен код")
            return@OnClickListener
        }
        viewModel.verifyPhoneNumberWithCode(SMScode)
    }
}
