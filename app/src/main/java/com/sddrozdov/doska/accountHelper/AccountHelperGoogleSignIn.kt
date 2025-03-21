package com.sddrozdov.doska.accountHelper

import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sddrozdov.doska.act.MainActivity
import com.sddrozdov.doska.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountHelperGoogleSignIn(private val act: MainActivity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(act)

    fun launchCredentialManager() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(act.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        act.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = act,
                    request = request
                )
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Credential retrieval failed", e)
                when (e) {
                    is NoCredentialException -> {
                        // Handle case where no credentials are available
                        Log.d(TAG, "No saved credentials found")
                    }

                    else -> {
                        // Handle other exceptions
                        Toast.makeText(act, "Sign-in error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun handleSignIn(credential: androidx.credentials.Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Unexpected credential type: ${credential.type}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        idToken?.let {
            val credential = GoogleAuthProvider.getCredential(it, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(act) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "GoogleSignInWithCredential:success")
                        act.uiUpdate(auth.currentUser)
                    } else {
                        Log.w(TAG, "GoogleSignInWithCredential:failure", task.exception)
                        act.uiUpdate(null)
                    }
                }
        } ?: run {
            Log.w(TAG, "ID Token is null")
            act.uiUpdate(null)
        }
    }

    fun signOut() {
        auth.signOut()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                act.uiUpdate(null)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Clear credentials failed: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "AccountHelperGoogle"
    }
}