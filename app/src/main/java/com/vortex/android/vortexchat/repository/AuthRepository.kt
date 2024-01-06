package com.vortex.android.vortexchat.repository

import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.firebase.BaseAuthenticator
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authenticator : BaseAuthenticator
) : BaseAuthRepository {

    private var currentUser : FirebaseUser? = null

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authenticator.signInWithEmailPassword(email , password)
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String, username: String): FirebaseUser? {
        return authenticator.signUpWithEmailPassword(email , password, username)
    }

    override fun signOut(): FirebaseUser? {
        currentUser = authenticator.signOut()
        return currentUser
    }

    override fun getCurrentUser(): FirebaseUser? {
        return if (currentUser != null) {
            currentUser
        } else {
            currentUser = authenticator.getUser()
            currentUser
        }
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        authenticator.sendPasswordReset(email)
        return true
    }
}