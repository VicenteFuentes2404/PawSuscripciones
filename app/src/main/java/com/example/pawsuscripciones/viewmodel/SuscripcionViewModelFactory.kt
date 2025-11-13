package com.example.pawsuscripciones.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawsuscripciones.data.AppDatabase
import com.example.pawsuscripciones.data.SuscripcionRepository
import com.example.pawsuscripciones.data.network.RetrofitClient
import com.example.pawsuscripciones.data.network.RetrofitClientDivisa
import com.example.pawsuscripciones.notifications.NotificationHelper

class SuscripcionViewModelFactory(
    private val application: Application,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuscripcionViewModel::class.java)) {

            // 1. Obtenemos la BD
            val db = AppDatabase.getInstance(application)

            // 2. Obtenemos las APIs
            val apiService = RetrofitClient.instance
            val divisaApiService = RetrofitClientDivisa.instance

            // 3. Creamos el Repositorio
            val repo = SuscripcionRepository(db.suscripcionDao(), apiService, divisaApiService)

            @Suppress("UNCHECKED_CAST")
            // 4. Pasamos todo al ViewModel
            return SuscripcionViewModel(application, notificationHelper, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}