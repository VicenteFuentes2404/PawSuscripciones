package com.example.pawsuscripciones.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suscripciones")
data class Suscripcion(

    @PrimaryKey val id: Long? = null,
    val nombre: String,
    val monto: Double,
    val fechaVencimiento: Long,
    val metodoPago: String,
    val etiqueta: String,
)