package com.sddrozdov.doska.accountHelper

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.sddrozdov.doska.act.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.constans.FirebaseAuthConstants

class AccountHelperEmailAndPassword(private val act: MainActivity) {

    fun signUpWithEmailAndPassword(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(act, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            return
        }
        act.mAuth.currentUser?.delete()?.addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                act.mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result.user?.let { user ->
                                sendEmailVerification(user)
                                act.uiUpdate(user)
                            } ?: run {
                                // Обработка случая, когда user равен null
                                Toast.makeText(
                                    act,
                                    R.string.error_user_not_found,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (task.exception is FirebaseAuthUserCollisionException) {
                            linkEmailToGmail(email, password)
                        } else handleSignUpError(task.exception)
                    }
            }
        }

    }


    private fun handleSignUpError(exception: Exception?) {

        when (exception) {

            is FirebaseAuthInvalidCredentialsException -> {
                Log.d("MyLog", "Exception111: ${exception.errorCode}")
                if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_CREDENTIAL) {
                    Toast.makeText(
                        act,
                        act.getString(R.string.wrong_email_or_password),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            is FirebaseAuthWeakPasswordException -> {
                Log.d("MyLog", "Exception: ${exception.errorCode}")
                Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG)
                    .show()
            }

            else -> {
                Toast.makeText(act, R.string.sign_dialog_sign_up_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.currentUser?.delete()?.addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
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

    private fun linkEmailToGmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {

            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        act,
                        act.getString(R.string.link_done),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                act,
                act.getString(R.string.login_to_Google_via_email),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}