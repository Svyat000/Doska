package com.sddrozdov.doska.utilites

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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

    fun launcher(editAdsActivity: EditAdsActivity, imageCounter: Int) {
        editAdsActivity.addPixToActivity(
            R.id.editAdsActPlace_holder, getOption(imageCounter)
        ) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectedImages(editAdsActivity, result.data)
                    closePixFragment(editAdsActivity)
                    Log.d("MyLog", "Фрагмент открыт")
                }

                PixEventCallback.Status.BACK_PRESSED -> TODO()
            }
        }
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
        } else if (editAdsActivity.chooseImageFrag != null) {
            editAdsActivity.chooseImageFrag?.updateAdapter(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && editAdsActivity.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                // editAdsActivity.binding.pBarLoad.visibility = View.VISIBLE
                val bitMapArray = ImageManager.imageResize(uris as ArrayList<Uri>, editAdsActivity) as ArrayList<Bitmap>
                //editAdsActivity.binding.pBarLoad.visibility = View.GONE
                editAdsActivity.imageAdapter.updateAdapter(bitMapArray)
            }
        }
    }

    fun singleImage(editAdsActivity: EditAdsActivity, uri: Uri) {
        editAdsActivity.chooseImageFrag?.setSingleImage(uri, editAdsActivity.editImagePos)
    }
}
