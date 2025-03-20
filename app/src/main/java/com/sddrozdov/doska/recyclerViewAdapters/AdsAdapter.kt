package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sddrozdov.doska.databinding.AdListItemBinding
import com.sddrozdov.doska.models.Ads

class AdsAdapter(private val auth: FirebaseAuth) : RecyclerView.Adapter<AdsAdapter.AdsViewHolder>() {

    val adsArray = ArrayList<Ads>()

    class AdsViewHolder(private val binding: AdListItemBinding, val auth: FirebaseAuth) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ads: Ads) {
            binding.apply {
                tvDescription.text = ads.description
                tvPrice.text = ads.price
                tvTitle.text = ads.title
            }
            showEditPanel(isOwner(ads))
        }

        private fun isOwner(ads: Ads): Boolean = ads.uid == auth.uid

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) binding.editPanel.visibility = View.VISIBLE
            else binding.editPanel.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsViewHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdsViewHolder(binding, auth)
    }

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
        return adsArray.size
    }

    fun updateAdapter(newAdsArray: List<Ads>) {
        adsArray.clear()
        adsArray.addAll(newAdsArray)
        notifyDataSetChanged()
    }
}