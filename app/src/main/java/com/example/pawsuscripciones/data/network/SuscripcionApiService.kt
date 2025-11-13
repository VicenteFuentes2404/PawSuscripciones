package com.example.pawsuscripciones.data.network

import com.example.pawsuscripciones.data.Suscripcion
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Path

/**
 * Interface de Retrofit que define los endpoints de la API de Spring Boot.
 * Coincide con tu SuscripcionController.
 */
interface SuscripcionApiService {

    // Coincide con: @GetMapping
    @GET("api/suscripciones")
    suspend fun getAll(): List<Suscripcion>

    // Coincide con: @GetMapping("/{id}")
    @GET("api/suscripciones/{id}")
    suspend fun getById(@Path("id") id: Long): Suscripcion

    // Coincide con: @PostMapping
    // Devuelve la Suscripcion guardada (con el ID generado por la base de datos)
    @POST("api/suscripciones")
    suspend fun guardar(@Body suscripcion: Suscripcion): Suscripcion

    // Coincide con: @PutMapping("/{id}")
    // Devuelve la Suscripcion actualizada
    @PUT("api/suscripciones/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body suscripcion: Suscripcion): Suscripcion

    // Coincide con: @DeleteMapping("/{id}")
    @DELETE("api/suscripciones/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit> // No devuelve contenido (Void)
}