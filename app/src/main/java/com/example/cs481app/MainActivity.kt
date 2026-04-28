package com.example.cs481app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.cs481app.ui.scenes.SceneNavigationGraph
import com.example.cs481app.ui.theme.CS481appTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}


