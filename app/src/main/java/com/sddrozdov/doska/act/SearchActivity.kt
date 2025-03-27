package com.sddrozdov.doska.act

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ActivitySearchBinding
import com.sddrozdov.doska.dialogs.DialogSpinnerHelper
import com.sddrozdov.doska.utilites.CityHelper

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private val dialogSpinnerHelper = DialogSpinnerHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

        setSupportActionBar(binding.searchActToolbar)
        onClickSelectCountry()
        onClickSelectCity()
        onClickSearch()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Toast.makeText(this, "НАЖАЛИ НАЗАД ", Toast.LENGTH_LONG).show()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onClickSelectCountry() = with(binding) {
        tvCountry.setOnClickListener {
            val countryList = CityHelper.getAllCountries(this@SearchActivity)
            // Показ диалогового окна со списком стран
            dialogSpinnerHelper.showSpinnerDialog(this@SearchActivity, countryList, tvCountry)
            if (tvCity.text.toString() != getString(R.string.select_city)) {
                tvCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding) {
        tvCity.setOnClickListener {
            val selectedCountry = tvCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val cityList = CityHelper.getAllSities(this@SearchActivity, selectedCountry)
                dialogSpinnerHelper.showSpinnerDialog(this@SearchActivity, cityList, tvCity)
            } else {
                Toast.makeText(
                    this@SearchActivity,
                    getString(R.string.first_select_your_country),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun onClickSearch() = with(binding) {
        btDone.setOnClickListener {
            Log.d("MAIN","text : ${createFilter()}")
        }
    }

    private fun createFilter(): String = with(binding) {
        val selectCountry = getString(R.string.select_country)
        val selectCity = getString(R.string.select_city)

        val tempArrayFilter = listOf(
            tvCountry.text?.toString().orEmpty(),
            tvCity.text?.toString().orEmpty(),
            edIndex.text?.toString().orEmpty()
        )

        val nonDefaultValues = tempArrayFilter.filter {
            it != selectCountry && it != selectCity && it.isNotEmpty()
        }

        return nonDefaultValues.joinToString("_")
    }

}