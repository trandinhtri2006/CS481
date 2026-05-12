package com.example.cs481app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.cs481app.ui.scenes.SceneNavigationGraph
import com.example.cs481app.ui.theme.CS481appTheme
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureFirebaseServices()
        enableEdgeToEdge()
        setContent {
            CS481appTheme {
                SceneNavigation()
            }
        }
    }

    @Composable
    fun SceneNavigation() {
        SceneNavigationGraph()
    }

    private fun configureFirebaseServices() {
        if (BuildConfig.DEBUG) {
            val LOCALHOST = "127.0.0.1"
            val AUTH_PORT = 4400
            Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        }
    }
}


