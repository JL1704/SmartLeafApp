package com.deltasquad.smartleafapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que configura y proporciona una instancia de Retrofit para consumir la API de detección de placas.
 */
object RetrofitClient {

    // URL base del backend. Reemplaza con la URL/IP correspondiente si cambia el entorno.
    private const val BASE_URL = "https://platescanbackend.onrender.com/"

    /**
     * Instancia perezosa (lazy) del servicio de la API.
     * Se construye solo una vez y se reutiliza durante toda la ejecución de la app.
     */
    val api: PlateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Conversor para parsear JSON con Gson
            .build()
            .create(PlateApiService::class.java)
    }
}
