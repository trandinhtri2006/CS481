package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.data.FirestoreHandler
import com.example.cs481app.data.Witness
import kotlinx.coroutines.launch


// VIEW MODEL
// Loads a single incident by ID, tracks all editable fields, and handles save/delete.
class IncidentDetailViewModel(private val incidentId: String) : ViewModel() {

    // ---- editable fields matching Incident data class ----
    var accidentType by mutableStateOf("")      // 'solo', 'hitandrun', 'multiparty'
        private set
    var date by mutableStateOf("")              // YYYY-MM-DD
        private set
    var time by mutableStateOf("")              // HH:MM
        private set
    var location by mutableStateOf("")          // latitude and longitude
        private set
    var driverName by mutableStateOf("")
        private set
    var licenseNumber by mutableStateOf("")
        private set
    var insuranceInfo by mutableStateOf("")
        private set
    var otherPartyName by mutableStateOf("")
        private set
    var otherPartyPhone by mutableStateOf("")
        private set
    var otherPartyLicense by mutableStateOf("")
        private set
    var otherPartyInsurance by mutableStateOf("")
        private set

    // Witnesses are a mutable list so rows can be added/removed dynamically
    val witnesses = mutableStateListOf<Witness>()

    // ---- UI state ----
    var isLoading by mutableStateOf(false)
        private set
    var isSaved by mutableStateOf(false)
        private set
    var showDeleteDialog by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    // ---- change handlers ----
    fun onAccidentTypeChange(v: String) { accidentType = v;   isSaved = false }
    fun onDateChange(v: String)         { date = v;           isSaved = false }
    fun onTimeChange(v: String)         { time = v;           isSaved = false }
    fun onLocationChange(v: String)     { location = v;       isSaved = false }
    fun onDriverNameChange(v: String)   { driverName = v;     isSaved = false }
    fun onLicenseChange(v: String)      { licenseNumber = v;  isSaved = false }
    fun onInsuranceChange(v: String)      { insuranceInfo = v;       isSaved = false }
    fun onOtherPartyNameChange(v: String)     { otherPartyName = v;     isSaved = false }
    fun onOtherPartyPhoneChange(v: String)    { otherPartyPhone = v;    isSaved = false }
    fun onOtherPartyLicenseChange(v: String)  { otherPartyLicense = v;  isSaved = false }
    fun onOtherPartyInsuranceChange(v: String){ otherPartyInsurance = v; isSaved = false }

    // WITNESS LIST HELPERS
    fun addWitness()                              { witnesses.add(Witness()); isSaved = false }
    fun removeWitness(index: Int)                 { witnesses.removeAt(index); isSaved = false }
    fun updateWitnessName(index: Int, v: String)  { witnesses[index] = witnesses[index].copy(name = v);  isSaved = false }
    fun updateWitnessPhone(index: Int, v: String) { witnesses[index] = witnesses[index].copy(phone = v); isSaved = false }

    fun openDeleteDialog()  { showDeleteDialog = true }
    fun closeDeleteDialog() { showDeleteDialog = false }

