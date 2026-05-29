package com.example.cs481app.geolocation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper {

    private const val REFRESH_INTERVAL_MS = 15 * 60 * 1000L

    var latitude: Double? = null
        private set
    var longitude: Double? = null
        private set
    var lastUpdated: Long? = null
        private set

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        if (locationCallback != null) return

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // Seed from last known location immediately so the first read isn't empty
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null && latitude == null) {
                latitude = location.latitude
                longitude = location.longitude
                lastUpdated = System.currentTimeMillis()
            }
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            REFRESH_INTERVAL_MS
        )
            .setMinUpdateIntervalMillis(REFRESH_INTERVAL_MS)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    latitude = location.latitude
                    longitude = location.longitude
                    lastUpdated = System.currentTimeMillis()
                }
            }
        }

        fusedClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
    }

    fun stopLocationUpdates(context: Context) {
        val callback = locationCallback ?: return
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(callback)
        locationCallback = null
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
        val lat = latitude
        val lon = longitude
        if (lat != null && lon != null) {
            callback(lat, lon)
            return
        }

        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    callback(location.latitude, location.longitude)
                }
            }
    }
}