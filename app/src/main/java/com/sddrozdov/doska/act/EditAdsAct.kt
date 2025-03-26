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
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

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
        clickPublicate()

    }

    private fun clickPublicate() {
        binding.buttonPublicate.setOnClickListener {
            ads = fillAd()
            if (isEditState) {
                ads?.copy(key = ads?.key)
                    ?.let { it1 -> dbManager.publicationAd(it1, onPublishFinish()) }
            } else {

                //dbManager.publicationAd(adTemp, onPublishFinish())
                uploadImages()
            }
        }
    }

    private fun checkEditState() {
        if (isEditState()) {
            isEditState = true
            ads = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ads
            if (ads != null) {
                fillView(ads!!)
            }
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
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }

        }
    }

    private fun fillAd(): Ads {
        val ads: Ads
        binding.apply {
            ads = Ads(
                editAdsActSelectCountry.text.toString(),
                editAdsActSelectCity.text.toString(),
                editTextEmail.text.toString(),
                editTextPhoneNumber.text.toString(),
                editTextIndex.text.toString(),
                editAdsActSelectCat.text.toString(),
                editTitle.text.toString(),
                editTextPrice.text.toString(),
                editTextDescription.text.toString(),
                dbManager.db.push().key,
                dbManager.auth.uid,
                favoriteCounter = "0",
                mainImage = "emptyImage",
                image2 = "emptyImage",
                image3 = "emptyImage"
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
            ImagePicker.getMultiImages(this, 3)
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
        chooseImageFrag = ImageListFragment(this)
        if (newList != null) chooseImageFrag?.resizeSelectedImages(newList, true, this)
        binding.editAdsActScrollView.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.editAdsActPlace_holder, chooseImageFrag!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageAdapter.imageArray.size == imageIndex) {
            dbManager.publicationAd(ads!!, onPublishFinish())
            return
        }
        //val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val byteArray = prepareImageByteArray(imageAdapter.imageArray[imageIndex])

        uploadImageToAppwrite(byteArray) { fileId, fileUrl ->
            //dbManager.publicationAd(ads!!, onPublishFinish())//ads?.copy(mainImage = fileUrl
            nextImage(fileUrl)
        }
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun setImageUriToAd(uri: String) {
        when (imageIndex) {
            0 -> ads = ads?.copy(mainImage = uri)
            1 -> ads = ads?.copy(image2 = uri)
            2 -> ads = ads?.copy(image3 = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

//    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
//        val imStorageRef = dbManager.dbStorage.child(dbManager.auth.uid!!)
//            .child("image_${System.currentTimeMillis()}")
//        val uploadTask = imStorageRef.putBytes(byteArray)
//        uploadTask.continueWithTask { task ->
//            imStorageRef.downloadUrl
//        }.addOnCompleteListener(listener)
//    }


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


                val fileUrl =
                    "https://cloud.appwrite.io/v1/storage/buckets/67e2a80d003d243b8d8a/files/${response.id}/view?project=67e2a75600274ddbebaa"

                Log.d("Appwrite", "Generated URL: $fileUrl") // Для отладки

                onComplete(response.id, fileUrl)

            } catch (e: Exception) {
                Log.e("Appwrite", "Upload error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@EditAdsActivity,
                        "Ошибка загрузки: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                tempFile.delete()
            }
        }
    }

}
