package com.example.pawsuscripciones.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.data.SuscripcionRepository
import com.example.pawsuscripciones.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

data class FormValidationResult(
    val ok: Boolean,
    val nombreError: String? = null,
    val montoError: String? = null
)

data class DivisaState(
    val selectedCode: String = "CLP",
    val rates: Map<String, Double> = emptyMap(),
    val error: String? = null
)

class SuscripcionViewModel(
    application: Application,
    private val notificationHelper: NotificationHelper,
    private val repo: SuscripcionRepository
) : AndroidViewModel(application) {

    private val _divisaState = MutableStateFlow(DivisaState())
    val divisaState: StateFlow<DivisaState> = _divisaState


    val suscripciones: StateFlow<List<Suscripcion>> =
        repo.getAll().stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptyList())

    fun refreshData() {
        viewModelScope.launch {
            try {
                repo.refreshSuscripciones()
                val response = repo.getExchangeRates()
                if (response != null && response.rates.isNotEmpty()) {
                    _divisaState.value = _divisaState.value.copy(rates = response.rates, error = null)
                } else {
                    _divisaState.value = _divisaState.value.copy(error = "Error cargando divisas")
                }
            } catch (e: Exception) {
                Log.e("SuscripcionViewModel", "Error: ${e.message}")
            }
        }
    }

    fun setSelectedDivisa(code: String) {
        _divisaState.value = _divisaState.value.copy(selectedCode = code)
    }

    fun getConvertedAmount(totalCLP: Double): Double {
        val selectedCode = _divisaState.value.selectedCode
        if (selectedCode == "CLP") return totalCLP
        val rate = _divisaState.value.rates[selectedCode] ?: return totalCLP
        return totalCLP * rate
    }

    fun agregar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val guardada = repo.insert(s)
                if (esFechaDeHoy(guardada.fechaVencimiento)) {
                    notificationHelper.showNotificationDemo("Vence hoy", "Tu suscripción vence hoy")
                }
                onDone?.invoke()
            } catch (e: Exception) {
                Log.e("VM", "Error: ${e.message}")
            }
        }
    }

    fun eliminar(s: Suscripcion) {
        viewModelScope.launch { try { repo.delete(s) } catch (e: Exception) {} }
    }

    fun actualizar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                repo.update(s)
                onDone?.invoke()
            } catch (e: Exception) {}
        }
    }

    suspend fun getSuscripcionById(id: Long): Suscripcion? = repo.getById(id)

    private fun esFechaDeHoy(fechaMillis: Long): Boolean {
        val hoy = Calendar.getInstance()
        val fechaSuscripcion = Calendar.getInstance().apply { timeInMillis = fechaMillis }
        return hoy.get(Calendar.YEAR) == fechaSuscripcion.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) == fechaSuscripcion.get(Calendar.DAY_OF_YEAR)
    }

    fun validarFormulario(nombre: String, montoText: String?): FormValidationResult {
        var nombreErr: String? = null
        var montoErr: String? = null

        if (nombre.isBlank()) {
            nombreErr = "Este campo es requerido"
        }

        val montoVal = montoText?.toDoubleOrNull()
        if (montoText.isNullOrBlank()) {
            montoErr = "Ingresa un monto"
        } else if (montoVal == null || montoVal <= 0.0) {
            montoErr = "Ingresa un monto válido mayor a 0"
        }

        val ok = nombreErr == null && montoErr == null
        return FormValidationResult(ok = ok, nombreError = nombreErr, montoError = montoErr)
    }
}