package com.deltasquad.smartleafapp.presentation.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.deltasquad.smartleafapp.presentation.navigation.Screen
import com.deltasquad.smartleafapp.presentation.theme.primaryGreen

@Composable
fun DetailsScreen(
    flowerId: String,
    navController: NavController,
    viewModel: DetailsViewModel = viewModel()
) {
    // 🔹 Cargar datos del registro
    LaunchedEffect(flowerId) {
        viewModel.fetchFlowerById(flowerId)
    }

    val flower by viewModel.flower.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (flower == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // 🔸 Encabezado con botón Back y nombre de la flor
                TopAppBarSection(
                    title = flower?.class_supervised ?: "Details",
                    onBackClick = { navController.popBackStack() }
                )

                // 🔸 Contenido de los datos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        FlowerInfoCard(flower!!)
                        Spacer(modifier = Modifier.height(16.dp))
                        FlowerImageSection(flower!!)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 🔹 Botones inferiores
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(Screen.EditData.createRoute(flowerId))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                            ) {
                                Text("Edit", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.deleteFlowerById(
                                        flowerId = flowerId,
                                        onSuccess = { navController.popBackStack() },
                                        onFailure = { /* TODO: Snackbar o Toast */ }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBarSection(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(28.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title.ifEmpty { "Flower Details" },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun FlowerInfoCard(flower: FlowerResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🌸 Flower Information", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow("Supervised Class:", flower.class_supervised ?: "Unknown")
            InfoRow("Cluster Class:", flower.class_cluster ?: "N/A")
            InfoRow("Cluster ID:", flower.cluster_id?.toString() ?: "N/A")
            InfoRow("Confidence:", "${(flower.confidence ?: 0f) * 100} %")
            InfoRow("Timestamp:", flower.timestamp ?: "N/A")
            InfoRow("User ID:", flower.userId ?: "N/A")
            InfoRow("Notes:", flower.notes ?: "—")
        }
    }
}

@Composable
private fun FlowerImageSection(flower: FlowerResponse) {
    if (!flower.imageUrl.isNullOrEmpty()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📷 Image", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = flower.imageUrl,
                contentDescription = "Flower Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = Color.Gray)
    }
}
