package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.AdListItemBinding
import com.sddrozdov.doska.models.Ads

class AdsAdapter : RecyclerView.Adapter<AdsAdapter.AdsViewHolder>() {

    val adsArray = ArrayList<Ads>()

    class AdsViewHolder(private val binding: AdListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ads: Ads) {
            binding.apply {
                tvDescription.text = ads.description
                tvPrice.text = ads.price

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsViewHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context))
        return AdsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
        return adsArray.size
    }

    fun updateAdapter(newAdsArray: ArrayList<Ads>) {
        adsArray.clear()
        adsArray.addAll(newAdsArray)
        notifyDataSetChanged()
    }
}