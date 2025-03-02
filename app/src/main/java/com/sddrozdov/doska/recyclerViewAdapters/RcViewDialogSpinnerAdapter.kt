package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.SpListItemBinding

class RcViewDialogSpinnerAdapter(
    private val editAdsActSelectCountryOrCity: TextView,
    private val dialog: AlertDialog
) :
    RecyclerView.Adapter<RcViewDialogSpinnerAdapter.CountryViewHolder>() {

    private val tempListForCountriesAndCities = ArrayList<String>()

    class CountryViewHolder(
        private val binding: SpListItemBinding,
        private val editAdsActSelectCountryOrCity: TextView,
        private var dialog: AlertDialog
    ) : RecyclerView.ViewHolder(binding.root) {

        private var itemText = ""

        fun setTextInTextView(countryName: String) {
            binding.tvSpItem.text = countryName
            itemText = countryName
            binding.root.setOnClickListener {
                editAdsActSelectCountryOrCity.text = itemText
                dialog.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SpListItemBinding.inflate(inflater, parent, false)
        return CountryViewHolder(binding, editAdsActSelectCountryOrCity, dialog)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.setTextInTextView(tempListForCountriesAndCities[position])
    }

    override fun getItemCount(): Int {
        return tempListForCountriesAndCities.size
    }

    fun updateAdapter(newCountryOrCitiesList: ArrayList<String>) {
        tempListForCountriesAndCities.clear()
        tempListForCountriesAndCities.addAll(newCountryOrCitiesList)
        notifyDataSetChanged()
    }
}
