package com.deltasquad.smartleafapp.presentation.flowers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.presentation.components.TagChip


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsFlowersScreen(
    flowerId: String,
    navController: NavController,
    viewModel: FlowersViewModel = viewModel()
) {
    val flower by viewModel.selectedFlower.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(flowerId) { viewModel.getFlowerById(flowerId) }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    if (flower == null) return

    val f = flower!!

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        // ------------------------------------------------------------
        // 🌸 HERO IMAGE
        // ------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            AsyncImage(
                model = f.imageUrl,
                contentDescription = f.name,
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

            // Back button
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

            // Nombre
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    f.name,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                if (f.scientificName.isNotEmpty()) {
                    Text(
                        f.scientificName,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------
        // 🖼 EXTRA IMAGES (Carrusel)
        // ------------------------------------------------------------
        if (f.extraImages.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(f.extraImages.size) { i ->
                    AsyncImage(
                        model = f.extraImages[i],
                        contentDescription = "Extra image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // ------------------------------------------------------------
        // 🏷 TAGS
        // ------------------------------------------------------------
        if (f.tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                f.tags.forEach { TagChip(it) }
            }
            Spacer(Modifier.height(20.dp))
        }

        // ------------------------------------------------------------
        // 📝 DESCRIPCIÓN
        // ------------------------------------------------------------
        PrettyCard(title = "Descripción") {
            Text(
                f.description.ifEmpty { "Sin descripción disponible." },
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
                )
        }

        Spacer(Modifier.height(16.dp))

        // ------------------------------------------------------------
        // 🌱 INFORMACIÓN BOTÁNICA
        // ------------------------------------------------------------
        PrettyCard(title = "Información Botánica") {

            InfoItem("Familia", f.family)
            InfoItem("Origen", f.origin)
            InfoItem("Floración", f.bloomSeason)
            InfoItem("Toxicidad", f.toxicity)
        }

        Spacer(Modifier.height(16.dp))

        // ------------------------------------------------------------
        // ☀️ CUIDADOS (iconos)
        // ------------------------------------------------------------
        PrettyCard(title = "Cuidados") {

            IconInfo(
                icon = R.drawable.ic_water,
                label = "Riego",
                value = f.watering
            )

            IconInfo(
                icon = R.drawable.ic_sun,
                label = "Luz",
                value = f.lightRequirements
            )

            IconInfo(
                icon = R.drawable.ic_leaf,
                label = "Suelo",
                value = f.soilType
            )

            Spacer(Modifier.height(12.dp))

            if (f.careTips.isNotEmpty()) {
                Text(
                    f.careTips,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ------------------------------------------------------------
        // 🔧 MANTENIMIENTO (chips)
        // ------------------------------------------------------------
        if (f.maintenance.isNotEmpty()) {
            PrettyCard(title = "Mantenimiento") {

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    f.maintenance.forEach {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun PrettyCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    if (value.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(
                value,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun IconInfo(icon: Int, label: String, value: String) {
    if (value.isEmpty()) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(value, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}