package com.example.cs481app.ui.scenes

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.cs481app.Auth.loadUserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

// Home screen composable function
// This is the main page users see after logging in
@Composable
fun HomePage(
    navController: NavController
) {
    var showCallDialog by remember { mutableStateOf(false) }
    val fallback = Firebase.auth.currentUser?.email?.substringBefore("@") ?: "User"
    var name by remember { mutableStateOf(fallback) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val profile = loadUserProfile()
                if (!profile?.firstName.isNullOrBlank()) name = profile!!.firstName
            } catch (_: Exception) {}
        }
    }

    // Grab context here at the composable level so it can be passed to dialNumber
    val context = LocalContext.current

    // Scaffold provides the basic screen structure:
    // - Bottom navigation bar
    // - Floating action button
    // - Main content area
    Scaffold(

        // Bottom navigation menu
        bottomBar = {
            Bottombar(
                homePage = true,
                reportPage = false,
                settingPage = false,
                historyPage = false,
                navController = navController
            )
        },

        ) { paddingValues ->

        // Main vertical layout for the whole screen
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill entire screen
                .padding(paddingValues) // Prevent overlap with scaffold components
                .background(Color(0xFFF5F5F5)) // Light gray background
                .padding(horizontal = 20.dp, vertical = 16.dp),

            // Center items horizontally
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),

                // Space between welcome text and notification icon
                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                // Welcome text section
                Column {

                    // Small greeting text
                    Text(
                        text = "Welcome Back,",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    // User name text
                    Text(
                        text = name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

            }

            // Space between header and emergency button
            Spacer(modifier = Modifier.height(60.dp))


            // EMERGENCY BUTTON SECTION
            // Circular red emergency card
            Card(

                // Large circular button size
                modifier = Modifier.size(280.dp),

                // Make card circular
                shape = CircleShape,

                // Add shadow/elevation
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                ),

                // Emergency red background color
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {

                // Box allows content centering
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showCallDialog = true },
                    // Center content inside circle
                    contentAlignment = Alignment.Center,
                ) {

                    // Column for icon + text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // Emergency alert icon
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Emergency",
                            modifier = Modifier.size(90.dp),
                            tint = Color.White
                        )

                        // Space below icon
                        Spacer(modifier = Modifier.height(16.dp))

                        // Emergency call text
                        Text(
                            text = "CALL 911",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Small spacing
                        Spacer(modifier = Modifier.height(8.dp))

                        // Helper description text
                        Text(
                            text = "Tap for emergency help",
                            fontSize = 14.sp,

                            // Slight transparency effect
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Routes.REPORT_PAGE) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("New Incident Report", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate(Routes.HISTORY_PAGE) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text("Incident History", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Call 911?") },
            text = { Text("This will open your phone dialer. After the call, use the recents button to return to the app.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCallDialog = false
                        dialNumber(context, "911")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Call 911", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showCallDialog = false }) { Text("Cancel") }
            }
        )
    }
}



// BOTTOM NAVIGATION BAR
@Composable
fun Bottombar(

    // Booleans used to track active page
    homePage: Boolean,
    reportPage: Boolean,
    historyPage: Boolean,
    settingPage: Boolean,

    // Navigation controller
    navController: NavController
) {

    // Material 3 bottom navigation bar
    NavigationBar(
        containerColor = Color.Black
    ) {

        // HOME BUTTON
        NavigationBarItem(

            // Currently selected page
            selected = homePage,

            // Navigate to Home page
            onClick = {
                navController.navigate(Routes.HOME_PAGE)
            },

            // Home icon
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            },

            // Colors for selected/unselected states
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )


        // REPORT BUTTON
        NavigationBarItem(
            selected = reportPage,

            // Navigate to Report page
            onClick = {
                navController.navigate(Routes.REPORT_PAGE)
            },

            // Report icon
            icon = {
                Icon(
                    Icons.Default.Description,
                    contentDescription = "Report"
                )
            },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )

        // HISTORY BUTTON
        NavigationBarItem(
            selected = historyPage,

            // Navigate to History page
            onClick = {
                navController.navigate(Routes.HISTORY_PAGE)
            },

            // History/clock icon
            icon = {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "History"
                )
            },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )


        // SETTINGS BUTTON
        NavigationBarItem(
            selected = settingPage,

            // Navigate to Settings/Profile page
            onClick = {
                navController.navigate(Routes.SETTING_PAGE)
            },

            // Profile icon
            icon = {
                Icon(
                    Icons.Default.AccountBox,
                    contentDescription = "Profile"
                )
            },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
    }
}

private fun dialNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
    context.startActivity(intent)
}
