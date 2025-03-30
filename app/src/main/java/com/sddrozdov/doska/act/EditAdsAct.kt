package com.sddrozdov.doska.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.sddrozdov.doska.R
import com.sddrozdov.doska.appwrite.Appwrite
import com.sddrozdov.doska.models.DbManager
import com.sddrozdov.doska.databinding.ActivityEditAdsBinding
import com.sddrozdov.doska.dialogs.DialogSpinnerHelper
import com.sddrozdov.doska.interfaces.FragmentCloseInterface
import com.sddrozdov.doska.fragments.ImageListFragment
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.recyclerViewAdapters.ImageAdapterForViewPager
import com.sddrozdov.doska.utilites.CityHelper
import com.sddrozdov.doska.utilites.ImageManager
import com.sddrozdov.doska.utilites.ImagePicker
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    private val dbManager = DbManager()
    var chooseImageFrag: ImageListFragment? = null
    private var _binding: ActivityEditAdsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")
    private var dialogSpinnerHelper = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapterForViewPager
    var editImagePos = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ads: Ads? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)
        Appwrite.init(applicationContext)

        init()
        checkEditState()
        setupViewPagerImageCounter()
        setupAuctionTypeListener()
        setupPublicationButtonClickListener()
    }

    private fun setupPublicationButtonClickListener() {
        binding.buttonPublicate.setOnClickListener {
            if (isFormValid()) {
                ads = createAd()
                if (isEditState) {
                    dbManager.publicationAd(ads!!, onPublishFinish())
                } else {
                    uploadImages()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        return if (binding.radioAuction.isChecked) {
            validateAuctionFields()
        } else {
            validateRegularAdFields()
        }
    }

    private fun validateAuctionFields(): Boolean {
        return !(binding.editAuctionStartPrice.text.isNullOrEmpty() ||
                binding.editAuctionDuration.text.isNullOrEmpty()) &&
                binding.editAuctionStartPrice.text.toString().toFloatOrNull() != null
    }

    private fun validateRegularAdFields(): Boolean {
        return binding.editTextPrice.text.toString().isNotEmpty() &&
                binding.editTextPrice.text.toString().toFloatOrNull() != null
    }

    private fun checkEditState() {
        if (isEditState()) {
            isEditState = true
            ads = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ads
            ads?.let { fillView(it) }
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillView(ads: Ads) = with(binding) {
        editAdsActSelectCountry.text = ads.country
        editAdsActSelectCity.text = ads.city
        editTextPhoneNumber.setText(ads.tel)
        editTextIndex.setText(ads.index)
        editAdsActSelectCat.text = ads.category
        editTitle.setText(ads.title)
        editTextPrice.setText(ads.price)
        editTextDescription.setText(ads.description)
        ImageManager.fillImageArray(ads, imageAdapter)
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish(success: Boolean?) {
                finish()
            }
        }
    }

    private fun setupAuctionTypeListener() {
        binding.radioGroupAdType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioAuction) {
                showAuctionFields()
            } else {
                showRegularAdFields()
            }
        }
    }

    private fun showAuctionFields() {
        binding.auctionFields.visibility = View.VISIBLE
        binding.editTextPrice.visibility = View.GONE
        binding.titlePrice.visibility = View.GONE
    }

    private fun showRegularAdFields() {
        binding.auctionFields.visibility = View.GONE
        binding.editTextPrice.visibility = View.VISIBLE
        binding.titlePrice.visibility = View.VISIBLE
    }

    private fun createAd(): Ads {
        val isAuction = binding.radioAuction.isChecked
        val startPrice = binding.editAuctionStartPrice.text.toString()
        val durationDays = binding.editAuctionDuration.text.toString().toIntOrNull() ?: 0

        return Ads(
            country = binding.editAdsActSelectCountry.text.toString(),
            city = binding.editAdsActSelectCity.text.toString(),
            email = binding.editTextEmail.text.toString(),
            tel = binding.editTextPhoneNumber.text.toString(),
            index = binding.editTextIndex.text.toString(),
            category = binding.editAdsActSelectCat.text.toString(),
            title = binding.editTitle.text.toString(),
            price = if (isAuction) startPrice else binding.editTextPrice.text.toString(),
            description = binding.editTextDescription.text.toString(),
            key = ads?.key ?: dbManager.db.push().key,
            uid = dbManager.auth.uid,
            mainImage = ads?.mainImage ?: "emptyImage",
            image2 = ads?.image2 ?: "emptyImage",
            image3 = ads?.image3 ?: "emptyImage",
            time = System.currentTimeMillis().toString(),
            isAuction = isAuction,
            auctionStartPrice = if (isAuction) startPrice else null,
            auctionCurrentPrice = if (isAuction) startPrice else null,
            auctionEndTime = if (isAuction) System.currentTimeMillis() + durationDays * 86400000 else 0
        )
    }

    fun onClickSelectCountry(view: View) {
        val countryList = CityHelper.getAllCountries(this)
        dialogSpinnerHelper.showSpinnerDialog(this, countryList, binding.editAdsActSelectCountry)
        if (binding.editAdsActSelectCity.text.toString() != getString(R.string.select_city)) {
            binding.editAdsActSelectCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCategory(view: View) {
        val listCategory = ArrayList(resources.getStringArray(R.array.category).toList())
        dialogSpinnerHelper.showSpinnerDialog(this, listCategory , binding.editAdsActSelectCat)
    }

    private fun init() {
        imageAdapter = ImageAdapterForViewPager()
        binding.editActAdsImages.adapter = imageAdapter
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.editAdsActSelectCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val cityList = CityHelper.getAllSities(this, selectedCountry)
            dialogSpinnerHelper.showSpinnerDialog(this, cityList, binding.editAdsActSelectCity)
        } else {
            Toast.makeText(this, getString(R.string.first_select_your_country), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun onClickGetImages(view: View) {
        if (imageAdapter.imageArray.isEmpty()) {
            ImagePicker.getMultiImages(this, 3)
        } else {
            openChooseImageFragment(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.imageArray)
        }
    }

    private fun setupViewPagerImageCounter() {
        binding.editActAdsImages.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.editActAdsImages.adapter?.itemCount}"
                binding.textViewImageCounter.text = imageCounter
            }
        })
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.editAdsActScrollView.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        chooseImageFrag = null
    }

    fun openChooseImageFragment(newList: ArrayList<Uri>?) {
        chooseImageFrag = ImageListFragment(this)
        newList?.let { chooseImageFrag?.resizeSelectedImages(it, true, this) }
        binding.editAdsActScrollView.visibility = View.GONE
        supportFragmentManager.beginTransaction() .replace(R.id.editAdsActPlace_holder, chooseImageFrag!!)
            .commit()
    }

    private fun uploadImages() {
        if (imageAdapter.imageArray.size == imageIndex) {
            dbManager.publicationAd(ads!!, onPublishFinish())
            return
        }

        val byteArray = prepareImageByteArray(imageAdapter.imageArray[imageIndex])
        uploadImageToAppwrite(byteArray) { fileId, fileUrl ->
            updateAdImageUri(fileUrl)
            moveToNextImage()
        }
    }

    private fun moveToNextImage() {
        imageIndex++
        uploadImages()
    }

    private fun updateAdImageUri(uri: String) {
        ads = when (imageIndex) {
            0 -> ads?.copy(mainImage = uri)
            1 -> ads?.copy(image2 = uri)
            2 -> ads?.copy(image3 = uri)
            else -> ads
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImageToAppwrite(
        byteArray: ByteArray,
        onComplete: (fileId: String, fileUrl: String) -> Unit
    ) {
        val tempFile = File.createTempFile(
            "${dbManager.auth.uid}_image_${System.currentTimeMillis()}",
            ".jpg",
            cacheDir
        ).apply {
            writeBytes(byteArray)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val storage = Storage(Appwrite.appwriteClient)
                val response = storage.createFile(
                    bucketId = "67e2a80d003d243b8d8a",
                    fileId = ID.unique(),
                    file = InputFile.fromFile(tempFile),
                    permissions = listOf("read(\"any\")")
                )

                val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/67e2a80d003d243b8d8a/files/${response.id}/view?project=67e2a75600274ddbebaa"
                Log.d("Appwrite", "Generated URL: $fileUrl") // Для отладки

                onComplete(response.id, fileUrl)

            } catch (e: Exception) {
                Log.e("Appwrite", "Upload error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@EditAdsActivity, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                tempFile.delete()
            }
        }
    }
}
