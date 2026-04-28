package com.example.cs481app.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs481app.R

@Composable
fun InitialPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Application Logo"
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = {navController.navigate(Routes.LOGIN_PAGE)},
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {navController.navigate(Routes.REGISTER_PAGE)},
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Create An Account")
        }
    }
}