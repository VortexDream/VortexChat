package com.vortex.android.vortexchat.firebase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

//Базовый аутентификатор, от которого наследуют другие классы
//для имплементации разных видов регистрациии входа
interface BaseAuthenticator {

    suspend fun signUpWithEmailPassword(email:String , password:String, username: String) : FirebaseUser?

    suspend fun signInWithEmailPassword(email: String , password: String): FirebaseUser?

    fun signOut() : FirebaseUser?

    fun getUser() : FirebaseUser?

    suspend fun sendPasswordReset(email :String)
}