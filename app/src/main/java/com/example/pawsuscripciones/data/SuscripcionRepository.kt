package com.example.pawsuscripciones.data

import com.example.pawsuscripciones.data.SuscripcionDao
import com.example.pawsuscripciones.data.Suscripcion
import kotlinx.coroutines.flow.Flow

class SuscripcionRepository(private val dao: SuscripcionDao) {
    fun getAll(): Flow<List<Suscripcion>> = dao.getAll()
    suspend fun insert(s: Suscripcion) = dao.insert(s)
    suspend fun delete(s: Suscripcion) = dao.delete(s)

    suspend fun update(s: Suscripcion) = dao.update(s)
    suspend fun getById(id: Long): Suscripcion? = dao.getById(id)
}