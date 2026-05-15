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
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.R
import com.example.cs481app.ui.Auth.createUser
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {
    var userEmail by mutableStateOf("")
        private set
    var userPassword by mutableStateOf("")
        private set
    var userConPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    fun onEmailChange(newValue: String) {
        userEmail = newValue
    }

    fun onPasswordChange(newValue: String) {
        userPassword = newValue
    }

    fun onConPasswordChange(newValue: String) {
        userConPassword = newValue
    }

    fun valid(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                createUser(userEmail, userPassword, userConPassword)
                onSuccess()
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message.toString()
            } catch (e: Exception) {
                errorMessage = "Something went wrong: ${e.message}"
            }
        }
    }
}


// REGISTRATION SCREEN
// Allows new users to create an account
// Handles input via ViewModel and validates user credentials
@Composable
fun RegisterPage(
    navController: NavController,

    // ViewModel manages form state, validation, and error messages
    viewModel: RegisterViewModel = viewModel()
) {

    // Full-screen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        // BACK NAVIGATION BUTTON
        // Returns user to login screen
        BackButton(
            navController,
            Routes.LOGIN_PAGE,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )


        // MAIN CONTENT COLUMN
        // Centers registration form vertically
        Column(
            Modifier
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
            OutlinedTextField(
                value = viewModel.userEmail,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // PASSWORD INPUT FIELD
            OutlinedTextField(
                value = viewModel.userPassword,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // CONFIRM PASSWORD FIELD
            // Ensures user correctly re-enters password
            OutlinedTextField(
                value = viewModel.userConPassword,
                onValueChange = viewModel::onConPasswordChange,
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            // ERROR MESSAGE DISPLAY
            // Shown when validation fails
            if (!viewModel.errorMessage.isBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(15.dp))
            }


            // CREATE ACCOUNT BUTTON
            // Triggers validation and account creation logic
            Button(
                onClick = {

                    // Validate inputs and create account
                    // Navigate back to login screen on success
                    viewModel.valid {
                        navController.navigate(Routes.LOGIN_PAGE)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create An Account")
            }
        }
    }
}
