package com.sddrozdov.doska.recyclerViewAdapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.act.MainActivity
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.AdListItemBinding
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.utilites.DiffUtilHelper

class AdsAdapter(private val mainActivity: MainActivity) :
    RecyclerView.Adapter<AdsAdapter.AdsViewHolder>() {

    val adsArray = ArrayList<Ads>()

    class AdsViewHolder(private val binding: AdListItemBinding, val mainActivity: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ads: Ads) {
            binding.apply {
                tvDescription.text = ads.description
                tvPrice.text = ads.price
                tvTitle.text = ads.title
            }
            showEditPanel(isOwner(ads))
            binding.ibEditAd.setOnClickListener(onClickEdit(ads))
            binding.ibDeleteAd.setOnClickListener {
                mainActivity.onDeleteItem(ads)
            }
        }

        private fun isOwner(ads: Ads): Boolean = ads.uid == mainActivity.mAuth.uid

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) binding.editPanel.visibility = View.VISIBLE
            else binding.editPanel.visibility = View.GONE
        }

        private fun onClickEdit(ads: Ads): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(mainActivity, EditAdsActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ads)
                }
                mainActivity.startActivity(editIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsViewHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdsViewHolder(binding, mainActivity)
    }

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
        return adsArray.size
    }

    fun updateAdapter(newAdsArray: List<Ads>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adsArray, newAdsArray))
        diffResult.dispatchUpdatesTo(this)
        adsArray.clear()
        adsArray.addAll(newAdsArray)
    }

    interface DeleteItemListener {
        fun onDeleteItem(ads: Ads)
    }
}