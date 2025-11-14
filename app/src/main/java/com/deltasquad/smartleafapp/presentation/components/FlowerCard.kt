package com.deltasquad.smartleafapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.data.model.FlowerData

@Composable
fun FlowerCard(
    flower: FlowerData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ────────────────────────────────
            // PRIMERA FILA → IMAGEN + INFO
            // ────────────────────────────────
            Row(verticalAlignment = Alignment.Top) {

                AsyncImage(
                    model = flower.imageUrl,
                    contentDescription = "Imagen de flor",
                    modifier = Modifier
                        .size(95.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    error = painterResource(R.drawable.placeholder),
                    placeholder = painterResource(R.drawable.placeholder)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                ) {

                    Text(
                        text = flower.name.ifEmpty { "Nombre desconocido" },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        color = Color(0xFF2C2C2C)
                    )

                    if (flower.scientificName.isNotEmpty()) {
                        Text(
                            text = flower.scientificName,
                            fontSize = 13.sp,
                            color = Color(0xFF7A7A7A),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (flower.description.isNotEmpty()) {
                        Text(
                            text = flower.description.take(95) +
                                    if (flower.description.length > 95) "..." else "",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 14.sp,
                            color = Color(0xFF6A6A6A),
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ────────────────────────────────
            // QUICK INFO – DEBAJO Y A TODO ANCHO
            // ────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    flower.lightRequirements,
                    flower.watering,
                    flower.soilType
                )
                    .filter { it.isNotEmpty() }
                    .take(3)
                    .forEachIndexed { index, value ->
                        val icon = when (index) {
                            0 -> R.drawable.ic_sun
                            1 -> R.drawable.ic_water
                            else -> R.drawable.ic_leaf
                        }
                        QuickInfoChip(icon = icon, text = value)
                    }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ────────────────────────────────
            // TAGS – TAMBIÉN ABAJO Y A TODO ANCHO
            // ────────────────────────────────
            if (flower.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    flower.tags.take(3).forEach { tag ->
                        TagChip(tag)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickInfoChip(icon: Int, text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFE9F7EC)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF43A047),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF2E7D32),
                maxLines = 1
            )
        }
    }
}

@Composable
fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE5E5E5))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF424242),
            fontSize = 12.sp
        )
    }
}
