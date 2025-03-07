package com.sddrozdov.doska.recyclerViewAdapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.ImageAdapterItemBinding

class ImageAdapterForViewPager : RecyclerView.Adapter<ImageAdapterForViewPager.ImageViewHolder>() {

    val imageArray = ArrayList<Bitmap>()

    class ImageViewHolder(private val binding: ImageAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(bitmap: Bitmap) {
            binding.imageViewForViewPager.setImageBitmap(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageAdapterItemBinding.inflate(inflater,parent,false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.setData(imageArray[position])
    }

    override fun getItemCount(): Int {
        return imageArray.size
    }

    fun updateAdapter(newImageArray: ArrayList<Bitmap>) {
        imageArray.clear()
        imageArray.addAll(newImageArray)
        notifyDataSetChanged()
    }
}