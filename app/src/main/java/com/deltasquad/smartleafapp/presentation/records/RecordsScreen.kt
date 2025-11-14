package com.deltasquad.smartleafapp.presentation.records

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.deltasquad.smartleafapp.R
import com.deltasquad.smartleafapp.presentation.components.ContentCard
import com.deltasquad.smartleafapp.presentation.components.SearchBar
import com.deltasquad.smartleafapp.presentation.components.SectionLabel
import com.deltasquad.smartleafapp.presentation.navigation.Screen
import com.deltasquad.smartleafapp.presentation.theme.primaryPurple

@Composable
fun RecordsScreen(
    navController: NavHostController,
    viewModel: RecordsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val filteredFlowers by viewModel.filteredFlowers.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchAllFlowers()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Back",
                    tint = primaryPurple,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                SectionLabel(
                    text = "Clasificaciones de Flores",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 44.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        // 🔍 Barra de búsqueda
        SearchBar(
            query = query,
            onQueryChanged = {
                query = it
                viewModel.filterFlowers(query)
            }
        )

        // 📜 Lista de flores
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredFlowers) { flower ->
                ContentCard(
                    imageUrl = flower.imageUrl,
                    classSupervised = flower.class_supervised,
                    confidence = flower.confidence,
                    date = flower.timestamp,
                    onClick = {
                        // 💡 Navegamos con el ID del documento
                        flower.id?.let { id ->
                            navController.navigate(Screen.Details.createRoute(id))
                        }
                    }
                )
            }
        }
    }
}