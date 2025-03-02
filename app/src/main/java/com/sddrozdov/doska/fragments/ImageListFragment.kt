package com.sddrozdov.doska.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.sddrozdov.doska.R

class ImageListFragment(private val onFragmentCloseInterface: FragmentCloseInterface) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_list_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bBack = view.findViewById<Button>(R.id.ImageFragmentBack)
        bBack.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentCloseInterface.onFragClose()
    }


}