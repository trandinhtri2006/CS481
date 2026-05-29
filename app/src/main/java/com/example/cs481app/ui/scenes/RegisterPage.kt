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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.R
import com.example.cs481app.Auth.createUser
import com.example.cs481app.Auth.deleteUser
import com.example.cs481app.Auth.emailVerified
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

    // Controls visibility of the "verification email sent" dialog
    var showEmailSentDialog by mutableStateOf(false)
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
    fun dismissEmailSentDialog() {
        showEmailSentDialog = false
    }

    fun valid(onSuccess: () -> Unit) {
        errorMessage = ""

        viewModelScope.launch {
            try {
                if (createUser(userEmail, userPassword, userConPassword)) {
                    // Show the "email sent" pop-up; navigation happens when user taps OK
                    showEmailSentDialog = true
                }
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message.toString()
            } catch (e: Exception) {
                errorMessage = "Something went wrong: ${e.message}"
            }
        }
    }
}


// REGISTRATION SCREEN
// Allows new users to create an account.
// After successful creation an AlertDialog confirms the verification email was sent.
@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {

    // EMAIL SENT DIALOG
    // Shown once the account is created and the verification email is dispatched
    if (viewModel.showEmailSentDialog) {
        AlertDialog(
            onDismissRequest = { /* force user to tap OK */ },
            title = {
                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "A verification link has been sent to\n${viewModel.userEmail}\n\n" +
                            "Please check your inbox (and spam folder) and verify " +
                            "your email before logging in.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissEmailSentDialog()
                        navController.navigate(Routes.LOGIN_PAGE)
                    }
                ) {
                    Text("OK, got it!")
                }
            }
        )
    }


    // Full-screen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // BACK NAVIGATION BUTTON
        // Cleans up unverified Firebase account before returning to login
        Button(
            onClick = {
                if (!emailVerified()) deleteUser()
                navController.navigate(Routes.LOGIN_PAGE)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        ) {
            Text("Back")
        }

        // MAIN CONTENT COLUMN
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
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONFIRM PASSWORD FIELD
            OutlinedTextField(
                value = viewModel.userConPassword,
                onValueChange = viewModel::onConPasswordChange,
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ERROR MESSAGE DISPLAY
            if (viewModel.errorMessage.isNotBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            // CREATE ACCOUNT BUTTON
            Button(
                onClick = {
                    // valid() shows the dialog on success; navigation is inside the dialog
                    viewModel.valid {}
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create An Account")
            }
        }
    }
}