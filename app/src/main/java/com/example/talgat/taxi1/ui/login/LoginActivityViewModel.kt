package com.example.talgat.taxi1.ui.login

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivityViewModel : ViewModel() {

    companion object {
        const val LOADING = "LOADING"
        const val AUTH_ERROR = "login_activity_error"
        const val OK = "successfully"
        const val CODE_SENT = "code sent"
    }


    private val TAG: String = this.javaClass.simpleName
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val state = MutableLiveData<String>()

    private var success: Boolean = false
    var verificationId: String? = null

    private var onVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
            if (p0 != null) {
                signInWithPhoneAuthCredential(p0)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException?) {
            Log.e(TAG, "onVerificationFailed", p0)

            state.value = AUTH_ERROR
        }

        override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
            Log.e(TAG, "onCodeSent:" + p0)

            verificationId = p0

            state.value = CODE_SENT
        }
    }

    fun getState(): LiveData<String> {
        return state
    }




    fun startPhoneNumberVerification(phoneNumber: String, activity: Activity) {
        Log.e(TAG, "startVerification")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                onVerificationStateChangedCallbacks
        )

        state.value = LOADING
    }

    fun verifyPhoneNumberWithCode(code: String) {
        val credential: PhoneAuthCredential? = PhoneAuthProvider.getCredential(this.verificationId!!, code)

        signInWithPhoneAuthCredential(credential!!)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    success = it.isSuccessful
                    if (it.isSuccessful) {

                        state.value = OK
                    } else {

                        state.value = AUTH_ERROR
                    }
                }
    }

}