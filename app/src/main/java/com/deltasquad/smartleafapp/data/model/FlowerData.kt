package com.deltasquad.smartleafapp.data.model

import com.google.firebase.Timestamp

/**
 * Modelo que representa los datos de una flor para Firestore / UI.
 *
 * Notas:
 * - Mantén los nombres de propiedad iguales que en los documentos de Firestore si quieres
 *   usar `document.toObject(FlowerData::class.java)`.
 * - Usamos `Timestamp` de Firebase para createdAt/updatedAt; si prefieres epoch (Long) cámbialo.
 */
data class FlowerData(
    val id: String? = null,                      // ID del documento en Firestore (opcional)
    val name: String = "",                       // Nombre común
    val scientificName: String = "",             // Nombre científico / latino
    val family: String = "",                     // Familia botánica
    val origin: String = "",                     // Origen geográfico
    val description: String = "",                // Descripción general de la planta/fiore
    val careTips: String = "",                   // Consejos generales de cuidado
    val watering: String = "",                   // Frecuencia de riego (p.ej. "1 vez/semana")
    val lightRequirements: String = "",          // Requerimiento de luz (p.ej. "sol parcial")
    val soilType: String = "",                   // Tipo de suelo recomendado
    val bloomSeason: String = "",                // Temporada de floración (p.ej. "primavera")
    val toxicity: String = "",                   // Toxicidad (p.ej. "no tóxica", "tóxica para mascotas")
    val maintenance: List<String> = emptyList(), // Lista de tareas de mantenimiento (p.ej. ["podar en marzo", "fertilizar"])
    val tags: List<String> = emptyList(),        // Etiquetas libres (p.ej. ["interior","suculenta"])
    val imageUrl: String = "",                   // URL a imagen principal
    val extraImages: List<String> = emptyList(), // URLs adicionales

) {
    // Constructor vacío ya está cubierto por los valores por defecto,
    // pero si prefieres explícito:
    constructor() : this(
        id = null,
        name = "",
        scientificName = "",
        family = "",
        origin = "",
        description = "",
        careTips = "",
        watering = "",
        lightRequirements = "",
        soilType = "",
        bloomSeason = "",
        toxicity = "",
        maintenance = emptyList(),
        tags = emptyList(),
        imageUrl = "",
        extraImages = emptyList(),
    )
}
