package com.sddrozdov.doska.act

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.databinding.ActivityDescAdsBinding
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.recyclerViewAdapters.ImageAdapterForViewPager
import com.sddrozdov.doska.utilites.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DescAdsActivity : AppCompatActivity() {

    private var _binding: ActivityDescAdsBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    lateinit var imageAdapterForViewPager: ImageAdapterForViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDescAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)
        init()
    }

    private fun init() {
        imageAdapterForViewPager = ImageAdapterForViewPager()
        binding.apply {
            viewPager.adapter = imageAdapterForViewPager

        }
        getIntentFromMainActivity()
    }

    private fun getIntentFromMainActivity() {
        val ad = intent.getSerializableExtra("AD") as Ads
        fillImageArray(ad)
    }

    private fun fillImageArray(ads: Ads) {
        val listUris = listOf(ads.mainImage, ads.image2, ads.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.getBitmapFromUri(listUris)
            imageAdapterForViewPager.updateAdapter(bitmapList as ArrayList<Bitmap>)
        }
    }


}