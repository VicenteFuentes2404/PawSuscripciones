package com.example.pawsuscripciones.data

import android.util.Log
import com.example.pawsuscripciones.data.network.SuscripcionApiService
import kotlinx.coroutines.flow.Flow
import com.example.pawsuscripciones.data.SuscripcionDao


// Inyectamos el servicio de la API (SuscripcionApiService)
class SuscripcionRepository(
    private val dao: SuscripcionDao,
    private val api: SuscripcionApiService
) {


    // La UI sigue observando la base de datos local (Room)
    fun getAll(): Flow<List<Suscripcion>> = dao.getAll()

    /**
     * Refresca la base de datos local con los datos de la API.
     * Se llama al iniciar la app.
     */
    suspend fun refreshSuscripciones() {
        try {
            val suscripcionesRemotas = api.getAll()
            Log.d("SuscripcionRepository", "Datos recibidos de la API: ${suscripcionesRemotas.size} items.")
            // Insertamos todos los datos de la API en Room, reemplazando los existentes.
            dao.insertAll(suscripcionesRemotas)
        } catch (e: Exception) {
            // Manejar error (ej. sin conexión a internet)
            Log.e("SuscripcionRepository", "Error al refrescar datos: ${e.message}")
            // Aquí podrías emitir un evento de error a la UI si lo deseas
        }
    }


    /**
     * Inserta una suscripción:
     * 1. La envía a la API.
     * 2. Recibe la suscripción guardada (con el ID real).
     * 3. Guarda esa suscripción en Room.
     * Devuelve la suscripción guardada.
     */
    suspend fun insert(s: Suscripcion): Suscripcion {
        try {
            // 1. Enviar a la API (con id=null si es nueva)
            val suscripcionGuardada = api.guardar(s)
            Log.d("SuscripcionRepository", "API guardó: ${suscripcionGuardada.id}")

            // 2. Guardar la respuesta de la API (con el ID correcto) en Room
            dao.insert(suscripcionGuardada)
            return suscripcionGuardada
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al insertar: ${e.message}")
            // Si falla, al menos la guardamos localmente?
            // Por ahora, solo relanzamos el error o devolvemos la original.
            // Para mantener la consistencia, es mejor que falle si la API falla.
            throw e // Relanzar para que el ViewModel sepa que falló
        }
    }

    /**
     * Elimina una suscripción:
     * 1. La elimina de la API (si tiene ID).
     * 2. Si la API tuvo éxito (o no fue necesario), la elimina de Room.
     */
    suspend fun delete(s: Suscripcion) {
        try {
            // Comprobamos si hay un ID
            s.id?.let { nonNullId ->
                // 1. SI HAY ID: Eliminar de la API primero.
                api.eliminar(nonNullId)
                Log.d("SuscripcionRepository", "API eliminó: $nonNullId")

                // 2. SI LA API TUVO ÉXITO: Eliminar de Room.
                // Si api.eliminar() falla, lanza una excepción y esto no se ejecuta.
                dao.delete(s)
                Log.d("SuscripcionRepository", "Room eliminó (post-API): ${s.id}")

            } ?: run {
                // 1. NO HAY ID: Solo eliminar de Room.
                // (Esto es para items locales que nunca se sincronizaron)
                dao.delete(s)
                Log.d("SuscripcionRepository", "Room eliminó (solo local): ${s.id}")
            }
        } catch (e: Exception) {
            // Si algo falla (principalmente la API), loguear y relanzar.
            // El ViewModel se encarga de 'catchear' esto.
            Log.e("SuscripcionRepository", "Error al eliminar: ${e.message}")
            throw e
        }
    }

    /**
     * Actualiza una suscripción:
     * 1. La envía a la API.
     * 2. Recibe la suscripción actualizada.
     * 3. Guarda la versión actualizada en Room.
     */
    suspend fun update(s: Suscripcion) {
        try {
            // Una actualización DEBE tener un ID.
            // Si s.id es nulo, lanzamos una excepción clara.
            val id = s.id ?: throw IllegalArgumentException("No se puede actualizar una suscripción sin ID")

            // 1. Actualizar en la API
            val suscripcionActualizada = api.actualizar(id, s)
            Log.d("SuscripcionRepository", "API actualizó: ${suscripcionActualizada.id}")

            // 2. Actualizar en Room
            dao.update(suscripcionActualizada)
        } catch (e: Exception) {
            Log.e("SuscripcionRepository", "Error al actualizar: ${e.message}")
            throw e
        }
    }

    suspend fun getById(id: Long): Suscripcion? = dao.getById(id)
}