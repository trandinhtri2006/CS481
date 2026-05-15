package com.example.cs481app.ui.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun ReportPage(
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            Bottombar(
                homePage = false,
                reportPage = true,
                settingPage = false,
                historyPage = false,
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

        }
    }
}