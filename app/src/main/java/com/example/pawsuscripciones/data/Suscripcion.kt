package com.example.pawsuscripciones.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suscripciones")
data class Suscripcion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val monto: Double,
    val fechaVencimiento: Long,
    val metodoPago: String,
    val etiqueta: String,
)