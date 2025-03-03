package com.sddrozdov.doska.recyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.SelectImageItemInFragmentBinding
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback

class RcViewSelectImageAdapter :
    RecyclerView.Adapter<RcViewSelectImageAdapter.SelectImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<String>()

    class SelectImageHolder(private val binding: SelectImageItemInFragmentBinding,private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: String) {
            //binding.selImageItemTitle.text = item.title
            binding.selImageItemTitle.text = context.resources.getStringArray(R.array.title_image_array)[adapterPosition]
            binding.imageItemInFragment.setImageURI(item.toUri())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SelectImageItemInFragmentBinding.inflate(inflater, parent, false)
        return SelectImageHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: SelectImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    fun updateAdapter(newList: List<String>, needClear: Boolean) {
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
        mainArray[startPosition] = targetItem
        notifyItemMoved(startPosition,targetPosition)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }


}