package com.sddrozdov.doska.utilites

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.widget.ImageView
import java.io.File
import java.io.InputStream

object ImageManager {

    private const val MAX_IMAGE_SIZE = 1000
    private const val WIDTH = 0
    private const val HEIGHT = 1

    fun getImageSize(uri: Uri, activity: Activity): List<Int> {

        val inputStream = activity.contentResolver.openInputStream(uri)
        val fTemp = File(activity.cacheDir,"temp.tmp")

        if (inputStream != null) {
            fTemp.copyInputStreamToFile(inputStream)
        }

        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(fTemp.path, option)

        return if (imageRotation(fTemp) == 90)
            listOf(option.outHeight, option.outWidth)
        else listOf(option.outWidth, option.outHeight)
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { out ->
            inputStream.copyTo(out)
        }
    }

    private fun imageRotation(imageFile : File): Int {
        val rotation: Int
        val exif = ExifInterface(imageFile.absoluteFile)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)

return 1
    }

    fun chooseScaleType(im: ImageView, bitmap: Bitmap){

    }
    fun imageResize(){

    }

}