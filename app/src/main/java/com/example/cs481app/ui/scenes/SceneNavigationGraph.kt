package com.example.cs481app.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
//
//        composable(Routes.HISTORY_PAGE) {
//
//        }
//
//        composable(Routes.SETTING_PAGE) {
//
//        }
//
//        composable(Routes.ACCOUNT_PAGE) {
//
//        }
//
        composable(Routes.CHANGE_EMAIL_PAGE) {
            ChangeEmailPage(navController)
        }

        composable(Routes.CHANGE_PASSWORD_PAGE) {
            ChangePasswordPage(navController)
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