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
    val otherPartyName: String = "",     // multiparty only
    val otherPartyPhone: String = "",    // multiparty only
    val otherPartyLicense: String = "",  // multiparty only
    val otherPartyInsurance: String = "", // multiparty only
    val witnessInfo: List<Witness> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val hash: String = "",
    val timestamp: Long = 0L
)

data class Witness(
    var name: String = "",
    var phone: String = ""
)