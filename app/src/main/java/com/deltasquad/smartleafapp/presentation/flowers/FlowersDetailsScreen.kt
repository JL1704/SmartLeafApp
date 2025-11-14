package com.deltasquad.smartleafapp.presentation.flowers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deltasquad.smartleafapp.presentation.components.TagChip
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun DetailsFlowersScreen(
    flowerId: String,
    navController: NavController,
    viewModel: FlowersViewModel = viewModel()
) {
    val selected by viewModel.selectedFlower.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Pedimos el detalle la primera vez que entra la pantalla
    LaunchedEffect(key1 = flowerId) {
        viewModel.getFlowerById(flowerId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        ) {
            // Back icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.popBackStack() }
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            if (error != null) {
                Text(text = "Error: ${error ?: "Desconocido"}")
                return@Column
            }

            val flower = selected
            if (flower == null) {
                Text(text = "No se encontró la flor")
                return@Column
            }

            AsyncImage(
                model = flower.imageUrl,
                contentDescription = "Imagen principal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = flower.name.ifEmpty { "Sin nombre" },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (flower.scientificName.isNotEmpty()) {
                Text(
                    text = flower.scientificName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (flower.description.isNotEmpty()) {
                Text(text = flower.description, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (flower.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    flower.tags.forEach { tag ->
                        TagChip(text = tag)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de cuidado resumida
            if (flower.careTips.isNotEmpty()) {
                Text(text = "Cuidados", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = flower.careTips)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detalles adicionales (watering, light, soil)
            if (flower.watering.isNotEmpty() || flower.lightRequirements.isNotEmpty() || flower.soilType.isNotEmpty()) {
                Text(text = "Detalles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                if (flower.watering.isNotEmpty()) Text(text = "Riego: ${flower.watering}")
                if (flower.lightRequirements.isNotEmpty()) Text(text = "Luz: ${flower.lightRequirements}")
                if (flower.soilType.isNotEmpty()) Text(text = "Suelo: ${flower.soilType}")
            }
        }
    }
}
