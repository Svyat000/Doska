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
import com.sddrozdov.doska.recyclerViewAdapters.RcViewDialogSpinnerAdapter
import com.sddrozdov.doska.utilites.CityHelper

class DialogSpinnerHelper {
    // Метод для показа диалогового окна
    fun showSpinnerDialog(
        context: Context,
        countryList: ArrayList<String>,
        editAdsActSelectCountryOrCity: TextView
    ) {
        // Проверка, что контекст не null
        if (context is Activity) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            val dialog = alertDialogBuilder.create()

            // Надувание пользовательского макета для диалогового окна
            val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)

            val adapter = RcViewDialogSpinnerAdapter(editAdsActSelectCountryOrCity, dialog)
            // Получение ссылки на RecyclerView из макета

            val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewSpinnerView)

            // Получение ссылки на SearchView из макета
            val searchView = rootView.findViewById<SearchView>(R.id.svSpinner)

            // Установка менеджера компоновки для RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(context)

            // Установка адаптера для RecyclerView
            recyclerView.adapter = adapter

            // Установка пользовательского представления в диалоге
            dialog.setView(rootView)

            // Обновление адаптера с первоначальным списком стран
            adapter.updateAdapter(countryList)

            // Настройка SearchView
            setSearchView(adapter, countryList, searchView)

            // Показ диалогового окна
            dialog.show()
        } else {
            throw IllegalArgumentException("Context must be an Activity")
        }
    }

    // Метод для настройки SearchView
    private fun setSearchView(
        adapter: RcViewDialogSpinnerAdapter,
        countryList: ArrayList<String>,
        searchView: SearchView?
    ) {
        // Установка слушателя для обработки текстовых изменений в SearchView
        searchView?.setOnQueryTextListener(object : OnQueryTextListener {
            // Метод вызывается при отправке текста
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false //
            }

            // Метод вызывается при изменении текста
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