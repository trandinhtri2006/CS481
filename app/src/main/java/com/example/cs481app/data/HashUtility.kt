package com.example.cs481app.data

import com.example.cs481app.data.HashUtility.generateHash
import java.security.MessageDigest

//hash utility
//generates and verifies 256 cryptographic hashes for the records \

object HashUtility {

    //
    //takes an inciddnet record to compile all the fields into one string and hash it
    fun generateHash(incident: Incident): String {
        val data = buildString {
            append(incident.accidentType)
            append(incident.date)
            append(incident.time)
            append(incident.location)
            append(incident.driverName)
            append(incident.licenseNumber)
            append(incident.insuranceInfo)
            append(incident.witnessInfo)
            append(incident.timestamp)
        }
        return sha256(data)
    }
}

    //sha256 core function
    //converts a string into a 64 character hexadecimal hash
    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    //verifying hash
    //rehashes incident and compares
    fun verifyHash(incident: Incident): Boolean {
        val recomputedHash = generateHash(incident)
        return recomputedHash == incident.hash
    }