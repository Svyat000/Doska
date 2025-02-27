package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R

class RcViewDialogSpinnerAdapter(
    private val editAdsActSelectCountryOrCity: TextView,
    private val dialog: AlertDialog
) :
    RecyclerView.Adapter<RcViewDialogSpinnerAdapter.CountryViewHolder>() {

    private val tempListForCountriesAndCities = ArrayList<String>()

    // ВьюХолдер для отдельного элемента списка
    class CountryViewHolder(
        itemView: View,
        private val editAdsActSelectCountryOrCity: TextView,
        var dialog: AlertDialog
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var itemText = ""

        // Метод для установки текста в TextView
        fun setTextInTextView(countryName: String) {
            // Получение ссылки на TextView из элемента списка
            val countryLinkFromElement = itemView.findViewById<TextView>(R.id.tvSpItem)
            // Установка имени страны в TextView
            countryLinkFromElement.text = countryName
            itemText = countryName
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            editAdsActSelectCountryOrCity.text = itemText
            dialog.dismiss()
        }
    }

    // Метод для создания нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        // Инфляция макета элемента списка
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return CountryViewHolder(
            itemView,
            editAdsActSelectCountryOrCity,
            dialog
        ) // Возвращаем новый ViewHolder
    }

    // Метод для привязки данных к ViewHolder
    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        // Установка данных для текущего элемента списка
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