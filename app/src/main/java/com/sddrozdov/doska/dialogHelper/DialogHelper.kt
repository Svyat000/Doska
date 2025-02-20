package com.sddrozdov.doska.dialogHelper

import androidx.appcompat.app.AlertDialog
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.accountHelper.AccountHelper
import com.sddrozdov.doska.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {

    private var _binding: SignDialogBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private val accountHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        _binding = SignDialogBinding.inflate(act.layoutInflater)

        if (index == DialogConstants.SIGN_UP_STATE) {
            binding.signDialogTitle.text = act.resources.getString(R.string.account_register)
            binding.signDialogSingUpAndInButton.text =
                act.resources.getString(R.string.sign_dialog_sign_up_button)
        } else {
            binding.signDialogTitle.text = act.resources.getString(R.string.account_login)
            binding.signDialogSingUpAndInButton.text =
                act.resources.getString(R.string.sign_dialog_sign_in_button)
        }

        binding.signDialogSingUpAndInButton.setOnClickListener {
            if (index == DialogConstants.SIGN_UP_STATE) {
                accountHelper.signUpWithEmail(
                    binding.signDialogEnterEmail.text.toString(),
                    binding.signDialogEnterPassword.text.toString()
                )
            } else {

            }
        }




        builder.setView(binding.root)
        builder.show()
    }
}