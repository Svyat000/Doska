package com.sddrozdov.doska.dialogs

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.recyclerViewAdapters.DialogSpinnerAdapterForCitiesAndCountries
import com.sddrozdov.doska.utilites.CityHelper

class DialogSpinnerHelper {
    // Метод для показа диалогового окна
    fun showSpinnerDialog(context: Context, countryList: ArrayList<String>, editAdsActSelectCountryOrCity: TextView) {
        if (context is Activity) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            val dialog = alertDialogBuilder.create()
            val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
            val adapter = DialogSpinnerAdapterForCitiesAndCountries(editAdsActSelectCountryOrCity, dialog)
            val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewSpinnerView)
            val searchView = rootView.findViewById<SearchView>(R.id.svSpinner)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            dialog.setView(rootView)
            adapter.updateAdapter(countryList)
            setSearchView(adapter, countryList, searchView)
            dialog.show()
        } else {
            throw IllegalArgumentException("Context must be an Activity")
        }
    }

    // Метод для настройки SearchView
    private fun setSearchView(adapter: DialogSpinnerAdapterForCitiesAndCountries, countryList: ArrayList<String>,
                              searchView: SearchView?) {
        // Установка слушателя для обработки текстовых изменений в SearchView
        searchView?.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false //
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Фильтрация списка стран на основе введенного текста
                val filteredList = CityHelper.filterListData(countryList, newText)
                // Обновление адаптера с отфильтрованным списком
                adapter.updateAdapter(filteredList)
                return true // Указываем, что текст был изменен
            }
        })
    }
}