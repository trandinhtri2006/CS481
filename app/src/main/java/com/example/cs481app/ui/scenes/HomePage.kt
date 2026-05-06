package com.example.cs481app.ui.scenes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color


@Composable
fun HomePage(
    navController: NavController
) {
    val name = "TONY"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(75.dp)
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Hey $name", fontSize = 36.sp, color = Color.White, modifier = Modifier.padding(10.dp))

            BadgedBox(
                badge = {
                    Badge { Text("5") }  // number of notifications
                }
            ) {
                IconButton(onClick = { /* open notifications */ }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(100.dp),
                        tint = Color.White
                    )
                }
            }
        }

        // Center content
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(35.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(150.dp),
                    tint = Color.White
                )
                Text(text = "Call 911", fontSize = 50.sp, color = Color.White)
            }
        }

        // Bottom Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(75.dp)
                .background(Color.Black),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {navController.navigate(Routes.HOME_PAGE)}
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "HomeButton",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {navController.navigate(Routes.HOME_PAGE)}
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Report",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {navController.navigate(Routes.HISTORY_PAGE)}
            ) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "History",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {navController.navigate(Routes.ACCOUNT_PAGE)}
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "Profile",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
        }
    }
}
