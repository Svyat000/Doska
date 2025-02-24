package com.sddrozdov.doska.accountHelper

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.constans.FirebaseAuthConstants

class AccountHelperEmailAndPassword(private val act: MainActivity) {

    fun signUpWithEmailAndPassword(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(act, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            return
        }

        act.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification(task.result.user!!)
                    act.uiUpdate(task.result.user)
                } else {
                    handleSignUpError(task.exception)

                }
            }
    }

    private fun handleSignUpError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Log.d("MyLog", "Exception: ${exception.errorCode}")
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,
                    Toast.LENGTH_LONG
                ).show()
            }

            is FirebaseAuthInvalidCredentialsException -> {
                Log.d("MyLog", "Exception: ${exception.errorCode}")
                if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                    Toast.makeText(
                        act,
                        FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                        Toast.LENGTH_LONG
                    ).show()
                    Toast.makeText(
                        act,
                        FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            is FirebaseAuthWeakPasswordException -> {
                Log.d("MyLog", "Exception: ${exception.errorCode}")
                Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG)
                    .show()
            }

            is FirebaseAuthInvalidUserException -> {
                Log.d("MyLog", "Exception: ${exception.errorCode}")
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_CREDENTIAL,
                    Toast.LENGTH_LONG
                )
                    .show()
            }

            else -> {
                Toast.makeText(act, R.string.sign_dialog_sign_up_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        act.uiUpdate(task.result.user)
                    } else {
                        Log.d("MyLog", "Exception: ${task.exception}")
                        handleSignUpError(task.exception)
                    }
                }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    R.string.sign_dialog_successful_send_verification_email,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    act,
                    R.string.sign_dialog_error_send_verification_email,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}