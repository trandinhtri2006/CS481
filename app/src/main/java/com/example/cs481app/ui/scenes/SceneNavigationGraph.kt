package com.example.cs481app.ui.scenes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cs481app.ai.AIAssistantViewModel
import com.example.cs481app.geolocation.LocationHelper

// Routes that belong to the pre-login flow — the AI FAB is hidden on these.
private val PRE_LOGIN_ROUTES = setOf(
    Routes.INITIAL_PAGE,
    Routes.LOGIN_PAGE,
    Routes.REGISTER_PAGE,
    Routes.FORGOT_PASS_PAGE
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SceneNavigationGraph() {
    val navController = rememberNavController()
    val aiViewModel: AIAssistantViewModel = viewModel()
    var showChat by remember { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showFab = currentRoute != null && currentRoute !in PRE_LOGIN_ROUTES

    val context = LocalContext.current

    var locationPermitted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        locationPermitted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Ask for location permission as soon as the user logs in
    LaunchedEffect(showFab) {
        if (showFab && !locationPermitted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Start updates only when logged in AND permission is granted
    DisposableEffect(showFab, locationPermitted) {
        if (showFab && locationPermitted) LocationHelper.startLocationUpdates(context)
        onDispose { LocationHelper.stopLocationUpdates(context) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.INITIAL_PAGE) {
            composable(Routes.INITIAL_PAGE) {
                InitialPage(navController)
            }

            composable(Routes.LOGIN_PAGE) {
                LoginPage(navController)
            }

            composable(Routes.REGISTER_PAGE) {
                RegisterPage(navController)
            }

            composable(Routes.FORGOT_PASS_PAGE) {
                ForgotPassPage(navController)
            }

            composable(Routes.HOME_PAGE) {
                aiViewModel.setContext("home")
                HomePage(navController)
            }

            composable(Routes.HISTORY_PAGE) {
                aiViewModel.setContext("history")
                HistoryPage(navController)
            }

            composable(Routes.SETTING_PAGE) {
                aiViewModel.setContext("settings")
                SettingPage(navController)
            }

            composable(Routes.REPORT_PAGE) {
                aiViewModel.setContext("report")
                ReportPage(navController, aiViewModel)
            }

            composable("${Routes.INCIDENT_DETAIL_PAGE}/{incidentId}") { backStackEntry ->
                val incidentId = backStackEntry.arguments?.getString("incidentId") ?: ""
                IncidentDetailPage(navController, incidentId)
            }

            composable(Routes.CHANGE_PASSWORD_PAGE) {
                ChangePasswordPage(navController)
            }

            composable(Routes.CHANGE_EMAIL_PAGE) {
                ChangeEmailPage(navController)
            }

            composable(Routes.ACCOUNT_PAGE) {
                AccountPage(navController)
            }
        }

        // ChatFab sits above all screens. Padding keeps it above the bottom nav bar (~80dp).
        if (showFab) {
            ChatFab(
                onClick = { showChat = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 125.dp)
            )
        }
    }

    // ModalBottomSheet renders in its own overlay layer above everything including the nav bar.
    ChatOverlay(
        viewModel = aiViewModel,
        isVisible = showChat,
        onDismiss = { showChat = false }
    )
}

@Composable
fun BackButton(
    navController: NavController,
    pageName: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { navController.navigate(pageName) },
        modifier = modifier
    ) {
        Text("Back")
    }
}