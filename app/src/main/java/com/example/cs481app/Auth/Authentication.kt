package com.example.cs481app.Auth

import android.util.Patterns
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await


// CREATE USER FUNCTION
// Creates a new Firebase user account and sends a verification email.
// Returns true only after both the account creation AND the verification
// email have completed successfully.
suspend fun createUser(email: String, password: String, confirmPassword: String): Boolean {

    // --- Input validation ---
    require(email.isNotBlank())
    { "Email cannot be blank." }
    require(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    { "Please enter a valid email address." }

    require(password.length in 8..20)
    { "Password must be between 8 and 20 characters long." }
    require(password.any { it.isUpperCase() })
    { "Password must contain at least 1 uppercase letter." }
    require(password.any { it.isLowerCase() })
    { "Password must contain at least 1 lowercase letter." }
    require(password.any { it.isDigit() })
    { "Password must contain at least 1 number." }
    require(password.any { !it.isLetterOrDigit() })
    { "Password must contain at least 1 special character." }
    require(password == confirmPassword)
    { "Password and Confirm Password do not match." }

    return try {
        // Step 1: create the account and AWAIT completion
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        // Step 2: send verification email and AWAIT completion
        // (currentUser is non-null here because Step 1 succeeded)
        Firebase.auth.currentUser
            ?.sendEmailVerification()
            ?.await()

        // Both steps succeeded → signal the UI to show the "email sent" dialog
        true
    }
    catch (e: FirebaseAuthUserCollisionException) {
        throw IllegalArgumentException("An account with this email already exists.")
    }
    catch (e: FirebaseAuthWeakPasswordException) {
        throw IllegalArgumentException("Password is too weak.")
    }
    catch (e: Exception) {
        throw Exception("Something went wrong: ${e.message}")
    }
}


// LOG IN FUNCTION
// Authenticates an existing user with email and password
suspend fun logIn(email: String, password: String): Boolean {
    require(email.isNotBlank())
    { "Email cannot be blank." }
    require(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    { "Please enter a valid email address." }
    require(password.isNotBlank())
    { "Password cannot be blank." }

    return try {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .await()

        true
    }
    catch (e: FirebaseAuthInvalidUserException) {
        throw IllegalArgumentException("No account found with this email.")
    }
    catch (e: FirebaseAuthInvalidCredentialsException) {
        throw IllegalArgumentException("Incorrect email or password.")
    }
    catch (e: Exception) {
        throw Exception("Something went wrong: ${e.message}")
    }
}


// DELETE USER
// Removes the current Firebase account (used when the user backs out before verifying)
fun deleteUser() {
    Firebase.auth.currentUser?.delete()
}