    // LOAD
    fun loadIncident() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val incident  = FirestoreHandler.getIncident(incidentId)
                accidentType  = incident.accidentType
                date          = incident.date
                time          = incident.time
                location      = incident.location
                driverName    = incident.driverName
                licenseNumber = incident.licenseNumber
                insuranceInfo     = incident.insuranceInfo
                otherPartyName    = incident.otherPartyName
                otherPartyPhone   = incident.otherPartyPhone
                otherPartyLicense = incident.otherPartyLicense
                otherPartyInsurance = incident.otherPartyInsurance
                witnesses.clear()
                witnesses.addAll(incident.witnessInfo)
            } catch (e: Exception) {
                errorMessage = "Failed to load incident: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // SAVE
    fun saveIncident() {
        isSaved = false
        errorMessage = ""
        viewModelScope.launch {
            isLoading = true
            try {
                // Convert the witness list to plain maps so Firestore can serialize it
                val witnessData = witnesses.map { mapOf("name" to it.name, "phone" to it.phone) }

                val updates: Map<String, Any> = mapOf(
                    "accidentType"      to accidentType.trim(),
                    "date"              to date.trim(),
                    "time"              to time.trim(),
                    "location"          to location.trim(),
                    "driverName"        to driverName.trim(),
                    "licenseNumber"     to licenseNumber.trim(),
                    "insuranceInfo"     to insuranceInfo.trim(),
                    "otherPartyName"    to otherPartyName.trim(),
                    "otherPartyPhone"   to otherPartyPhone.trim(),
                    "otherPartyLicense" to otherPartyLicense.trim(),
                    "otherPartyInsurance" to otherPartyInsurance.trim(),
                    "witnessInfo"       to witnessData
                )
                FirestoreHandler.updateIncident(incidentId, updates)
                isSaved = true
            } catch (e: Exception) {
                errorMessage = "Failed to save: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // DELETE
    fun deleteIncident(onDeleted: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                FirestoreHandler.deleteIncident(incidentId)
                onDeleted()
            } catch (e: Exception) {
                errorMessage = "Failed to delete: ${e.message}"
                isLoading = false
            }
        }
    }

    // FACTORY — required to pass incidentId into the ViewModel constructor
    companion object {
        fun factory(incidentId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    IncidentDetailViewModel(incidentId) as T
            }
    }
}


// INCIDENT DETAIL / EDIT SCREEN
// Displays all fields of one incident and allows the user to update or delete it.
// Navigate here with: navController.navigate("${Routes.INCIDENT_DETAIL}/$incidentId")
@Composable
fun IncidentDetailPage(
    navController: NavController,
    incidentId: String,
    viewModel: IncidentDetailViewModel = viewModel(factory = IncidentDetailViewModel.factory(incidentId))
) {
    LaunchedEffect(incidentId) { viewModel.loadIncident() }

    // DELETE CONFIRMATION DIALOG
    if (viewModel.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeDeleteDialog,
            title = { Text("Delete Incident?") },
            text  = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteIncident { navController.popBackStack() } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeDeleteDialog) { Text("Cancel") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        BackButton(
            navController = navController,
            pageName = Routes.HISTORY_PAGE,
            modifier = Modifier.align(Alignment.TopStart)
        )

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        // SCROLLABLE FORM
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Edit Incident", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // ACCIDENT TYPE  ('solo', 'hitandrun', 'multiparty')
            OutlinedTextField(
                value = viewModel.accidentType,
                onValueChange = viewModel::onAccidentTypeChange,
                label = { Text("Accident Type (solo / hitandrun / multiparty)") },
                modifier = Modifier.fillMaxWidth()
            )

            // DATE  YYYY-MM-DD
            OutlinedTextField(
                value = viewModel.date,
                onValueChange = viewModel::onDateChange,
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // TIME  HH:MM
            OutlinedTextField(
                value = viewModel.time,
                onValueChange = viewModel::onTimeChange,
                label = { Text("Time (HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            // LOCATION  (latitude, longitude)
            OutlinedTextField(
                value = viewModel.location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Location (lat, lng)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Driver Information", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            // DRIVER NAME
            OutlinedTextField(
                value = viewModel.driverName,
                onValueChange = viewModel::onDriverNameChange,
                label = { Text("Driver Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // LICENSE NUMBER
            OutlinedTextField(
                value = viewModel.licenseNumber,
                onValueChange = viewModel::onLicenseChange,
                label = { Text("License Number") },
                modifier = Modifier.fillMaxWidth()
            )

            // INSURANCE INFO
            OutlinedTextField(
                value = viewModel.insuranceInfo,
                onValueChange = viewModel::onInsuranceChange,
                label = { Text("Insurance Info") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // OTHER PARTY SECTION — multiparty only
            if (viewModel.accidentType == "multiparty") {
                Text("Other Party Information", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = viewModel.otherPartyName,
                    onValueChange = viewModel::onOtherPartyNameChange,
                    label = { Text("Other Driver's Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.otherPartyPhone,
                    onValueChange = viewModel::onOtherPartyPhoneChange,
                    label = { Text("Other Driver's Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.otherPartyLicense,
                    onValueChange = viewModel::onOtherPartyLicenseChange,
                    label = { Text("Other Driver's License Plate") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.otherPartyInsurance,
                    onValueChange = viewModel::onOtherPartyInsuranceChange,
                    label = { Text("Other Driver's Insurance Info") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // WITNESSES SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Witnesses", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                // ADD WITNESS BUTTON
                IconButton(onClick = viewModel::addWitness) {
                    Icon(Icons.Default.Add, contentDescription = "Add Witness")
                }
            }

            if (viewModel.witnesses.isEmpty()) {
                Text(
                    text = "No witnesses — tap + to add one.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // One row per witness: Name field | Phone field | Delete icon
            viewModel.witnesses.forEachIndexed { index, witness ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = witness.name,
                        onValueChange = { viewModel.updateWitnessName(index, it) },
                        label = { Text("Name") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = witness.phone,
                        onValueChange = { viewModel.updateWitnessPhone(index, it) },
                        label = { Text("Phone") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.removeWitness(index) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Witness",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ERROR MESSAGE
            if (viewModel.errorMessage.isNotBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // SUCCESS CONFIRMATION
            if (viewModel.isSaved) {
                Text(
                    text = "Changes saved!",
                    color = Color(0xFF388E3C),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = viewModel::saveIncident,
                    modifier = Modifier.weight(1f)
                ) { Text("Save Changes") }

                OutlinedButton(
                    onClick = viewModel::openDeleteDialog,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) { Text("Delete") }
            }
        }
    }
}