package com.example.cs481app.ui.scenes

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cs481app.data.FirestoreHandler.saveIncident
import com.example.cs481app.data.Incident
import com.example.cs481app.data.Witness
import com.example.cs481app.geolocation.LocationHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportPage(navController: NavController) {
    val context = LocalContext.current

    var incidentId by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var accidentType by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var insuranceInfo by remember { mutableStateOf("") }
    var witnesses by remember { mutableStateOf(listOf(Witness())) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var selectedPhotos by remember { mutableStateOf(listOf<String>()) }

    val accidentTypes = listOf("solo", "hitandrun", "multiparty")
    val scope = rememberCoroutineScope()

    // Launcher for picking multiple photos from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // Append new picks, capping total at 10
        selectedPhotos = (selectedPhotos + uris.map { it.toString() }).take(10)
    }

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Incident Report",
                style = MaterialTheme.typography.headlineMedium
            )

            // Accident Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = accidentType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Accident Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    accidentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                accidentType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = driverName,
                onValueChange = { driverName = it },
                label = { Text("Driver Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("License Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = insuranceInfo,
                onValueChange = { insuranceInfo = it },
                label = { Text("Insurance Info") },
                modifier = Modifier.fillMaxWidth()
            )

            // Witnesses section — only for non-solo accidents
            if (accidentType != "solo") {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Witnesses",
                    style = MaterialTheme.typography.titleMedium
                )

                witnesses.forEachIndexed { index, witness ->
                    OutlinedTextField(
                        value = witness.name,
                        onValueChange = { newValue ->
                            witnesses = witnesses.toMutableList().also {
                                it[index] = it[index].copy(name = newValue)
                            }
                        },
                        label = { Text("Witness Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = witness.phone,
                        onValueChange = { newValue ->
                            witnesses = witnesses.toMutableList().also {
                                it[index] = it[index].copy(phone = newValue)
                            }
                        },
                        label = { Text("Witness Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (witnesses.size > 1) {
                        TextButton(
                            onClick = {
                                witnesses = witnesses.toMutableList().also { it.removeAt(index) }
                            }
                        ) {
                            Text("Remove Witness")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { witnesses = witnesses + Witness() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Witness")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Upload Section
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium
            )

            if (selectedPhotos.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selectedPhotos.forEachIndexed { index, uri ->
                        PhotoThumbnail(
                            uri = uri,
                            onRemove = {
                                selectedPhotos = selectedPhotos.toMutableList().also {
                                    it.removeAt(index)
                                }
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPhotos.size < 10
            ) {
                Text(
                    if (selectedPhotos.size < 10)
                        "Add Photos (${selectedPhotos.size}/10)"
                    else
                        "Photo Limit Reached (10/10)"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    var location = ""
                    LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                        location = "$latitude,$longitude"
                    }

                    val incident = Incident(
                        incidentId = incidentId,
                        accidentType = accidentType,
                        date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                        location = location,
                        driverName = driverName,
                        licenseNumber = licenseNumber,
                        insuranceInfo = insuranceInfo,
                        witnessInfo = witnesses,
                        timestamp = System.currentTimeMillis(),
                        photoUrls = selectedPhotos
                    )

                    scope.launch {
                        try {
                            saveIncident(incident)
                            dialogMessage = "SUBMIT SUCCESSFULLY"
                        } catch (e: Exception) {
                            "Error: ${e.message}"
                        }
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Report")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Save Status") },
                    text = { Text(dialogMessage) },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

// Extracted composable to keep ReportPage readable
@Composable
private fun PhotoThumbnail(uri: String, onRemove: () -> Unit) {
    androidx.compose.foundation.layout.Box {
        AsyncImage(
            model = uri,
            contentDescription = "Selected photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove photo",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Preview_ReportPage() {
    ReportPage(rememberNavController())
}