package com.sddrozdov.doska.recyclerViewAdapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.SelectImageItemInFragmentBinding
import com.sddrozdov.doska.interfaces.AdapterCallback
import com.sddrozdov.doska.utilites.ImageManager
import com.sddrozdov.doska.utilites.ImagePicker
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback

class SelectImageAdapterInFragment(private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<SelectImageAdapterInFragment.SelectImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<Bitmap>()

    class SelectImageHolder(private val binding: SelectImageItemInFragmentBinding, private val context: Context, val adapter : SelectImageAdapterInFragment) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(bitmapItem: Bitmap) {
            binding.editImageButton.setOnClickListener {
                val test = context as EditAdsActivity//
                ImagePicker.launcher(context, 1)
                test.editImagePos = adapterPosition
            }
            binding.imageDelete.setOnClickListener {
                adapter.mainArray. removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for(i in 0 until adapter.mainArray.size){
                    adapter.notifyItemChanged(i)
                }
                adapter.adapterCallback.onItemDelete()
            }

            binding.selImageItemTitle.text = context.resources.getStringArray(R.array.title_image_array)[adapterPosition]
            ImageManager.chooseScaleType(binding.imageItemInFragment,bitmapItem)
            binding.imageItemInFragment.setImageBitmap(bitmapItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SelectImageItemInFragmentBinding.inflate(inflater, parent, false)
        return SelectImageHolder(binding,parent.context,this)
    }

    override fun onBindViewHolder(holder: SelectImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    fun updateAdapter(newList: List<Bitmap>, needClear: Boolean) {
        if(needClear) mainArray.clear()
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