package com.deltasquad.smartleafapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.deltasquad.smartleafapp.presentation.navigation.Screen
import com.deltasquad.smartleafapp.R

@Composable
fun ButtonGroup(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationButton(
            text = "Records",
            iconRes = R.drawable.ic_history,
            onClick = { navController.navigate(Screen.Records.route) }
        )
        NavigationButton(
            text = "Flowers",
            iconRes = R.drawable.ic_flower,
            onClick = { navController.navigate(Screen.Flowers.route) }
        )
        NavigationButton(
            text = "Stats",
            iconRes = R.drawable.ic_stats,
            onClick = { navController.navigate(Screen.Stats.route) }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ButtonGroupPreview() {
    //ButtonGroup()
}
