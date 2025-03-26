package com.sddrozdov.doska.utilites

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

object ImageManager {

    private const val MAX_IMAGE_SIZE = 1000
    private const val WIDTH = 0
    private const val HEIGHT = 1

    fun getImageSize(uri: Uri, activity: Activity): List<Int> {

        val inputStream = activity.contentResolver.openInputStream(uri)

//        val fTemp = File(activity.cacheDir, "temp.tmp")
//
//        if (inputStream != null) {
//            fTemp.copyInputStreamToFile(inputStream)
//        }

        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, option)
        return listOf(option.outWidth, option.outHeight)
    }

//    private fun File.copyInputStreamToFile(inputStream: InputStream) {
//        this.outputStream().use { out ->
//            inputStream.copyTo(out)
//        }
//    }

//    private fun imageRotation(imageFile: File): Int {
//        val rotation: Int
//        val exifInterface = ExifInterface(imageFile.absoluteFile)
//        val orientation = exifInterface.getAttributeInt(
//            ExifInterface.TAG_ORIENTATION,
//            ExifInterface.ORIENTATION_NORMAL
//        )
//        rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                90
//            } else {
//                0
//            }
//        return rotation
//    }

    fun chooseScaleType(imageView: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    suspend fun imageResize(uris: List<Uri>, activity: Activity): List<Bitmap> =
        withContext(Dispatchers.IO) {

            val tempList = ArrayList<List<Int>>()
            val bitmapList = ArrayList<Bitmap>()

            for (i in uris.indices) {
                val size = getImageSize(uris[i], activity)
                val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()

                if (imageRatio > 1) {
                    if (size[WIDTH] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                    } else {
                        tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                    }
                } else {
                    if (size[HEIGHT] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                    } else {
                        tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                    }
                }
            }

            for (i in uris.indices) {
                val exception = runCatching {
                    bitmapList.add(
                        Picasso.get().load(uris[i])
                            .resize(tempList[i][WIDTH], tempList[i][HEIGHT]).get()
                    )
                }
                Log.d("Loggg", "bitmap ${exception.isSuccess}")
            }
            return@withContext bitmapList
        }

    suspend fun getBitmapFromUri(uris: List<String?>): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val bitmapList = ArrayList<Bitmap>()

            for (i in uris.indices) {
                val exception = runCatching {
                    bitmapList.add(Picasso.get().load(uris[i]).get())
                }
                Log.d("Loggg", "bitmap ${exception.isSuccess}")
            }
            return@withContext bitmapList
        }
}