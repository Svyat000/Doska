package com.sddrozdov.doska.act

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.databinding.ActivityEditAdsBinding
import com.sddrozdov.doska.dialogs.DialogSpinnerHelper
import com.sddrozdov.doska.utilites.CityHelper

class EditAdsActivity : AppCompatActivity() {

    private var _binding: ActivityEditAdsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        _binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

        // Получение списка стран из CityHelper
        val countryList = CityHelper.getAllCountries(this)

        // Создание экземпляра диалогового помощника для выбора
        val dialogSpinnerHelper = DialogSpinnerHelper()
        // Показ диалогового окна со списком стран
        dialogSpinnerHelper.showSpinnerDialog(this, countryList)
    }
}