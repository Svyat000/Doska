package com.sddrozdov.doska.act

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ActivityEditAdsBinding
import com.sddrozdov.doska.dialogs.DialogSpinnerHelper
import com.sddrozdov.doska.utilites.CityHelper

class EditAdsActivity : AppCompatActivity() {

    private var _binding: ActivityEditAdsBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private var dialogSpinnerHelper = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

    }

    fun onClickSelectCountry(view: View) {
        // Получение списка стран из CityHelper
        val countryList = CityHelper.getAllCountries(this)
        // Показ диалогового окна со списком стран
        dialogSpinnerHelper.showSpinnerDialog(this, countryList,binding.EditAdsActSelectCountry)
        if(binding.editAdsActSelectCity.text.toString() != getString(R.string.select_city)){
            binding.editAdsActSelectCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.EditAdsActSelectCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            // Получение списка стран из CityHelper
            val cityList = CityHelper.getAllSities(this, selectedCountry)
            // Показ диалогового окна со списком стран
            dialogSpinnerHelper.showSpinnerDialog(this, cityList, binding.editAdsActSelectCity)
        } else {
            Toast.makeText(this, getString(R.string.first_select_your_country), Toast.LENGTH_LONG)
                .show()
        }
    }
}