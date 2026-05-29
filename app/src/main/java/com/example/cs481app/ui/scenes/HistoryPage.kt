package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.data.FirestoreHandler
import com.example.cs481app.data.Incident
import kotlinx.coroutines.launch


class HistoryViewModel : ViewModel() {

    var incidents by mutableStateOf<List<Incident>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    // Called once when the screen opens
    fun loadIncidents() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                incidents = FirestoreHandler.getIncidents()
            } catch (e: Exception) {
                errorMessage = "Failed to load incidents: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}


// HISTORY SCREEN
// Displays all saved incident records for the current user.
// Tapping a card navigates to the editable detail page.
@Composable
fun HistoryPage(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {
    // Load incidents from Firestore when screen first appears
    LaunchedEffect(Unit) { viewModel.loadIncidents() }

    Scaffold(
        bottomBar = {
            Bottombar(
                homePage = false,
                reportPage = false,
                settingPage = false,
                historyPage = true,
                navController = navController
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // PAGE HEADER
            Text(
                text = "Incident History",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap a record to view or edit",
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // LOADING STATE
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            // ERROR STATE
            if (viewModel.errorMessage.isNotBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Scaffold
            }

            // EMPTY STATE
            if (viewModel.incidents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No incidents recorded yet.",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                return@Scaffold
            }

            // INCIDENT LIST
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                items(viewModel.incidents) { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = {
                            navController.navigate("${Routes.INCIDENT_DETAIL_PAGE}/${incident.incidentId}")
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}


// INCIDENT CARD COMPONENT
// Shows a summary of one incident record
@Composable
fun IncidentCard(
    incident: Incident,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Incident",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // TEXT CONTENT
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = incident.accidentType.ifBlank { "Unknown Type" }
                        .replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = incident.date.ifBlank { "No date" },
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open Incident",
                tint = Color.Gray
            )
        }
    }
}

