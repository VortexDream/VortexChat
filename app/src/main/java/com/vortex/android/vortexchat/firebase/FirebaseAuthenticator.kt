package com.vortex.android.vortexchat.firebase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.vortex.android.vortexchat.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthenticator @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : BaseAuthenticator {

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
        ): FirebaseUser? {
        Firebase.auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val id = it.result.user!!.uid
                    val user = User(
                        userId = id,
                        email = email,
                        username = username
                    )
                    firebaseDatabase.reference.child("Users").child(id).setValue(user)
                }
            }
            .await()
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        val user = Firebase.auth.currentUser
        user?.updateProfile(profileUpdates)?.await()
        return Firebase.auth.currentUser
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        Firebase.auth.signInWithEmailAndPassword(email , password).await()
        return Firebase.auth.currentUser
    }

    override fun signOut(): FirebaseUser? {
        Firebase.auth.signOut()
        return Firebase.auth.currentUser
    }

    override fun getUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    override suspend fun sendPasswordReset(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).await()
    }
}