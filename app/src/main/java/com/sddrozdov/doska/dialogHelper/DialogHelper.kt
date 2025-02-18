package com.sddrozdov.doska.dialogHelper

import androidx.appcompat.app.AlertDialog
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.databinding.SignDialogBinding

class DialogHelper(act: MainActivity) {

    private val act = act

    private var _binding: SignDialogBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    fun createSignDialog() {
        val builder = AlertDialog.Builder(act)
        _binding = SignDialogBinding.inflate(act.layoutInflater)
        builder.setView(binding.root)
        builder.show()
    }
}