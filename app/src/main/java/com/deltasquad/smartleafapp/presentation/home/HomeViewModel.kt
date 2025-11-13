package com.deltasquad.smartleafapp.presentation.home

import androidx.lifecycle.ViewModel
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _latestFlowers = MutableStateFlow<List<FlowerResponse>>(emptyList())
    val latestFlowers: StateFlow<List<FlowerResponse>> = _latestFlowers

    private val _filteredFlowers = MutableStateFlow<List<FlowerResponse>>(emptyList())
    val filteredFlowers: StateFlow<List<FlowerResponse>> = _filteredFlowers

    private var allFlowers: List<FlowerResponse> = emptyList()

    /** 🔹 Obtiene las 5 clasificaciones más recientes */
    fun fetchLatestFlowers() {
        val user = auth.currentUser ?: return
        db.collection("users")
            .document(user.uid)
            .collection("flowers")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val flowers = result.mapNotNull { doc ->
                    val flower = doc.toObject(FlowerResponse::class.java)
                    flower?.copy(id = doc.id) // 💡 Guardamos el ID del documento
                }
                _latestFlowers.value = flowers
            }
    }

    /** 🔹 Carga todas las flores clasificadas */
    fun fetchAllFlowers() {
        val user = auth.currentUser ?: return
        db.collection("users")
            .document(user.uid)
            .collection("flowers")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val flowers = result.mapNotNull { doc ->
                    val flower = doc.toObject(FlowerResponse::class.java)
                    flower?.copy(id = doc.id) // 💡 Guardamos el ID del documento
                }
                allFlowers = flowers
                _filteredFlowers.value = flowers
            }
    }

    /** 🔹 Filtro por nombre de flor */
    fun filterFlowers(query: String) {
        _filteredFlowers.value = if (query.isBlank()) {
            allFlowers
        } else {
            allFlowers.filter {
                it.class_supervised?.contains(query, ignoreCase = true) == true
            }
        }
    }
}