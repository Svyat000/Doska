package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.ImageAdapterItemBinding
import com.sddrozdov.doska.models.SelectImageItem

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val imageArray = ArrayList<SelectImageItem>()

    class ImageViewHolder(private val binding: ImageAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(uri: String) {
            binding.imageViewForViewPager.setImageURI(uri.toUri())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageAdapterItemBinding.inflate(inflater,parent,false)
        return ImageViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.setData(imageArray[position].imageUri)
    }

    override fun getItemCount(): Int {
        return imageArray.size
    }

    fun updateAdapter(newImageArray: ArrayList<SelectImageItem>) {
        imageArray.clear()
        imageArray.addAll(newImageArray)
        notifyDataSetChanged()
    }
}