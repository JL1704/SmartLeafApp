package com.deltasquad.smartleafapp.presentation.stats

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import android.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import com.deltasquad.smartleafapp.presentation.components.StatCard
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun StatsScreen(
    navController: NavHostController,
    userId: String,
    viewModel: StatsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val clusterStats by viewModel.clusterCount
    val supervisedStats by viewModel.supervisedCount

    val mostCommonSupervised by viewModel.mostCommonSupervised
    val mostCommonCluster by viewModel.mostCommonCluster
    val averageConfidence by viewModel.averageConfidence
    val totalPredictions by viewModel.totalPredictions

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadFlowerStats(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text(
                text = "Estadísticas de tus clasificaciones",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // ---- DASHBOARD CARDS ----
        item {
            StatCard(
                title = "Flor más común (supervisado)",
                value = mostCommonSupervised
            )
        }

        item {
            StatCard(
                title = "Flor más común (cluster)",
                value = mostCommonCluster
            )
        }

        item {
            StatCard(
                title = "Confianza promedio",
                value = "%.2f".format(averageConfidence)
            )
        }

        item {
            StatCard(
                title = "Total de clasificaciones",
                value = totalPredictions.toString()
            )
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // ---- CLUSTER GRAPH ----
        item {
            Text(
                text = "Clasificación por Clúster",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            if (clusterStats.isNotEmpty()) {
                BarChartView(
                    title = "Clusters",
                    dataMap = clusterStats,
                    maxBars = 6
                )
            } else {
                Text("No hay datos.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // ---- SUPERVISED GRAPH ----
        item {
            Text(
                text = "Clasificación Supervisada",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            if (supervisedStats.isNotEmpty()) {
                BarChartView(
                    title = "Supervised",
                    dataMap = supervisedStats,
                    maxBars = 6
                )
            } else {
                Text("No hay predicciones supervisadas.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun BarChartView(
    title: String,
    dataMap: Map<String, Int>,
    maxBars: Int
) {
    if (dataMap.isEmpty()) {
        Text("No data available")
        return
    }

    val sortedData = dataMap.toSortedMap().toList().takeLast(maxBars)
    val entries = sortedData.mapIndexed { index, entry ->
        BarEntry(index.toFloat(), entry.second.toFloat())
    }
    val labels = sortedData.map { it.first }

    // Calcular altura dinámica según número de barras
    val barHeight = 50.dp // altura aproximada por barra
    val chartHeight = (sortedData.size * barHeight.value).coerceIn(200f, 600f)

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    chartHeight.toInt()
                )

                setBackgroundColor(Color.TRANSPARENT)

                val dataSet = BarDataSet(entries, title).apply {
                    setColor(Color.rgb(30, 144, 255))
                    valueTextColor = Color.rgb(17, 185, 123)
                    valueTextSize = 16f
                }
                this.data = BarData(dataSet)

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    setDrawGridLines(false)
                    setLabelCount(labels.size)
                    textColor = Color.rgb(17, 185, 123)
                    textSize = 14f
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                }

                axisLeft.apply {
                    textColor = Color.rgb(17, 185, 123)
                    gridColor = Color.GRAY
                    axisLineColor = Color.GRAY
                }

                axisRight.isEnabled = false

                description.isEnabled = false
                legend.isEnabled = false
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight.dp)
    )
}
