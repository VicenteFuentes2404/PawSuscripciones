package com.example.pawsuscripciones.data.network

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface para la API de Exchangerate-API (Versión abierta/gratuita).
 * Documentación: https://www.exchangerate-api.com/docs/free
 */
interface DivisaApiService {

    // La URL completa será: https://api.exchangerate-api.com/v4/latest/CLP
    // Usamos @Path para reemplazar {base} por "CLP"
    @GET("v4/latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") base: String = "CLP"
    ): DivisaResponse
}


data class DivisaResponse(
    val base: String,
    val rates: Map<String, Double> // Mapa de tasas (ej: "USD" -> 0.0011)
)