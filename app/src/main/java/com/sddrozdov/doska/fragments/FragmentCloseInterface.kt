package com.sddrozdov.doska.fragments

import com.sddrozdov.doska.models.SelectImageItem

interface FragmentCloseInterface {
    fun onFragClose(list : ArrayList<SelectImageItem>)
}