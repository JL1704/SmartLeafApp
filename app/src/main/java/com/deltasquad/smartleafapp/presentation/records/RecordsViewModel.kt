package com.deltasquad.smartleafapp.presentation.records

import androidx.lifecycle.ViewModel
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecordsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _allFlowers = MutableStateFlow<List<FlowerResponse>>(emptyList())
    val allFlowers: StateFlow<List<FlowerResponse>> = _allFlowers

    private val _filteredFlowers = MutableStateFlow<List<FlowerResponse>>(emptyList())
    val filteredFlowers: StateFlow<List<FlowerResponse>> = _filteredFlowers

    // Lista completa para búsqueda
    private var allFlowerList: List<FlowerResponse> = emptyList()

    /** 🔹 Carga todos los registros de flores del usuario actual */
    fun fetchAllFlowers() {
        val user = auth.currentUser ?: return
        db.collection("users")
            .document(user.uid)
            .collection("flowers")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val flowers = result.mapNotNull { doc ->
                    doc.toObject(FlowerResponse::class.java)
                }
                allFlowerList = flowers
                _allFlowers.value = flowers
                _filteredFlowers.value = flowers
            }
    }

    /** 🔹 Filtra flores por nombre */
    fun filterFlowers(query: String) {
        _filteredFlowers.value = if (query.isBlank()) {
            allFlowerList
        } else {
            allFlowerList.filter { flower ->
                flower.class_supervised?.contains(query, ignoreCase = true) == true
            }
        }
    }
}
