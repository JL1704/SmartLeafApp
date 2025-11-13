package com.deltasquad.smartleafapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.deltasquad.smartleafapp.presentation.components.*
import com.deltasquad.smartleafapp.presentation.navigation.Screen
import com.deltasquad.smartleafapp.presentation.theme.PlateScanAppTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val latestFlowers by viewModel.latestFlowers.collectAsState()
    val filteredFlowers by viewModel.filteredFlowers.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchLatestFlowers()
        viewModel.fetchAllFlowers()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
    ) {
        // 🔍 Barra de búsqueda
        item {
            SearchBar(
                query = query,
                onQueryChanged = {
                    query = it
                    viewModel.filterFlowers(query)
                }
            )
        }

        // 🔘 Botones de acción (si los tienes definidos)
        item { ButtonGroup(navController = navController) }

        if (query.isBlank()) {
            item { SectionLabel(text = "Últimas Clasificaciones") }
            items(latestFlowers) { flower ->
                ContentCard(
                    imageUrl = flower.imageUrl,
                    classSupervised = flower.class_supervised,
                    confidence = flower.confidence,
                    date = flower.timestamp,
                    onClick = {
                        navController.navigate(
                            Screen.Details.createRoute(flower.class_supervised ?: "unknown")
                        )
                    }
                )
            }
        } else {
            item { SectionLabel(text = "Resultados de Búsqueda") }
            items(filteredFlowers) { flower ->
                ContentCard(
                    imageUrl = flower.imageUrl,
                    classSupervised = flower.class_supervised,
                    confidence = flower.confidence,
                    date = flower.timestamp,
                    onClick = {
                        navController.navigate(
                            Screen.Details.createRoute(flower.class_supervised ?: "unknown")
                        )
                    }
                )
            }
        }
    }
}
