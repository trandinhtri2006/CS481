package com.example.cs481app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cs481app.ui.scenes.SceneNavigationGraph
import com.example.cs481app.ui.theme.CS481appTheme
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureFirebaseServices()
        enableEdgeToEdge()
        setContent {
            CS481appTheme {
                checkPermission()
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

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

}


