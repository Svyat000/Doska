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
import com.sddrozdov.doska.fragments.FragmentCloseInterface
import com.sddrozdov.doska.fragments.ImageListFragment
import com.sddrozdov.doska.models.SelectImageItem
import com.sddrozdov.doska.recyclerViewAdapters.ImageAdapter
import com.sddrozdov.doska.utilites.CityHelper

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    private var _binding: ActivityEditAdsBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private var dialogSpinnerHelper = DialogSpinnerHelper()

    private lateinit var imageAdapter: ImageAdapter

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
        dialogSpinnerHelper.showSpinnerDialog(this, countryList, binding.editAdsActSelectCountry)
        if (binding.editAdsActSelectCity.text.toString() != getString(R.string.select_city)) {
            binding.editAdsActSelectCity.text = getString(R.string.select_city)
        }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.editActAdsImages.adapter = imageAdapter
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.editAdsActSelectCountry.text.toString()
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

    fun onClickGetImages(view: View) {
        binding.editAdsActScrollView.visibility = View.GONE
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.editAdsActPlace_holder, ImageListFragment(this, TODO()))
        fragmentManager.commit()
    }

    override fun onFragClose(list: ArrayList<SelectImageItem>) {
        binding.editAdsActScrollView.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
    }
}