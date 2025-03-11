package com.sddrozdov.doska.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sddrozdov.doska.databinding.ImageListFragmentBinding

open class BaseSelectImageFragment : Fragment() {

    lateinit var binding : ImageListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ImageListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
}