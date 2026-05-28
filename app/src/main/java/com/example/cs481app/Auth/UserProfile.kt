package com.example.cs481app.Auth

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


// DATA MODEL
// Represents a user's profile stored in Firestore
// Default empty strings so Firestore deserialization works without a no-arg constructor issue
data class UserProfile(
    val email: String = "",
    val licenseNumber: String = "",
    val insuranceProvider: String = "",
    val insurancePolicyNumber: String = "",
    val insuranceExpiry: String = ""        // e.g. "MM/YYYY"
)


// FIRESTORE COLLECTION / DOCUMENT PATH
// users/{uid}/profile  (document)
private const val COLLECTION = "users"
private const val PROFILE_DOC = "profile"


// SAVE USER PROFILE
// Writes (or overwrites) the profile document for the current user
suspend fun saveUserProfile(profile: UserProfile) {
    val uid = Firebase.auth.currentUser?.uid
        ?: throw IllegalStateException("No authenticated user.")

    Firebase.firestore
        .collection(COLLECTION)
        .document(uid)
        .collection(PROFILE_DOC)
        .document(PROFILE_DOC)
        .set(profile)
        .await()
}


// LOAD USER PROFILE
// Returns null if no profile has been saved yet
suspend fun loadUserProfile(): UserProfile? {
    val uid = Firebase.auth.currentUser?.uid
        ?: throw IllegalStateException("No authenticated user.")

    val snapshot = Firebase.firestore
        .collection(COLLECTION)
        .document(uid)
        .collection(PROFILE_DOC)
        .document(PROFILE_DOC)
        .get()
        .await()

    return if (snapshot.exists()) snapshot.toObject(UserProfile::class.java) else null
}

fun emailVerified(): Boolean {
    return Firebase.auth.currentUser?.isEmailVerified == true
}

fun userID(): String {
    return Firebase.auth.currentUser?.uid.toString()
}

fun userEmail(): String {
    return Firebase.auth.currentUser?.email.toString()
}