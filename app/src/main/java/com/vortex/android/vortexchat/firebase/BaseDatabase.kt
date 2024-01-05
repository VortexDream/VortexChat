package com.vortex.android.vortexchat.firebase

import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.model.User

//Базовый доступ к бд, от которого наследуют другие классы
interface BaseDatabase {

    suspend fun getAllUsers()

}