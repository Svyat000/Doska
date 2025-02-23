package com.sddrozdov.doska.accountHelper

import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.R

class AccountHelperEmailAndPassword(private val act: MainActivity) {

    fun signUpWithEmailAndPassword(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result.user!!)
                        act.uiUpdate(task.result.user)
                    } else {
                        Toast.makeText(act, R.string.sign_dialog_sign_up_error, Toast.LENGTH_SHORT)
                            .show()
                    }
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
                        Toast.makeText(act, R.string.sign_dialog_sign_in_error, Toast.LENGTH_SHORT)
                            .show()
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