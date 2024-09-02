package com.example.farmconnectadmin.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.farmconnectadmin.Utils
import com.example.farmconnectadmin.Admin
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit


class AuthViewModel : ViewModel() {
    private val _isACurrentUser= MutableStateFlow<Boolean>(false)
    val isACurrentUser=_isACurrentUser
    init {
        Utils.getFirebaseAuthInstance().currentUser?.let {
            _isACurrentUser.value=true
        }
}

    private val _verificationID= MutableStateFlow<String?>(null)
    private val _otpSent= MutableStateFlow<Boolean>(false)
    val otpSent=_otpSent

    private val _isSignedInSuccessfully= MutableStateFlow<Boolean>(false)
    val isSignedInSuccessfully=_isSignedInSuccessfully
    fun sendOTP(userNumber: String, activity: Activity) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Handle verification completed
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Handle verification failed
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationID.value=verificationId
                _otpSent.value=true


            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getFirebaseAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: Admin) {
        val verificationId = _verificationID.value
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            Utils.getFirebaseAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    user.uid=Utils.getCurrentUserId()
                    if (task.isSuccessful) {
                        _isSignedInSuccessfully.value = true
                        FirebaseDatabase.getInstance().getReference("Admin").child("AdminInfo").child(user.uid!!).setValue(user)
                    } else {
                        // Handle sign-in error (e.g., log the error or show a message to the user)
                    }
                }
        } else {
        }
    }

}