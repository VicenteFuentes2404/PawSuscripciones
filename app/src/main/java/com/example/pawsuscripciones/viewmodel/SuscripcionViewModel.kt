package com.example.pawsuscripciones.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawsuscripciones.data.AppDatabase
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.data.SuscripcionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormValidationResult(
    val ok: Boolean,
    val nombreError: String? = null,
    val montoError: String? = null
)

class SuscripcionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: SuscripcionRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = SuscripcionRepository(db.suscripcionDao())
    }

    // Exponemos un StateFlow para que la UI lo observe de forma reactiva
    val suscripciones: StateFlow<List<Suscripcion>> =
        repo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun agregar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insert(s)
            onDone?.invoke()
        }
    }

    fun eliminar(s: Suscripcion) {
        viewModelScope.launch {
            repo.delete(s)
        }
    }

    // Validación centralizada y desacoplada de la UI
    fun validarFormulario(nombre: String, montoText: String?): FormValidationResult {
        var nombreErr: String? = null
        var montoErr: String? = null
        if (nombre.isBlank()) nombreErr = "Este campo es requerido"
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