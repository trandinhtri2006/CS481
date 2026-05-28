package com.example.cs481app.geolocation

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        callback: (Double, Double) -> Unit
    ) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    callback(latitude, longitude)
                }
            }
    }
}