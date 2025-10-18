package com.example.pawsuscripciones.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawsuscripciones.notifications.NotificationHelper

class SuscripcionViewModelFactory(
    private val application: Application,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuscripcionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SuscripcionViewModel(application, notificationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}