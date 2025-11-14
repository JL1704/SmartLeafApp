package com.deltasquad.smartleafapp.presentation.flowers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deltasquad.smartleafapp.data.model.FlowerData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlowersViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Lista de flores
    private val _flowers = MutableStateFlow<List<FlowerData>>(emptyList())
    val flowers: StateFlow<List<FlowerData>> = _flowers

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Error simple
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Flower seleccionado para detalles
    private val _selectedFlower = MutableStateFlow<FlowerData?>(null)
    val selectedFlower: StateFlow<FlowerData?> = _selectedFlower

    private var listener: ListenerRegistration? = null

    init {
        subscribeFlowers()
    }

    private fun subscribeFlowers() {
        _isLoading.value = true
        listener = db.collection("flowers")
            .addSnapshotListener { snapshot, exception ->
                viewModelScope.launch {
                    if (exception != null) {
                        _error.value = exception.message
                        _isLoading.value = false
                        return@launch
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            val obj = doc.toObject(FlowerData::class.java)
                            // Asegurarnos de que el id esté presente (firestore document id)
                            obj?.copy(id = doc.id)
                        }
                        _flowers.value = list
                    } else {
                        _flowers.value = emptyList()
                    }
                    _isLoading.value = false
                    _error.value = null
                }
            }
    }

    fun getFlowerById(id: String) {
        _isLoading.value = true
        _error.value = null
        db.collection("flowers").document(id).get()
            .addOnSuccessListener { doc ->
                val obj = doc.toObject(FlowerData::class.java)
                _selectedFlower.value = obj?.copy(id = doc.id)
                _isLoading.value = false
            }
            .addOnFailureListener { ex ->
                _error.value = ex.message
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
