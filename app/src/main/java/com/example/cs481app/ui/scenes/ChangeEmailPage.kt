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

class ChangeEmailViewModel : ViewModel() {
    var userCurrEmail by mutableStateOf("")
        private set

    var userNewEmail by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    fun onCurrEmailChange(newValue : String) {
        userCurrEmail = newValue
    }

    fun onNewEmailChange(newValue: String) {
        userNewEmail = newValue
    }

    fun valid(onSuccess: () -> Unit) {
        if (validation()) {
            onSuccess()
        }
    }

    private fun validation(): Boolean {
        if (!userNewEmail.equals(userCurrEmail)) {
            return true
        } else {
            errorMessage = "Need Implementation"
            return false
        }
    }
}

// CHANGE EMAIL SCREEN
// Allows user to update their account email address
// Uses a ViewModel for state management and validation
@Composable
fun ChangeEmailPage(
    navController: NavController,

    // ViewModel handles input state + validation logic
    viewModel: ChangeEmailViewModel = viewModel()
) {

    // Full-screen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        // BACK NAVIGATION BUTTON
        // Returns user to account page
        BackButton(
            navController,
            Routes.ACCOUNT_PAGE,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // MAIN FORM CONTENT
        // Centered vertically and horizontally
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // CURRENT EMAIL INPUT FIELD
            OutlinedTextField(
                value = viewModel.userCurrEmail,

                // Updates current email state in ViewModel
                onValueChange = viewModel::onCurrEmailChange,

                label = { Text("Old Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // NEW EMAIL INPUT FIELD
            OutlinedTextField(
                value = viewModel.userNewEmail,

                // Updates new email state in ViewModel
                onValueChange = viewModel::onNewEmailChange,

                label = { Text("New Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // SUBMIT BUTTON
            // Triggers validation + email update process
            Button(
                onClick = {

                    // Validate input and update email
                    // On success, navigate back to account page
                    viewModel.valid {
                        navController.navigate(Routes.ACCOUNT_PAGE)
                    }
                }
            ) {
                Text("Change Email")
            }
        }
    }
}