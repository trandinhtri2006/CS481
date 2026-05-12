package com.example.cs481app.ui.scenes

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
 import androidx.navigation.compose.rememberNavController

@Composable
fun SceneNavigationGraph() {
    val navController = rememberNavController()

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
            HomePage(navController)
        }

        composable(Routes.HISTORY_PAGE) {
            HistoryPage(navController)
        }

        composable(Routes.SETTING_PAGE) {
            SettingPage(navController)
        }

        composable(Routes.ACCOUNT_PAGE) {
            AccountPage(navController)
        }

        composable(Routes.CHANGE_EMAIL_PAGE) {
            ChangeEmailPage(navController)
        }

        composable(Routes.CHANGE_PASSWORD_PAGE) {
            ChangePasswordPage(navController)
        }

        composable(Routes.REPORT_PAGE) {
            ReportPage(navController)
        }

        composable (Routes.AI_CHATBOX) {
            AIChatBoxPage(navController)
        }
    }
}


@Composable
fun BackButton(
    navController: NavController,
    pageName: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {navController.navigate(pageName)},
        modifier = modifier
    ) {
        Text("Back")
    }
}