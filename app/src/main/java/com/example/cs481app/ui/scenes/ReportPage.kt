package com.example.cs481app.ui.scenes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cs481app.ai.AIAssistantViewModel
import com.example.cs481app.data.FirestoreHandler.saveIncident
import com.example.cs481app.data.HashUtility
import com.example.cs481app.data.Incident
import com.example.cs481app.data.StorageHandler
import com.example.cs481app.data.Witness
import com.example.cs481app.geolocation.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportPage(navController: NavController, aiViewModel: AIAssistantViewModel? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var incidentId by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var accidentType by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var insuranceInfo by remember { mutableStateOf("") }
    var otherPartyName by remember { mutableStateOf("") }
    var otherPartyPhone by remember { mutableStateOf("") }
    var otherPartyLicense by remember { mutableStateOf("") }
    var otherPartyInsurance by remember { mutableStateOf("") }
    var witnesses by remember { mutableStateOf(listOf(Witness())) }
    var selectedPhotos by remember { mutableStateOf(listOf<String>()) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(0) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        scope.launch(Dispatchers.IO) {
            val cached = uris.mapNotNull { uri ->
                try {
                    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}_${uri.hashCode()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }
                    file.absolutePath
                } catch (e: Exception) { null }
            }
            withContext(Dispatchers.Main) {
                selectedPhotos = (selectedPhotos + cached).take(10)
            }
        }
    }

    var cameraPhotoFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraPhotoFile?.absolutePath?.let { path ->
                selectedPhotos = (selectedPhotos + path).take(10)
            }
            cameraPhotoFile = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            cameraPhotoFile = file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        cameraPhotoFile = file
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        cameraLauncher.launch(uri)
    }

    fun goNext() {
        currentStep = when (currentStep) {
            0 -> 1
            1 -> if (accidentType == "multiparty") 2 else 3
            2 -> 3
            3 -> 4
            4 -> 5
            else -> currentStep
        }
        aiViewModel?.setStep(when (currentStep) {
            1 -> "driver_info"
            2 -> "other_party_info"
            3 -> "photos"
            4 -> "witnesses"
            5 -> "review"
            else -> "accident_type"
        })
    }

    fun goPrev() {
        currentStep = when (currentStep) {
            1 -> 0
            2 -> 1
            3 -> if (accidentType == "multiparty") 2 else 1
            4 -> 3
            5 -> 4
            else -> currentStep
        }
    }

    fun resetForm() {
        incidentId = UUID.randomUUID().toString()
        accidentType = ""
        driverName = ""
        licenseNumber = ""
        insuranceInfo = ""
        otherPartyName = ""
        otherPartyPhone = ""
        otherPartyLicense = ""
        otherPartyInsurance = ""
        witnesses = listOf(Witness())
        selectedPhotos = emptyList()
        currentStep = 0
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
        ) {
            when (currentStep) {

                // ── STEP 0: TYPE SELECTION ─────────────────────────────────
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }

                        Text(
                            text = "What kind of incident occurred?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        IncidentTypeCard(
                            title = "Solo Accident",
                            description = "Single vehicle, no other party involved"
                        ) {
                            accidentType = "solo"
                            aiViewModel?.setContext("report", "solo")
                            goNext()
                        }

                        IncidentTypeCard(
                            title = "Hit & Run",
                            description = "Other driver fled the scene"
                        ) {
                            accidentType = "hitandrun"
                            aiViewModel?.setContext("report", "hitandrun")
                            goNext()
                        }

                        IncidentTypeCard(
                            title = "Multi-Party",
                            description = "Two or more vehicles involved"
                        ) {
                            accidentType = "multiparty"
                            aiViewModel?.setContext("report", "multiparty")
                            goNext()
                        }
                    }
                }

                // ── STEP 1: YOUR INFORMATION ───────────────────────────────
                1 -> {
                    StepScaffold(title = "Your Information", onBack = ::goPrev, onNext = ::goNext) {
                        OutlinedTextField(
                            value = driverName,
                            onValueChange = { driverName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) aiViewModel?.setStep("driver_info") }
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
                    }
                }

                // ── STEP 2: OTHER PARTY (multiparty only) ──────────────────
                2 -> {
                    StepScaffold(title = "Other Party Information", onBack = ::goPrev, onNext = ::goNext) {
                        OutlinedTextField(
                            value = otherPartyName,
                            onValueChange = { otherPartyName = it },
                            label = { Text("Other Driver's Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) aiViewModel?.setStep("other_party_info") }
                        )
                        OutlinedTextField(
                            value = otherPartyPhone,
                            onValueChange = { otherPartyPhone = it },
                            label = { Text("Other Driver's Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = otherPartyLicense,
                            onValueChange = { otherPartyLicense = it },
                            label = { Text("Other Driver's License Plate") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = otherPartyInsurance,
                            onValueChange = { otherPartyInsurance = it },
                            label = { Text("Other Driver's Insurance Info") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ── STEP 3: PHOTOS ─────────────────────────────────────────
                3 -> {
                    StepScaffold(title = "Photos", onBack = ::goPrev, onNext = ::goNext) {
                        Text(
                            text = "Add photos of the damage, scene, or relevant documents.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (selectedPhotos.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                selectedPhotos.forEachIndexed { index, uri ->
                                    PhotoThumbnail(uri = uri) {
                                        selectedPhotos = selectedPhotos.toMutableList().also { it.removeAt(index) }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    aiViewModel?.setStep("photos")
                                    val granted = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) launchCamera()
                                    else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedPhotos.size < 10
                            ) { Text("Take Photo") }

                            OutlinedButton(
                                onClick = {
                                    aiViewModel?.setStep("photos")
                                    photoPickerLauncher.launch("image/*")
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedPhotos.size < 10
                            ) {
                                Text(
                                    if (selectedPhotos.size < 10) "Gallery (${selectedPhotos.size}/10)"
                                    else "Limit Reached"
                                )
                            }
                        }
                    }
                }

                // ── STEP 4: WITNESSES ──────────────────────────────────────
                4 -> {
                    StepScaffold(title = "Witnesses", onBack = ::goPrev, onNext = ::goNext) {
                        Text(
                            text = "Add any witnesses present at the scene.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )

                        witnesses.forEachIndexed { index, witness ->
                            OutlinedTextField(
                                value = witness.name,
                                onValueChange = { v ->
                                    witnesses = witnesses.toMutableList().also { it[index] = it[index].copy(name = v) }
                                },
                                label = { Text("Witness Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { if (it.isFocused && index == 0) aiViewModel?.setStep("witnesses") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = witness.phone,
                                onValueChange = { v ->
                                    witnesses = witnesses.toMutableList().also { it[index] = it[index].copy(phone = v) }
                                },
                                label = { Text("Witness Phone") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (witnesses.size > 1) {
                                TextButton(onClick = {
                                    witnesses = witnesses.toMutableList().also { it.removeAt(index) }
                                }) { Text("Remove") }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = { witnesses = witnesses + Witness() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Add Witness") }
                    }
                }

                // ── STEP 5: REVIEW & SUBMIT ────────────────────────────────
                5 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Review & Submit", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))

                        ReviewRow("Type", accidentType.replaceFirstChar { it.uppercase() })
                        ReviewRow("Driver", driverName.ifBlank { "—" })
                        ReviewRow("License", licenseNumber.ifBlank { "—" })
                        ReviewRow("Insurance", insuranceInfo.ifBlank { "—" })

                        if (accidentType == "multiparty") {
                            HorizontalDivider()
                            ReviewRow("Other Party", otherPartyName.ifBlank { "—" })
                            ReviewRow("Their Phone", otherPartyPhone.ifBlank { "—" })
                            ReviewRow("Their Plate", otherPartyLicense.ifBlank { "—" })
                            ReviewRow("Their Insurance", otherPartyInsurance.ifBlank { "—" })
                        }

                        HorizontalDivider()
                        ReviewRow("Photos", "${selectedPhotos.size} added")
                        ReviewRow("Witnesses", "${witnesses.count { it.name.isNotBlank() }} added")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = ::goPrev,
                                modifier = Modifier.weight(1f)
                            ) { Text("← Back") }

                            Button(
                                onClick = {
                                    aiViewModel?.setStep("review")
                                    scope.launch {
                                        dialogMessage = try {
                                            val online = isOnline(context)

                                            var location = ""
                                            LocationHelper.getCurrentLocation(context) { lat, lon ->
                                                location = "$lat,$lon"
                                            }

                                            val photoUris = selectedPhotos.map { Uri.fromFile(File(it)) }
                                            val uploadedUrls = if (online && photoUris.isNotEmpty())
                                                StorageHandler.uploadPhotos(photoUris, incidentId)
                                            else
                                                emptyList()

                                            val incident = Incident(
                                                incidentId = incidentId,
                                                accidentType = accidentType,
                                                date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                                                location = location,
                                                driverName = driverName,
                                                licenseNumber = licenseNumber,
                                                insuranceInfo = insuranceInfo,
                                                otherPartyName = otherPartyName,
                                                otherPartyPhone = otherPartyPhone,
                                                otherPartyLicense = otherPartyLicense,
                                                otherPartyInsurance = otherPartyInsurance,
                                                witnessInfo = witnesses,
                                                timestamp = System.currentTimeMillis(),
                                                photoUrls = uploadedUrls
                                            )
                                            val hashedIncident = incident.copy(hash = HashUtility.generateHash(incident))
                                            saveIncident(hashedIncident)
                                            if (online) "Report saved successfully."
                                            else "Saved locally. Will sync automatically when you reconnect.\nNote: photos must be added when back online."
                                        } catch (e: Exception) {
                                            "Error: ${e.message}"
                                        }
                                        showDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) { Text("Submit", color = Color.White) }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Save Status") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (!dialogMessage.startsWith("Error")) resetForm()
                }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun StepScaffold(
    title: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        content()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("← Back") }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("Next →", color = Color.White) }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun IncidentTypeCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
private fun PhotoThumbnail(uri: String, onRemove: () -> Unit) {
    Box {
        AsyncImage(
            model = uri,
            contentDescription = "Selected photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}