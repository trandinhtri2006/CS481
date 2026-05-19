package com.example.cs481app.data

data class Incident(
    val incidentId: String = "",
    val accidentType: String = "",       //'solo', 'hitandrun', 'multiparty'
    val date: String = "",               // YYYY-MM-DD
    val time: String = "",               // HH:MM
    val location: String = "",           // latitude and longitude
    val driverName: String = "",
    val licenseNumber: String = "",
    val insuranceInfo: String = "",
    val otherPartyInfo: String = "",     // null if solo flow
    val witnessInfo: String = "",        // null if no witnesses
    val photoUrls: List<String> = emptyList(),
    val hash: String = "",              // SHA-256 hash of record
    val timestamp: Long = 0L           // timestamp
)
