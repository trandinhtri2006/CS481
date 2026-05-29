package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.Auth.UserProfile
import com.example.cs481app.Auth.loadUserProfile
import com.example.cs481app.Auth.saveUserProfile
import com.example.cs481app.Auth.userEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf(userEmail())
        private set
    var licenseNumber by mutableStateOf("")
        private set
    var insuranceProvider by mutableStateOf("")
        private set
    var insurancePolicyNumber by mutableStateOf("")
        private set
    var insuranceExpiry by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSaved by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    fun onFirstNameChange(v: String)       { firstName = v; isSaved = false }
    fun onLastNameChange(v: String)        { lastName = v; isSaved = false }
    fun onEmailChange(v: String)           { email = v; isSaved = false }
    fun onLicenseChange(v: String)         { licenseNumber = v; isSaved = false }
    fun onProviderChange(v: String)        { insuranceProvider = v; isSaved = false }
    fun onPolicyNumberChange(v: String)    { insurancePolicyNumber = v; isSaved = false }
    fun onExpiryChange(v: String)          { insuranceExpiry = v; isSaved = false }

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val profile = loadUserProfile()
                if (profile != null) {
                    firstName             = profile.firstName
                    lastName              = profile.lastName
                    email                 = profile.email.ifBlank { userEmail() }
                    licenseNumber         = profile.licenseNumber
                    insuranceProvider     = profile.insuranceProvider
                    insurancePolicyNumber = profile.insurancePolicyNumber
                    insuranceExpiry       = profile.insuranceExpiry
                } else {
                    email = userEmail()
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveProfile() {
        errorMessage = ""
        isSaved = false
        viewModelScope.launch {
            isLoading = true
            try {
                saveUserProfile(UserProfile(
                    firstName             = firstName.trim(),
                    lastName              = lastName.trim(),
                    email                 = email.trim(),
                    licenseNumber         = licenseNumber.trim(),
                    insuranceProvider     = insuranceProvider.trim(),
                    insurancePolicyNumber = insurancePolicyNumber.trim(),
                    insuranceExpiry       = insuranceExpiry.trim()
                ))
                isSaved = true
            } catch (e: Exception) {
                errorMessage = "Failed to save: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    navController: NavController,
    viewModel: AccountViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Bottombar(
                homePage = false,
                reportPage = false,
                settingPage = true,
                historyPage = false,
                navController = navController
            )
        }
    ) { paddingValues ->

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
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
            // ── PROFILE CARD ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(58.dp))
                    }

                    OutlinedTextField(
                        value = viewModel.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
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

            // ── LICENSE & INSURANCE ───────────────────────────────
            Text("License & Insurance", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(viewModel.licenseNumber, viewModel::onLicenseChange, label = { Text("Driver's License Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(viewModel.insuranceProvider, viewModel::onProviderChange, label = { Text("Insurance Provider") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(viewModel.insurancePolicyNumber, viewModel::onPolicyNumberChange, label = { Text("Policy Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(viewModel.insuranceExpiry, viewModel::onExpiryChange, label = { Text("Insurance Expiry (MM/YYYY)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    if (viewModel.errorMessage.isNotBlank()) {
                        Text(viewModel.errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (viewModel.isSaved) {
                        Text("Profile saved!", color = Color(0xFF388E3C), style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = viewModel::saveProfile,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Save Profile", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // ── CHANGE EMAIL / PASSWORD ───────────────────────────
            Button(
                onClick = { navController.navigate(Routes.CHANGE_EMAIL_PAGE) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) { Text("Change Email", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }

            Button(
                onClick = { navController.navigate(Routes.CHANGE_PASSWORD_PAGE) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) { Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
