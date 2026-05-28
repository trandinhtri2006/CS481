package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs481app.Auth.UserProfile
import com.example.cs481app.Auth.loadUserProfile
import com.example.cs481app.Auth.saveUserProfile
import com.example.cs481app.Auth.userEmail
import kotlinx.coroutines.launch


// ─────────────────────────────────────────────────────────────
// VIEW MODEL
// Owns all form state + load/save logic for the combined screen
// ─────────────────────────────────────────────────────────────
class SettingPageViewModel : ViewModel() {

    // ---- editable identity fields ----
    var displayName by mutableStateOf("")
        private set
    var email by mutableStateOf(userEmail())
        private set

    // ---- license & insurance fields ----
    var licenseNumber by mutableStateOf("")
        private set
    var insuranceProvider by mutableStateOf("")
        private set
    var insurancePolicyNumber by mutableStateOf("")
        private set
    var insuranceExpiry by mutableStateOf("")
        private set

    // ---- UI state ----
    var isLoading by mutableStateOf(false)
        private set
    var isSaved by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    // ---- change handlers ----
    fun onDisplayNameChange(v: String)     { displayName = v; isSaved = false }
    fun onEmailChange(v: String)           { email = v; isSaved = false }
    fun onLicenseChange(v: String)         { licenseNumber = v; isSaved = false }
    fun onProviderChange(v: String)        { insuranceProvider = v; isSaved = false }
    fun onPolicyNumberChange(v: String)    { insurancePolicyNumber = v; isSaved = false }
    fun onExpiryChange(v: String)          { insuranceExpiry = v; isSaved = false }

    // LOAD — called once when the screen opens
    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val profile = loadUserProfile()
                if (profile != null) {
                    email                 = profile.email.ifBlank { userEmail() }
                    licenseNumber         = profile.licenseNumber
                    insuranceProvider     = profile.insuranceProvider
                    insurancePolicyNumber = profile.insurancePolicyNumber
                    insuranceExpiry       = profile.insuranceExpiry
                } else {
                    displayName = userEmail()
                    email       = userEmail()
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // SAVE
    fun saveProfile() {
        errorMessage = ""
        isSaved = false
        val profile = UserProfile(
            email                 = email.trim(),
            licenseNumber         = licenseNumber.trim(),
            insuranceProvider     = insuranceProvider.trim(),
            insurancePolicyNumber = insurancePolicyNumber.trim(),
            insuranceExpiry       = insuranceExpiry.trim()
        )
        viewModelScope.launch {
            isLoading = true
            try {
                saveUserProfile(profile)
                isSaved = true
            } catch (e: Exception) {
                errorMessage = "Failed to save: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────
// COMBINED SETTINGS + PROFILE SCREEN
// Profile card (with editable name/email) + license & insurance
// form + logout — all in one scrollable page.
// ─────────────────────────────────────────────────────────────
@Composable
fun SettingPage(
    navController: NavController,
    viewModel: SettingPageViewModel = viewModel()
) {
    // Load existing data once on first composition
    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Scaffold(
        bottomBar = {
            Bottombar(
                homePage    = false,
                reportPage  = false,
                settingPage = true,
                historyPage = false,
                navController = navController
            )
        }
    ) { paddingValues ->

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── PROFILE CARD ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    // Editable email
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            // ── SECTION HEADER ────────────────────────────────
            Text(
                text = "License & Insurance",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            // ── FORM CARD ─────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    OutlinedTextField(
                        value = viewModel.licenseNumber,
                        onValueChange = viewModel::onLicenseChange,
                        label = { Text("Driver's License Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.insuranceProvider,
                        onValueChange = viewModel::onProviderChange,
                        label = { Text("Insurance Provider") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.insurancePolicyNumber,
                        onValueChange = viewModel::onPolicyNumberChange,
                        label = { Text("Policy Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.insuranceExpiry,
                        onValueChange = viewModel::onExpiryChange,
                        label = { Text("Insurance Expiry (MM/YYYY)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Error message
                    if (viewModel.errorMessage.isNotBlank()) {
                        Text(
                            text = viewModel.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Success confirmation
                    if (viewModel.isSaved) {
                        Text(
                            text = "Profile saved!",
                            color = Color(0xFF388E3C),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // SAVE BUTTON
                    Button(
                        onClick = viewModel::saveProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Save Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // ── LOGOUT BUTTON ─────────────────────────────────
            Button(
                onClick = { navController.navigate(Routes.INITIAL_PAGE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


// ─────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun SettingPage_Preview() {
    SettingPage(rememberNavController())
}