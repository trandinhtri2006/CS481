package com.example.cs481app.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cs481app.Auth.UserProfile
import com.example.cs481app.Auth.loadUserProfile
import com.example.cs481app.Auth.saveUserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailPage(navController: NavController) {
    var newEmail by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Email") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter your new email address below.",
                fontSize = 15.sp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it; errorMessage = ""; successMessage = "" },
                label = { Text("New Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = confirmEmail,
                onValueChange = { confirmEmail = it; errorMessage = ""; successMessage = "" },
                label = { Text("Confirm New Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (successMessage.isNotBlank()) {
                Text(
                    text = successMessage,
                    color = Color(0xFF388E3C),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    when {
                        newEmail.isBlank() -> errorMessage = "Email cannot be empty."
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() ->
                            errorMessage = "Please enter a valid email address."
                        newEmail != confirmEmail -> errorMessage = "Emails do not match."
                        else -> scope.launch {
                            isLoading = true
                            errorMessage = ""
                            try {
                                Firebase.auth.currentUser?.updateEmail(newEmail)?.await()

                                // Keep Firestore UserProfile in sync
                                val existing = loadUserProfile() ?: UserProfile()
                                saveUserProfile(existing.copy(email = newEmail))

                                successMessage = "Email updated successfully."
                                newEmail = ""
                                confirmEmail = ""
                            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                                errorMessage = "Please log out and log back in before changing your email."
                            } catch (e: Exception) {
                                errorMessage = "Failed to update email: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = if (isLoading) "Updating..." else "Reset Email",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
