package com.deltasquad.smartleafapp.presentation.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.deltasquad.smartleafapp.data.model.PlateReport
import com.deltasquad.smartleafapp.data.model.ScanRecord
import com.google.firebase.firestore.FirebaseFirestore

class StatsViewModel : ViewModel() {

    private val _scansPerDay = mutableStateOf<Map<String, Int>>(emptyMap())
    val scansPerDay: State<Map<String, Int>> = _scansPerDay

    private val _monthlyReports = mutableStateOf<Map<String, Int>>(emptyMap())
    val monthlyReports: State<Map<String, Int>> = _monthlyReports

    fun loadStats(userId: String) {
        getScansPerDay(userId)
        getMonthlyReports(userId)
    }

    private fun getScansPerDay(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("scans")
            .get()
            .addOnSuccessListener { snapshot ->
                val map = snapshot.documents.mapNotNull { it.toObject(ScanRecord::class.java) }
                    .groupingBy { it.date.take(10) } // yyyy-MM-dd
                    .eachCount()
                _scansPerDay.value = map
            }
    }

    private fun getMonthlyReports(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("reports")
            .get()
            .addOnSuccessListener { snapshot ->
                val map = snapshot.documents.mapNotNull { it.toObject(PlateReport::class.java) }
                    .groupingBy {
                        it.dateReported.take(7) // yyyy-MM
                    }
                    .eachCount()
                _monthlyReports.value = map
            }
    }
}
