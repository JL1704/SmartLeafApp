package com.deltasquad.smartleafapp.presentation.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.google.firebase.firestore.FirebaseFirestore

class StatsViewModel : ViewModel() {

    private val _clusterCount = mutableStateOf<Map<String, Int>>(emptyMap())
    val clusterCount: State<Map<String, Int>> = _clusterCount

    private val _supervisedCount = mutableStateOf<Map<String, Int>>(emptyMap())
    val supervisedCount: State<Map<String, Int>> = _supervisedCount

    // ---- Nuevos estados Dashboard ----
    private val _mostCommonSupervised = mutableStateOf("N/A")
    val mostCommonSupervised: State<String> = _mostCommonSupervised

    private val _mostCommonCluster = mutableStateOf("N/A")
    val mostCommonCluster: State<String> = _mostCommonCluster

    private val _averageConfidence = mutableStateOf(0f)
    val averageConfidence: State<Float> = _averageConfidence

    private val _totalPredictions = mutableStateOf(0)
    val totalPredictions: State<Int> = _totalPredictions

    fun loadFlowerStats(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("flowers")
            .get()
            .addOnSuccessListener { snapshot ->
                val flowers = snapshot.documents.mapNotNull {
                    it.toObject(FlowerResponse::class.java)
                }

                _totalPredictions.value = flowers.size

                if (flowers.isNotEmpty()) {

                    // ===============================
                    //          SUPERVISED
                    // ===============================
                    val supervised = flowers.groupingBy { it.class_supervised ?: "Unknown" }
                        .eachCount()
                    _supervisedCount.value = supervised

                    _mostCommonSupervised.value =
                        supervised.maxByOrNull { it.value }?.key ?: "N/A"

                    // ===============================
                    //            CLUSTER
                    // ===============================
                    val clusters = flowers.groupingBy { it.class_cluster ?: "Unknown" }
                        .eachCount()
                    _clusterCount.value = clusters

                    _mostCommonCluster.value =
                        clusters.maxByOrNull { it.value }?.key ?: "N/A"

                    // ===============================
                    //      AVERAGE CONFIDENCE
                    // ===============================
                    _averageConfidence.value =
                        flowers.mapNotNull { it.confidence }.average().toFloat()
                }
            }
    }
}