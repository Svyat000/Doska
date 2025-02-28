package com.sddrozdov.doska.utilites

import android.net.Uri
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import io.ak1.pix.models.VideoOptions

object ImagePicker {

    fun getImages() {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO                       //Image/video capture ratio
            count = 1                        //Number of images to restrict selection count
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

    }
    
}
