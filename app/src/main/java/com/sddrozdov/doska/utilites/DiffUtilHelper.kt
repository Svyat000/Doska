package com.sddrozdov.doska.utilites

import androidx.recyclerview.widget.DiffUtil
import com.sddrozdov.doska.models.Ads

class DiffUtilHelper(private val oldList: List<Ads>,private val newList: List<Ads>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}