package com.example.cs481app.data

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

//storage handler
//handling all the firebase storage operations for the photo uploads and deletions

object StorageHandler {

    private val storage = Firebase.storage
    private val auth = Firebase.auth

    // Get current user's UID
    private fun getCurrentUID(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("No user is currently logged in.")
    }

    //uploading photo
    //uploads a single photo to the firebase storage and returns its download url
    suspend fun uploadPhoto(photoUri: Uri, incidentId: String): String {
        val uid = getCurrentUID()
        val filename = "photo_${System.currentTimeMillis()}.jpg"

        val storageRef = storage.reference
            .child("users/$uid/incidents/$incidentId/$filename")

        return try {
            //upload the photo file
            storageRef.putFile(photoUri).await()

            //gets and returns the download url
            storageRef.downloadUrl.await().toString()

        } catch (e: Exception) {
            throw Exception("Failed to upload photo: ${e.message}")
        }
    }

    //uploading more than 1 photo
    //uploads a list of photos and returns a list of downloads
    suspend fun uploadPhotos(photoUris: List<Uri>, incidentId: String): List<String> {
        return photoUris.map { uri ->
            uploadPhoto(uri, incidentId)
        }
    }

    //deleting a photo
    //deletes a single photo from firebase storage using its download url
    suspend fun deletePhoto(downloadUrl: String) {
        try {
            val photoRef = storage.getReferenceFromUrl(downloadUrl)
            photoRef.delete().await()

        } catch (e: Exception) {
            throw Exception("Failed to delete photo: ${e.message}")
        }
    }

    //deletes all photos for an incident
    //deletes all photos that belong to an incident
    suspend fun deleteIncidentPhotos(photoUrls: List<String>) {
        photoUrls.forEach { url ->
            deletePhoto(url)
        }
    }
}