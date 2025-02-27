package com.sddrozdov.doska.utilites

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.Locale

object CityHelper {
    // Метод для получения всех стран из JSON файла
    fun getAllCountries(context: Context): ArrayList<String> {
        val countryArray = ArrayList<String>() // Список для хранения названий стран
        try {
            // Открытие потока для чтения JSON файла
            val inputStream: InputStream = context.assets.open("countriesAndTheirCities.json")
            val size: Int = inputStream.available() // Получение размера файла
            val bytesArray = ByteArray(size) // Создание массива байтов
            inputStream.read(bytesArray) // Чтение файла в массив байтов
            val jsonFileContent = String(bytesArray) // Преобразование массива байтов в строку
            val jsonObject = JSONObject(jsonFileContent) // Создание JSON объекта из строки
            val countriesNames = jsonObject.names() // Получение имен стран из JSON объекта
            if (countriesNames != null) {
                for (i in 0 until countriesNames.length()) {
                    // Добавление названий стран в список
                    countryArray.add(countriesNames.getString(i))
                }
            }

        } catch (e: IOException) {
            // Обработка исключений (например, вывод в лог)
            e.printStackTrace()
        }
        return countryArray // Возвращаем список стран
    }

    // Метод для фильтрации списка данных на основе введенного текста
    fun filterListData(countryList: ArrayList<String>, searchText: String?): ArrayList<String> {
        val filteredList = ArrayList<String>() // Список для хранения отфильтрованных данных
        filteredList.clear() // Очищаем список
        if (searchText == null || searchText.isEmpty()) {
            filteredList.add("No result") // Если текст пустой, добавляем сообщение о результате
            return filteredList // Возвращаем список с сообщением
        }
        // Ищем совпадения по введенному тексту
        for (country: String in countryList) {
            // Проверяем, начинается ли название страны с введенного текста (без учета регистра)
            if (country.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT))) {
                filteredList.add(country) // Добавляем страну в отфильтрованный список
            }
        }
        // Если ничего не найдено, добавляем сообщение о результате
        if (filteredList.isEmpty()) {
            filteredList.add("No result") // Добавляем сообщение о том, что результатов нет
        }
        return filteredList // Возвращаем отфильтрованный список
    }
}