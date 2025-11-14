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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Imagen
            AsyncImage(
                model = flower.imageUrl,
                contentDescription = "Imagen de flor",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                error = painterResource(R.drawable.placeholder),
                placeholder = painterResource(R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Nombre
                Text(
                    text = flower.name.ifEmpty { "Sin nombre" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Descripción corta
                if (flower.description.isNotEmpty()) {
                    Text(
                        text = flower.description.take(80) + if (flower.description.length > 80) "..." else "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Tags
                if (flower.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        flower.tags.take(3).forEach { tag ->
                            TagChip(tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE0E0E0))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 12.sp
        )
    }
}
