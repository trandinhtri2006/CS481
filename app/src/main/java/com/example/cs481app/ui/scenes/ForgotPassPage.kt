package com.example.cs481app.ui.scenes

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.R

class ForgotPassViewModel : ViewModel() {

    var userEmail by mutableStateOf("")
        private set

    var newPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

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
        if (newPassword.equals("admin123", ignoreCase = true)) {
            return true
        } else {
            errorMessage = "Need implementation"
            return false
        }
    }
}


// FORGOT PASSWORD SCREEN
// Allows user to reset password using email verification
// Uses ViewModel for state handling and validation logic
@Composable
fun ForgotPassPage(
    navController: NavController,

    // ViewModel handles input state + reset logic
    viewModel: ForgotPassViewModel = viewModel()
) {

    // Full-screen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // BACK BUTTON
        // Returns user to login screen
        BackButton(
            navController,
            Routes.LOGIN_PAGE,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )


        // MAIN CONTENT LAYOUT
        // Centered form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // APP LOGO
            Image(
                modifier = Modifier.size(300.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Application Logo"
            )

            Spacer(modifier = Modifier.height(32.dp))


            // EMAIL INPUT FIELD
            // Used to identify user account
            OutlinedTextField(
                value = viewModel.userEmail,

                // Updates email state in ViewModel
                onValueChange = viewModel::onEmailChange,

                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // NEW PASSWORD INPUT FIELD
            // User sets a new password
            OutlinedTextField(
                value = viewModel.newPassword,

                // Updates new password state in ViewModel
                onValueChange = viewModel::onNewPassChange,

                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // RESET PASSWORD BUTTON
            // Triggers password reset logic
            Button(
                onClick = {

                    // Validate inputs and reset password
                    // Navigate to account page after success
                    viewModel.valid {
                        navController.navigate(Routes.ACCOUNT_PAGE)
                    }
                }
            ) {
                Text("Reset Password")
            }
        }
    }
}