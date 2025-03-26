package com.sddrozdov.doska.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.sddrozdov.doska.R
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

    private var ads: Ads? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDescAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)
        init()
        binding.apply {
            fbTel.setOnClickListener {
                callToNumber()
            }
            fbEmail.setOnClickListener {
                sendEmail()
            }
        }

    }

    private fun init() {
        imageAdapterForViewPager = ImageAdapterForViewPager()
        binding.apply {
            viewPager.adapter = imageAdapterForViewPager

        }
        getIntentFromMainActivity()
    }

    private fun callToNumber() {
        val callUri = "tel:${ads?.tel}"
        val intentCall = Intent(Intent.ACTION_DIAL)
        intentCall.data = callUri.toUri()
        startActivity(intentCall)
    }

    private fun sendEmail() {
        val intentForSendEmail = Intent(Intent.ACTION_SEND)
        intentForSendEmail.type = "message/rfc822"
        intentForSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ads?.email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.AD))
            putExtra(Intent.EXTRA_TEXT, "TODO()") //TODO("Доделать заполнение объявления")
        }
        try {
            startActivity(Intent.createChooser(intentForSendEmail, getString(R.string.OPEN_WITH)))
        } catch (exeption: ActivityNotFoundException) {

        }
    }


    private fun getIntentFromMainActivity() {
        ads = intent.getSerializableExtra("AD") as Ads
        if (ads != null) updateUI(ads!!)
    }

    private fun updateUI(ads: Ads) {
        fillImageArray(ads)
        fillTextView(ads)
    }

    private fun fillImageArray(ads: Ads) {
        val listUris = listOf(ads.mainImage, ads.image2, ads.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.getBitmapFromUri(listUris)
            imageAdapterForViewPager.updateAdapter(bitmapList as ArrayList<Bitmap>)
        }
    }

    private fun fillTextView(ads: Ads) {
        binding.apply {
            tvTitle.text = ads.title
            tvDescription.text = ads.description
            tvPrice.text = ads.price
            tvEmail.text = ads.email
            tvTel.text = ads.tel
            tvEmail.text
            tvCountry.text = ads.tel
            tvCity.text = ads.city
            tvIndex.text = ads.index
        }
    }


}