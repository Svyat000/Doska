package com.sddrozdov.doska.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.sddrozdov.doska.databinding.ProgressDialogLayoutBinding

object ProgressDialog {
    fun createProgressDialog(activity: Activity): AlertDialog {
        val binding = ProgressDialogLayoutBinding.inflate(activity.layoutInflater)
        val builderDialog = AlertDialog.Builder(activity)
        builderDialog.setView(binding.root)
        val dialog = builderDialog.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}