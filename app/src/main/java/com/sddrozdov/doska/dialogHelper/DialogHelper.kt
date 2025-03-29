package com.sddrozdov.doska.dialogHelper

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sddrozdov.doska.act.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.accountHelper.AccountHelperAnonymously
import com.sddrozdov.doska.accountHelper.AccountHelperEmailAndPassword
import com.sddrozdov.doska.accountHelper.AccountHelperGoogleSignIn
import com.sddrozdov.doska.databinding.SignDialogBinding

class DialogHelper(private val mainActivity: MainActivity) {

    private var _binding: SignDialogBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private val accountHelperEmailAndPassword = AccountHelperEmailAndPassword(mainActivity)
    val accountHelperGoogleSignIn = AccountHelperGoogleSignIn(mainActivity)
    val accountHelperAnonymously = AccountHelperAnonymously(mainActivity)

    fun createSignDialog(index: Int) {
        val builderDialog = AlertDialog.Builder(mainActivity)
        _binding = SignDialogBinding.inflate(mainActivity.layoutInflater)

        builderDialog.setView(binding.root)
        setTextTitleAndButtonInDialog(index, binding)

        val dialog = builderDialog.create()

        binding.signDialogSingUpAndInButton.setOnClickListener {
            takeFromTheFieldsEmailAndPassword(index, binding, dialog)
        }
        binding.signDialogForgetPasswordButton.setOnClickListener {
            setOnClickResetPassword(binding, dialog)
        }
        binding.signDialogSingUpAndInGoogleButton.setOnClickListener {
            accountHelperGoogleSignIn.launchCredentialManager()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(binding: SignDialogBinding, dialog: AlertDialog) {
        if (binding.signDialogEnterEmail.text!!.isNotEmpty()) {
            mainActivity.mAuth.sendPasswordResetEmail(binding.signDialogEnterEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            mainActivity,
                            R.string.sign_dialog_send_email_reset_password,
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            dialog.dismiss()
        } else {
            binding.signDialogPleaseEnterEmail.visibility = View.VISIBLE
        }
    }

    private fun takeFromTheFieldsEmailAndPassword(
        index: Int,
        binding: SignDialogBinding,
        dialog: AlertDialog
    ) {
        dialog.dismiss()
        if (index == DialogConstants.SIGN_UP_STATE) {
            accountHelperEmailAndPassword.signUpWithEmailAndPassword(
                binding.signDialogEnterEmail.text.toString(),
                binding.signDialogEnterPassword.text.toString()
            )
        } else {
            accountHelperEmailAndPassword.signInWithEmailAndPassword(
                binding.signDialogEnterEmail.text.toString(),
                binding.signDialogEnterPassword.text.toString()
            )
        }
    }

    private fun setTextTitleAndButtonInDialog(index: Int, binding: SignDialogBinding) {
        if (index == DialogConstants.SIGN_UP_STATE) {
            binding.signDialogTitle.text = mainActivity.resources.getString(R.string.account_register)
            binding.signDialogSingUpAndInButton.text =
                mainActivity.resources.getString(R.string.sign_dialog_sign_up_button)
        } else {
            binding.signDialogTitle.text = mainActivity.resources.getString(R.string.account_login)
            binding.signDialogSingUpAndInButton.text =
                mainActivity.resources.getString(R.string.sign_dialog_sign_in_button)
            binding.signDialogForgetPasswordButton.visibility = View.VISIBLE
        }
    }
}