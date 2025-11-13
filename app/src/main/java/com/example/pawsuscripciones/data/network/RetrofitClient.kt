package com.example.pawsuscripciones.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton para crear y proveer la instancia de Retrofit.
 */
object RetrofitClient {

    // Esta es la URL pública de tu API en Render
    private const val BASE_URL = "https://pawsuscripcionesspring-5.onrender.com/"

    // Creamos un interceptor de logging para ver las llamadas en Logcat (solo para debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra todo: URL, Headers, Body
    }

    // Creamos un cliente OkHttp y le añadimos el interceptor
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Creamos la instancia de Retrofit usando lazy (se crea solo la primera vez que se usa)
    val instance: SuscripcionApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Añadimos el conversor de Gson
            .addConverterFactory(GsonConverterFactory.create())
            // Añadimos el cliente OkHttp (con el logger)
            .client(httpClient)
            .build()

        // Creamos la implementación de nuestra interfaz
        retrofit.create(SuscripcionApiService::class.java)
    }
}