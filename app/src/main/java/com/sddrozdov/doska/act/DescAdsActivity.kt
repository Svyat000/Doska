package com.sddrozdov.doska.act

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sddrozdov.doska.databinding.ActivityDescAdsBinding


class DescAdsActivity : AppCompatActivity() {

    private var _binding: ActivityDescAdsBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDescAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)

    }
}