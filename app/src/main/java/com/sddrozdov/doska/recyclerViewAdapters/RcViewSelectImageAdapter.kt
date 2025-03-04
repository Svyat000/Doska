package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.SelectImageItemInFragmentBinding
import com.sddrozdov.doska.models.SelectImageItem
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback

class RcViewSelectImageAdapter :
    RecyclerView.Adapter<RcViewSelectImageAdapter.SelectImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<SelectImageItem>()

    class SelectImageHolder(private val binding: SelectImageItemInFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: SelectImageItem) {
            binding.selImageItemTitle.text = item.title
            binding.imageItemInFragment.setImageURI(item.imageUri.toUri())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SelectImageItemInFragmentBinding.inflate(inflater, parent, false)
        return SelectImageHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    fun updateAdapter(newList: List<SelectImageItem>, needClear: Boolean) {
        if(needClear){
           TODO()
        }
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onMove(startPosition: Int, targetPosition: Int) {
        val targetItem = mainArray[targetPosition]
        mainArray[targetPosition] = mainArray[startPosition]
        val titleStart = mainArray[targetPosition].title
        mainArray[targetPosition].title = targetItem.title
        mainArray[startPosition] = targetItem
        mainArray[startPosition].title = titleStart
        notifyItemMoved(startPosition,targetPosition)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

}