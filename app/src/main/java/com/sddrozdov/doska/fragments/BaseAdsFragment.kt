package com.sddrozdov.doska.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

open class BaseAdsFragment : Fragment() {

    //lateinit var adView: AdView
    //var interAds: InterstatialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialAds()
    }

    override fun onResume() {
        super.onResume()
        //adView.resume()

    }

    override fun onPause() {
        super.onPause()
        //adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //adView.destroy()
    }

    private fun initialAds() {
        //MobileAds.initialize(activity as Activity)
    }

}