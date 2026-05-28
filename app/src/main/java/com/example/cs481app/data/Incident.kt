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
    val witnessInfo: List<Witness> = emptyList(),        // null if no witnesses
    val photoUrls: List<String> = emptyList(),
    val hash: String = "",              // SHA-256 hash of record
    val timestamp: Long = 0L           // timestamp
)

data class Witness(
    var name: String = "",
    var phone: String = ""
)