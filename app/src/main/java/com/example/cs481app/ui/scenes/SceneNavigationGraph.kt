package com.example.cs481app.ui.scenes

import androidx.compose.runtime.Composable
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

//        composable(Routes.FORGOT_PASS_PAGE) {
//
//        }
//
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
//        composable(Routes.CHANGE_EMAIL_PAGE) {
//
//        }
//
//        composable(Routes.CHANGE_USERNAME_PAGE) {
//
//        }
//
//        composable(Routes.CHANGE_PASSWORD_PAGE) {
//
//        }
    }
}
