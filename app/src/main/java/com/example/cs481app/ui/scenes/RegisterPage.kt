package com.example.cs481app.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs481app.R


class RegisterViewModel : ViewModel() {
    var userEmail by mutableStateOf("")
        private set
    var userPassword by mutableStateOf("")
        private set
    var userConPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")

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
        if (validation()) {
            onSuccess()
        }
    }

    private fun validation(): Boolean {
        if (
            userPassword.equals(userConPassword, ignoreCase = false)
            and !userPassword.isBlank()
            and !userConPassword.isBlank()
            )
        {
            // Implement userEmail constraints
            return true
        } else {
            errorMessage = "Need Implementation"
            return false
        }
    }
}
@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    Column (
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Application Logo"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = viewModel.userEmail,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = viewModel.userPassword,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password field
        OutlinedTextField(
            value = viewModel.userConPassword,
            onValueChange = viewModel::onConPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!viewModel.errorMessage.isBlank()) {
            Text(
                text  = viewModel.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        Button(
            onClick = {viewModel.valid { navController.navigate(Routes.LOGIN_PAGE) }},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create An Account")
        }
    }
}