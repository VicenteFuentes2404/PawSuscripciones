package com.example.pawsuscripciones.data

import androidx.room.*
import com.example.pawsuscripciones.data.Suscripcion
import kotlinx.coroutines.flow.Flow

@Dao
interface SuscripcionDao {
    @Query("SELECT * FROM suscripciones ORDER BY fechaVencimiento ASC")
    fun getAll(): Flow<List<Suscripcion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(suscripcion: Suscripcion): Long

    @Delete
    suspend fun delete(suscripcion: Suscripcion)

    @Query("SELECT * FROM suscripciones WHERE id = :id")
    suspend fun getById(id: Long): Suscripcion?
}