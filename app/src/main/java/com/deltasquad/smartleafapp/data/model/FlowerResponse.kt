package com.deltasquad.smartleafapp.data.model

/**
 * Modelo que representa la respuesta del servidor de clasificación de flores.
 */
data class FlowerResponse(
    val success: Boolean,
    val class_supervised: String? = null,
    val confidence: Float? = null,
    val cluster_id: Int? = null,
    val class_cluster: String? = null,

    // 🌿 Campos adicionales opcionales (para llenar después)
    var userId: String? = null,
    var timestamp: String? = null,
    var location: String? = null,
    var notes: String? = null,
    var imageUrl: String? = null
)
