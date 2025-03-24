package com.sddrozdov.doska.accountHelper

import android.widget.Toast
import com.sddrozdov.doska.act.MainActivity

class AccountHelperAnonymously(private val mainActivity: MainActivity) {

    fun signInAnonymously(listener: Listener) {
        mainActivity.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(mainActivity,"Вы вошли как гость",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mainActivity,"Не удалось войти как гость",Toast.LENGTH_SHORT).show()
            }
        }
    }

}