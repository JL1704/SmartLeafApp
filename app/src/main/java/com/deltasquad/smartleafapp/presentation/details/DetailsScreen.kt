package com.deltasquad.smartleafapp.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.deltasquad.smartleafapp.presentation.theme.primaryPurple

@Composable
fun DetailsScreen(
    flowerId: String,
    navController: NavController,
    viewModel: DetailsViewModel = viewModel()
) {
    // Cargar datos del registro
    LaunchedEffect(flowerId) {
        viewModel.fetchFlowerById(flowerId)
    }

    val flower by viewModel.flower.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (flower == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    FlowerHeroSection(flower!!, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowerInfoCard(flower!!)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Botón eliminar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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

@Composable
private fun FlowerHeroSection(f: FlowerResponse, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        // Imagen principal
        AsyncImage(
            model = f.imageUrl,
            contentDescription = f.class_supervised,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay degradado
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                    )
                )
        )

        // Botón Back
        Icon(
            painter = painterResource(id = R.drawable.ic_back_24),
            tint = Color.White,
            contentDescription = "Back",
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
                .clickable { navController.popBackStack() }
        )

        // Nombre de la flor
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                f.class_supervised ?: "Unknown",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            f.class_cluster?.let {
                Text(
                    "Cluster: $it",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun FlowerInfoCard(flower: FlowerResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Flower Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

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