package com.sddrozdov.doska.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.sddrozdov.doska.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    private var _binding: ProgressDialogLayoutBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    fun createProgressDialog(activity: Activity) : AlertDialog {

        val builderDialog = AlertDialog.Builder(activity)
        _binding = ProgressDialogLayoutBinding.inflate(activity.layoutInflater)
        builderDialog.setView(binding.root)

        val dialog = builderDialog.create()

        dialog.setCancelable(false)

        dialog.show()

        return dialog
    }
}