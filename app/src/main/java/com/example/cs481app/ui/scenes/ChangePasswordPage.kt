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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

class ChangePasswordPageViewModel : ViewModel() {
    var userEmail by mutableStateOf("")
        private set
    var oldPassword by mutableStateOf("")
        private set

    var newPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    fun onOldPassChange(newValue: String) {
        oldPassword = newValue
    }

    fun onNewPassChange(newValue: String) {
        newPassword = newValue
    }

    fun onEmailChange(newValue: String) {
        userEmail = newValue
    }
    fun valid(onSuccess: () -> Unit) {
        if (validation()) {
            onSuccess()
        }
    }

    private fun validation(): Boolean{
        if (newPassword.equals(oldPassword)) {
            return true
        } else {
            errorMessage = "Need implementation"
            return false
        }
    }
}


// CHANGE PASSWORD SCREEN
// Allows user to update their password after verifying old credentials
// Uses ViewModel to manage input state and validation logic
@Composable
fun ChangePasswordPage(
    navController: NavController,

    // ViewModel holds UI state and business logic
    viewModel: ChangePasswordPageViewModel = viewModel()
) {

    // Full-screen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Back button to return to account screen
        BackButton(
            navController,
            Routes.ACCOUNT_PAGE,
            modifier = Modifier.align(Alignment.TopStart)
        )


        // FORM CONTAINER
        // Centered input form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // EMAIL INPUT FIELD
            // Used to verify user identity
            OutlinedTextField(
                value = viewModel.userEmail,

                // Updates email state in ViewModel
                onValueChange = viewModel::onEmailChange,

                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // OLD PASSWORD FIELD
            // Required for authentication before update
            OutlinedTextField(
                value = viewModel.oldPassword,

                // Updates old password state in ViewModel
                onValueChange = viewModel::onOldPassChange,

                label = { Text("Old Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // NEW PASSWORD FIELD
            // User enters updated password
            OutlinedTextField(
                value = viewModel.newPassword,

                // Updates new password state in ViewModel
                onValueChange = viewModel::onNewPassChange,

                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // UPDATE BUTTON
            // Triggers validation + password update logic
            Button(
                onClick = {

                    // Validate inputs and update password
                    // Navigate back to account screen on success
                    viewModel.valid {
                        navController.navigate(Routes.ACCOUNT_PAGE)
                    }
                }
            ) {
                Text("Update Password")
            }
        }
    }
}