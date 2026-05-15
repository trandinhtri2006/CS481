package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// SETTINGS SCREEN
// Displays user profile info, account actions, and logout option
@Composable
fun SettingPage(
    navController: NavController
) {

    // SCAFFOLD LAYOUT
    // Provides bottom navigation bar and main content area
    Scaffold(

        // Bottom navigation bar (shared across app screens)
        bottomBar = {
            Bottombar(
                homePage = false,
                reportPage = false,
                settingPage = true, // current active tab
                historyPage = false,
                navController = navController
            )
        }

    ) { paddingValues ->

        // MAIN CONTENT COLUMN
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

                // Enables vertical scrolling if content overflows
                .verticalScroll(rememberScrollState())

                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // PROFILE CARD SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // PROFILE AVATAR
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {

                        // Default profile icon (placeholder avatar)
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // USER NAME DISPLAY
                    Text(
                        text = "Tony Stark",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // USER EMAIL DISPLAY
                    Text(
                        text = "tony@email.com",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // EDIT PROFILE BUTTON
                    Button(
                        onClick = {
                            // Navigate to account edit screen
                            navController.navigate(Routes.ACCOUNT_PAGE)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {

                        // Edit icon
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Edit Profile",
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION HEADER
            Text(
                text = "Personal Information",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))


            // LOGOUT BUTTON
            Button(
                onClick = {
                    // Navigate back to initial/login screen
                    navController.navigate(Routes.INITIAL_PAGE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {

                // Logout icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


// PREVIEW FUNCTION
// Used for Android Studio UI preview
@Preview
@Composable
fun SettingPage_Preview() {
    SettingPage(rememberNavController())
}