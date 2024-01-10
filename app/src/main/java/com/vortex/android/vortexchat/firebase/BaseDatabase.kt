package com.vortex.android.vortexchat.firebase

import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.model.User

//Базовый доступ к бд, от которого наследуют другие классы
interface BaseDatabase {

    suspend fun getAllUsers()

    suspend fun getAllGlobalChatMessages()

    suspend fun sendMessageToGlobalChat(message: Message)

    suspend fun sendMessageToDialog(message: Message, otherUserId: String)

    suspend fun getAllDialogMessages(otherUserId: String)

    suspend fun getLastDialogMessages()

}