package com.sddrozdov.doska.recyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity

class RcViewDialogSpinnerAdapter(private val context: Context, private val dialog: AlertDialog) :
    RecyclerView.Adapter<RcViewDialogSpinnerAdapter.CountryViewHolder>() {

    private val countryList = ArrayList<String>()

    // ВьюХолдер для отдельного элемента списка
    class CountryViewHolder(itemView: View, private val context: Context, var dialog: AlertDialog) :
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
            (context as EditAdsActivity).binding.EditAdsActSelectCountry.text = itemText
            dialog.dismiss()
        }
    }

    // Метод для создания нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        // Инфляция макета элемента списка
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return CountryViewHolder(itemView, context, dialog) // Возвращаем новый ViewHolder
    }

    // Метод для привязки данных к ViewHolder
    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        // Установка данных для текущего элемента списка
        holder.setTextInTextView(countryList[position])
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    fun updateAdapter(newCountryList: ArrayList<String>) {
        countryList.clear()
        countryList.addAll(newCountryList)
        notifyDataSetChanged()
    }
}