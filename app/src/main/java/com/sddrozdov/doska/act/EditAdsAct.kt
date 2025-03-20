package com.sddrozdov.doska.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.MainActivity
import com.sddrozdov.doska.R
import com.sddrozdov.doska.database.DbManager
import com.sddrozdov.doska.databinding.ActivityEditAdsBinding
import com.sddrozdov.doska.dialogs.DialogSpinnerHelper
import com.sddrozdov.doska.interfaces.FragmentCloseInterface
import com.sddrozdov.doska.fragments.ImageListFragment
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.recyclerViewAdapters.ImageAdapterForViewPager
import com.sddrozdov.doska.utilites.CityHelper
import com.sddrozdov.doska.utilites.ImagePicker

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    private val dbManager = DbManager(null)

    var chooseImageFrag: ImageListFragment? = null

    private var _binding: ActivityEditAdsBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private var dialogSpinnerHelper = DialogSpinnerHelper()

    lateinit var imageAdapter: ImageAdapterForViewPager

    var editImagePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

        init()
        clickPublicate()
    }

    fun clickPublicate() {
        binding.buttonPublicate.setOnClickListener {
            dbManager.publicationAd(fillAd())
        }
    }

    private fun fillAd(): Ads {
        val ads: Ads
        binding.apply {
            ads = Ads(
                editAdsActSelectCountry.text.toString(), editAdsActSelectCity.text.toString(),
                editTextPhoneNumber.text.toString(), editTextIndex.text.toString(),
                editAdsActSelectCat.text.toString(), editTitle.text.toString(),
                editTextPrice.text.toString(), editTextDescription.text.toString(),
                dbManager.db.push().key, dbManager.auth.uid
            )
        }
        return ads
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

    fun onClickSelectCategory(view: View) {
        val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialogSpinnerHelper.showSpinnerDialog(this, listCategory, binding.editAdsActSelectCat)
    }

    private fun init() {
        imageAdapter = ImageAdapterForViewPager()
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

        if (imageAdapter.imageArray.size == 0) {
            ImagePicker.launcher(this, 3)
        } else {
            openChooseImageFragment(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.imageArray)

        }

//        binding.editAdsActScrollView.visibility = View.GONE
//        val fragmentManager = supportFragmentManager.beginTransaction()
//        fragmentManager.replace(R.id.editAdsActPlace_holder, ImageListFragment(this, TODO()
//        fragmentManager.commit()
    }


    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.editAdsActScrollView.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        chooseImageFrag = null
    }

    fun openChooseImageFragment(newList: ArrayList<Uri>?) {
        chooseImageFrag = ImageListFragment(this, newList)
        binding.editAdsActScrollView.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.editAdsActPlace_holder, chooseImageFrag!!)
        fm.commit()

    }
}