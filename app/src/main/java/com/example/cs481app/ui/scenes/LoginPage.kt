package com.example.cs481app.ui.scenes

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.cs481app.ui.Auth.logIn
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    var userEmail by mutableStateOf("")
        private set

    var userPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    fun onEmailChange(newValue: String) {
        userEmail = newValue
    }

    fun onPasswordChange(newValue: String) {
        userPassword = newValue
    }

    fun valid(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                logIn(userEmail, userPassword)
                onSuccess()
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message ?: "Invalid credentials"
            } catch (e: Exception) {
                errorMessage = e.message ?: "Login failed. Please try again."
            }
        }
    }

}


// LOGIN SCREEN
// Entry point of the app where users authenticate
// Handles email/password login via ViewModel
@Composable
fun LoginPage(
    navController: NavController,

    // ViewModel manages login state, input fields, and validation logic
    viewModel: LoginViewModel = viewModel()
) {

    // Root container
    Box(
        modifier = Modifier.background(Color.White)
    ) {


        // MAIN CONTENT COLUMN
        // Centers login form vertically
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // HEADER TEXT
            Text("Welcome To AccidentNow Witness Manager")

            Spacer(modifier = Modifier.height(32.dp))


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

            Spacer(modifier = Modifier.height(24.dp))

            // ERROR MESSAGE DISPLAY
            // Shown when login validation fails
            if (!viewModel.errorMessage.isBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(15.dp))
            }

            // LOGIN BUTTON
            // Attempts authentication and navigates to Home screen on success
            Button(
                onClick = {
                    viewModel.valid {
                        navController.navigate(Routes.HOME_PAGE)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In")
            }

            Spacer(modifier = Modifier.height(16.dp))


            // NAVIGATION LINKS
            // Forgot password + Register account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Navigate to password recovery screen
                TextButton(
                    onClick = {
                        navController.navigate(Routes.FORGOT_PASS_PAGE)
                    }
                ) {
                    Text("Forgot password?", color = Color.Blue)
                }

                // Navigate to registration screen
                TextButton(
                    onClick = {
                        navController.navigate(Routes.REGISTER_PAGE)
                    }
                ) {
                    Text("Don't Have An Account?", color = Color.Blue)
                }
            }
        }
    }
}
