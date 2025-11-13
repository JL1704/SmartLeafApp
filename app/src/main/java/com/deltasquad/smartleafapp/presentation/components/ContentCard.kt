package com.deltasquad.smartleafapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.presentation.theme.PlateScanAppTheme

@Composable
fun ContentCard(
    imageUrl: String?,
    classSupervised: String?,
    confidence: Float?,
    date: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PlateScanAppTheme.dimens.paddingNormal)
            .clickable { onClick() },
        shape = RoundedCornerShape(PlateScanAppTheme.dimens.roundedShapeMedium),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(PlateScanAppTheme.dimens.paddingNormal),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen de flor",
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = PlateScanAppTheme.dimens.paddingNormal),
                error = painterResource(R.drawable.placeholder),
                placeholder = painterResource(R.drawable.placeholder)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = classSupervised ?: "Desconocida",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                confidence?.let {
                    Text(
                        text = "Confianza: ${(it * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                date?.let {
                    Text(
                        text = "Fecha: $it",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
