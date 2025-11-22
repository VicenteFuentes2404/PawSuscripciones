package com.example.pawsuscripciones.data

import android.util.Log
import com.example.pawsuscripciones.data.network.DivisaApiService
import com.example.pawsuscripciones.data.network.DivisaResponse
import com.example.pawsuscripciones.data.network.SuscripcionApiService
import kotlinx.coroutines.flow.Flow


class SuscripcionRepository(
    private val dao: SuscripcionDao,
    private val api: SuscripcionApiService,
    private val divisaApi: DivisaApiService
) {

    // --- MÉTODOS EXISTENTES (Room y Backend) ---

    fun getAll(): Flow<List<Suscripcion>> = dao.getAll()

    suspend fun refreshSuscripciones() {
        try {
            val suscripcionesRemotas = api.getAll()
            Log.d("SuscripcionRepository", "Datos recibidos de la API: ${suscripcionesRemotas.size} items.")
            dao.insertAll(suscripcionesRemotas)
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al refrescar datos: ${e.message}")
        }
    }

    suspend fun insert(s: Suscripcion): Suscripcion {
        try {
            val suscripcionGuardada = api.guardar(s)
            dao.insert(suscripcionGuardada)
            return suscripcionGuardada
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al insertar: ${e.message}")
            throw e
        }
    }

    suspend fun delete(s: Suscripcion) {
        try {
            s.id?.let { nonNullId ->
                api.eliminar(nonNullId)
                dao.delete(s)
            } ?: run {
                dao.delete(s)
            }
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al eliminar: ${e.message}")
            throw e
        }
    }

    suspend fun update(s: Suscripcion) {
        try {
            val id = s.id ?: throw IllegalArgumentException("No se puede actualizar una suscripción sin ID")
            val suscripcionActualizada = api.actualizar(id, s)
            dao.update(suscripcionActualizada)
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al actualizar: ${e.message}")
            throw e
        }
    }

    suspend fun getById(id: Long): Suscripcion? = dao.getById(id)

    // --- NUEVO MÉTODO CORREGIDO (Divisas) ---

    suspend fun getExchangeRates(): DivisaResponse? {
        return try {
            // Llamamos a la API. ExchangeRate-API usa la moneda base en la URL
            val response = divisaApi.getExchangeRates(base = "CLP")

            if (response.rates.isNotEmpty()) {
                Log.d("SuscripcionRepository", "Tasas de cambio recibidas. Base: ${response.base}")
                response
            } else {
                Log.e("SuscripcionRepository", "La lista de tasas está vacía.")
                null
            }
        } catch (e: Exception) {
            // El Logcat te mostrará el error exacto si vuelve a fallar
            Log.e("SuscripcionRepository", "Error obteniendo divisas: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}