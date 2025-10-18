package com.example.pawsuscripciones.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawsuscripciones.data.AppDatabase
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.data.SuscripcionRepository
import com.example.pawsuscripciones.notifications.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

data class FormValidationResult(
    val ok: Boolean,
    val nombreError: String? = null,
    val montoError: String? = null
)

// 1. Modificamos el constructor para que acepte el NotificationHelper
class SuscripcionViewModel(
    application: Application,
    private val notificationHelper: NotificationHelper // <-- AÑADIDO
) : AndroidViewModel(application) {

    private val repo: SuscripcionRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = SuscripcionRepository(db.suscripcionDao())
    }

    val suscripciones: StateFlow<List<Suscripcion>> =
        repo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 2. Modificamos la función 'agregar' para que revise la fecha
    fun agregar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insert(s)

            // 3. Lógica para comprobar si la fecha es hoy y lanzar la notificación
            if (esFechaDeHoy(s.fechaVencimiento)) {
                notificationHelper.showNotificationDemo(
                    "Una de tus Suscripciones vence hoy!",
                    "Tu suscripción a '${s.nombre}' vence hoy."
                )
            }

            onDone?.invoke()
        }
    }

    fun eliminar(s: Suscripcion) {
        viewModelScope.launch {
            repo.delete(s)
        }
    }

    // 4. Función auxiliar para comprobar si la fecha es hoy
    private fun esFechaDeHoy(fechaMillis: Long): Boolean {
        val hoy = Calendar.getInstance()
        val fechaSuscripcion = Calendar.getInstance().apply {
            timeInMillis = fechaMillis
        }
        return hoy.get(Calendar.YEAR) == fechaSuscripcion.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) == fechaSuscripcion.get(Calendar.DAY_OF_YEAR)
    }

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