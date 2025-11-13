package com.example.pawsuscripciones.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawsuscripciones.data.AppDatabase
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.data.SuscripcionRepository
import com.example.pawsuscripciones.data.network.RetrofitClient
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

class SuscripcionViewModel(
    application: Application,
    private val notificationHelper: NotificationHelper
) : AndroidViewModel(application) {

    private val repo: SuscripcionRepository

    init {
        val db = AppDatabase.getInstance(application)

        // Creamos la instancia de la API y se la pasamos al Repositorio
        val apiService = RetrofitClient.instance
        repo = SuscripcionRepository(db.suscripcionDao(), apiService)
    }

    val suscripciones: StateFlow<List<Suscripcion>> =
        repo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    /**
     * Carga los datos desde la API y los guarda en Room.
     * La UI se actualizará automáticamente porque observa `suscripciones`.
     */
    fun refreshData() {
        Log.d("SuscripcionViewModel", "Iniciando refreshData...")
        viewModelScope.launch {
            try {
                repo.refreshSuscripciones()
                Log.d("SuscripcionViewModel", "refreshData completado.")
            } catch (e: Exception) {
                Log.e("SuscripcionViewModel", "Error en refreshData: ${e.message}")
                // Aquí podrías exponer un StateFlow<Error> a la UI
            }
        }
    }

    fun agregar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {

                // 'insert' ahora devuelve la suscripción con el ID real de la API
                val suscripcionGuardada = repo.insert(s)


                // Usamos el objeto devuelto (con ID real) para la notificación
                if (esFechaDeHoy(suscripcionGuardada.fechaVencimiento)) {
                    notificationHelper.showNotificationDemo(
                        "Una de tus Suscripciones vence hoy!",
                        "Tu suscripción a '${suscripcionGuardada.nombre}' vence hoy."
                    )
                }

                onDone?.invoke()
            } catch (e: Exception) {
                Log.e("SuscripcionViewModel", "Error al agregar: ${e.message}")
                // Manejar error, quizás mostrar un Toast/Snackbar al usuario
                // onDone? no se llama para que el usuario no navegue
            }
        }
    }

    fun eliminar(s: Suscripcion) {
        viewModelScope.launch {
            try {
                repo.delete(s)
            } catch (e: Exception) {
                Log.e("SuscripcionViewModel", "Error al eliminar: ${e.message}")
            }
        }
    }

    fun actualizar(s: Suscripcion, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                repo.update(s)

                if (esFechaDeHoy(s.fechaVencimiento)) {
                    notificationHelper.showNotificationDemo(
                        "Suscripción Actualizada",
                        "Tu suscripción a '${s.nombre}' vence hoy."
                    )
                }

                onDone?.invoke()
            } catch (e: Exception) {
                Log.e("SuscripcionViewModel", "Error al actualizar: ${e.message}")
            }
        }
    }

    suspend fun getSuscripcionById(id: Long): Suscripcion? {
        return repo.getById(id)
    }

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