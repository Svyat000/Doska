package com.sddrozdov.doska.recyclerViewAdapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.MainActivity
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.AdListItemBinding
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.utilites.DiffUtilHelper
import com.squareup.picasso.Picasso

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
                tvViewCounter.text = ads.viewsCounter
                tvFavCounter.text = ads.favoriteCounter
                Log.d("AdsAdapter", "Loading image from URL: ${ads.mainImage}")

                Picasso.get().load(ads.mainImage).into(mainImage)

                isFav(ads)
                showEditPanel(isOwner(ads))
                mainOnClick(ads)

            }
        }

        private fun mainOnClick(ads: Ads) = with(binding) {
            itemView.setOnClickListener {
                mainActivity.onAdViewed(ads)
            }
            ibFav.setOnClickListener {
                if (mainActivity.mAuth.currentUser?.isAnonymous == false)
                    mainActivity.onFavoriteCLicked(ads)
            }
            ibEditAd.setOnClickListener(onClickEdit(ads))
            ibDeleteAd.setOnClickListener {
                mainActivity.onDeleteItem(ads)
            }
        }

        private fun isFav(ads: Ads) {
            if (ads.isFavorite) binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            else binding.ibFav.setImageResource(R.drawable.ic_fav_normal)
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
        val tempArray = ArrayList<Ads>()
        tempArray.addAll(adsArray)
        tempArray.addAll(newAdsArray)
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adsArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        adsArray.clear()
        adsArray.addAll(tempArray)
    }

    interface ItemListener {
        fun onDeleteItem(ads: Ads)
        fun onAdViewed(ads: Ads)
        fun onFavoriteCLicked(ads: Ads)
    }
}