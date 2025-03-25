package com.sddrozdov.doska.utilites

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import io.ak1.pix.models.VideoOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {

    const val MAX_IMAGE_COUNT = 3

    private fun getOption(imageCounter: Int): Options {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO                       //Image/video capture ratio
            count = imageCounter                      //Number of images to restrict selection count
            spanCount = 4                             //Number for columns in grid
            path = "Pix/Camera"                          //Custom Path For media Storage
            isFrontFacing = false                           //Front Facing camera on start
            mode = Mode.Picture                //Option to select only pictures or videos or both
            flash = Flash.Auto                          //Option to select flash type
            preSelectedUrls = ArrayList<Uri>()                //Pre selected Image Urls
            videoOptions = VideoOptions().apply {               //Video configurations
                videoDurationLimitInSeconds = 10                 //Duration for video recording
            }
        }
        return options
    }

    fun getMultiImages(editAdsActivity: EditAdsActivity, imageCounter: Int) {
        editAdsActivity.addPixToActivity(
            R.id.editAdsActPlace_holder, getOption(imageCounter)
        ) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectedImages(editAdsActivity, result.data)
                    //closePixFragment(editAdsActivity)
                    Log.d("MyLog", "Фрагмент открыт")
                }

                PixEventCallback.Status.BACK_PRESSED -> TODO()
            }
        }
    }

    fun addImages(editAdsActivity: EditAdsActivity, imageCounter: Int) {
        val tempFragment = editAdsActivity.chooseImageFrag
        editAdsActivity.addPixToActivity(
            R.id.editAdsActPlace_holder, getOption(imageCounter)
        ) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    editAdsActivity.chooseImageFrag = tempFragment
                    openChooseImageFragment(editAdsActivity, tempFragment!!)
                    editAdsActivity.chooseImageFrag?.updateAdapter(
                        result.data as ArrayList<Uri>,
                        editAdsActivity
                    )
                }

                PixEventCallback.Status.BACK_PRESSED -> TODO()
            }
        }
    }

    fun getSingleImages(editAdsActivity: EditAdsActivity) {
        val fragmentTemp = editAdsActivity.chooseImageFrag
        editAdsActivity.addPixToActivity(
            R.id.editAdsActPlace_holder, getOption(1)
        ) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    editAdsActivity.chooseImageFrag = fragmentTemp
                    openChooseImageFragment(editAdsActivity, fragmentTemp!!)
                    singleImage(editAdsActivity, result.data[0])
                }

                PixEventCallback.Status.BACK_PRESSED -> TODO()
            }
        }
    }

    private fun openChooseImageFragment(editAdsActivity: EditAdsActivity, fragment: Fragment) {
        editAdsActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.editAdsActPlace_holder, fragment).commit()
    }

    private fun closePixFragment(editAdsActivity: EditAdsActivity) {
        val fragmentLists = editAdsActivity.supportFragmentManager.fragments
        fragmentLists.forEach {
            if (it.isVisible) editAdsActivity.supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }
    }

    fun getMultiSelectedImages(editAdsActivity: EditAdsActivity, uris: List<Uri>) {

        if (uris.size > 1 && editAdsActivity.chooseImageFrag == null) {
            editAdsActivity.openChooseImageFragment(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && editAdsActivity.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                val bitMapArray = ImageManager.imageResize(
                    uris as ArrayList<Uri>,
                    editAdsActivity
                ) as ArrayList<Bitmap>
                editAdsActivity.imageAdapter.updateAdapter(bitMapArray)
                closePixFragment(editAdsActivity)
            }
        }
    }

    private fun singleImage(editAdsActivity: EditAdsActivity, uri: Uri) {
        editAdsActivity.chooseImageFrag?.setSingleImage(uri, editAdsActivity.editImagePos)
    }
}
