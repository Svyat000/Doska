package com.sddrozdov.doska.act

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.databinding.ActivityEditAdsBinding
import com.sddrozdov.doska.utilites.CityHelper


class EditAdsAct : AppCompatActivity() {

    private var _binding: ActivityEditAdsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            CityHelper.getAllCountries(this)
        )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editActAdsCountrySpinner.adapter = adapter
    }
}