package com.example.cs481app.ui.scenes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cs481app.R

class CrashReportActivity : AppCompatActivity() {

    private lateinit var locationEditText: EditText
    private lateinit var useLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_report)

        locationEditText =
            findViewById(R.id.locationEditText)

        useLocationButton =
            findViewById(R.id.useLocationButton)

        useLocationButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                fillCurrentLocation()

            } else {

                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun fillCurrentLocation() {

        val helper = UserLocation(this)

        helper.getCurrentAddress { address ->

            runOnUiThread {

                if (address != null) {
                    locationEditText.setText(address)
                } else {
                    locationEditText.setText(
                        "Unable to determine location"
                    )
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                fillCurrentLocation()
            }
        }
}