package com.deltasquad.smartleafapp.presentation.details

import androidx.lifecycle.ViewModel
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _flower = MutableStateFlow<FlowerResponse?>(null)
    val flower: StateFlow<FlowerResponse?> = _flower

    fun fetchFlowerById(flowerId: String) {
        val user = auth.currentUser ?: return
        db.collection("users")
            .document(user.uid)
            .collection("flowers")
            .document(flowerId)
            .get()
            .addOnSuccessListener { result ->
                val data = result.toObject(FlowerResponse::class.java)
                _flower.value = data
            }
            .addOnFailureListener {
                _flower.value = null
            }
    }

    fun deleteFlowerById(
        flowerId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        db.collection("users")
            .document(user.uid)
            .collection("flowers")
            .document(flowerId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
