package com.vortex.android.vortexchat.firebase

import com.google.firebase.auth.FirebaseUser

//Базовый аутентификатор, от которого наследуют другие классы
//для имплементации разных видов регистрациии входа
interface BaseAuthenticator {

    suspend fun signUpWithEmailPassword(email:String , password:String) : FirebaseUser?

    suspend fun signInWithEmailPassword(email: String , password: String): FirebaseUser?

    fun signOut() : FirebaseUser?

    fun getUser() : FirebaseUser?

    suspend fun sendPasswordReset(email :String)
}