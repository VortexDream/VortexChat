package com.vortex.android.vortexchat.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vortex.android.vortexchat.model.LastMessage
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.model.User
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnlineDatabase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val repository: BaseAuthRepository
) : BaseDatabase {

    private val TAG = "OnlineDatabase"

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList : StateFlow<List<User>>
        get() = _userList.asStateFlow()

    private val _globalChatMessageList = MutableStateFlow<List<Message>>(emptyList())
    val globalChatMessageList : StateFlow<List<Message>>
        get() = _globalChatMessageList.asStateFlow()

    private val _dialogMessageList = MutableStateFlow<List<Message>>(emptyList())
    val dialogMessageList : StateFlow<List<Message>>
        get() = _dialogMessageList.asStateFlow()

    private val _lastDialogMessageList = MutableStateFlow<List<LastMessage>>(emptyList())
    val lastDialogMessageList : StateFlow<List<LastMessage>>
        get() = _lastDialogMessageList.asStateFlow()

    private val currentUserId = repository.getCurrentUser()!!.uid

    override suspend fun getAllUsers() {
        val usersRef = firebaseDatabase.getReference("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val users = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {

                    val email = userSnapshot.child("email").getValue(String::class.java)
                    val userId = userSnapshot.child("userId").getValue(String::class.java)
                    val username = userSnapshot.child("username").getValue(String::class.java)

                    val user = User(userId, username, email)

                    if (userId != currentUserId) {
                        users.add(user)
                    }
                }

                _userList.value = users
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок при чтении данных
                _userList.value = emptyList()
            }
        })
    }

    override suspend fun getAllGlobalChatMessages() {
        val globalChatMessagesRef = firebaseDatabase.getReference("GlobalChatMessages")
        globalChatMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val messages = mutableListOf<Message>()
                for (messageSnapshot in dataSnapshot.children) {

                    val senderUserId = messageSnapshot.child("senderUserId").getValue(String::class.java)!!
                    val text = messageSnapshot.child("text").getValue(String::class.java)!!
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java)!!

                    val message = Message(senderUserId, text, timestamp)
                    messages.add(message)
                }

                _globalChatMessageList.value = messages
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок при чтении данных
                _globalChatMessageList.value = emptyList()
            }
        })
    }

    override suspend fun sendMessageToGlobalChat(message: Message) {
        firebaseDatabase.reference.child("GlobalChatMessages").push().setValue(message)
    }

    override suspend fun getAllDialogMessages(otherUserId: String) {
        val dialogMessagesRef = firebaseDatabase
            .getReference("DialogMessages")
            .child(currentUserId)
            .child(otherUserId)
        dialogMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val messages = mutableListOf<Message>()
                for (messageSnapshot in dataSnapshot.children) {

                    val senderUserId = messageSnapshot.child("senderUserId").getValue(String::class.java)!!
                    val text = messageSnapshot.child("text").getValue(String::class.java)!!
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java)!!

                    val message = Message(senderUserId, text, timestamp)
                    messages.add(message)
                }

                _dialogMessageList.value = messages
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок при чтении данных
                _dialogMessageList.value = emptyList()
            }
        })
    }

    override suspend fun sendMessageToDialog(message: Message, otherUserId: String) {
        val lastMessage = LastMessage(currentUserId, otherUserId, message.text)
        firebaseDatabase.reference
            .child("DialogMessages")
            .child(currentUserId)
            .child(otherUserId)
            .push()
            .setValue(message)
        firebaseDatabase.reference
            .child("DialogMessages")
            .child(otherUserId)
            .child(currentUserId)
            .push()
            .setValue(message)
        firebaseDatabase.reference
            .child("LastDialogMessages")
            .child(currentUserId)
            .child(otherUserId)
            .setValue(lastMessage)
        firebaseDatabase.reference
            .child("LastDialogMessages")
            .child(otherUserId)
            .child(currentUserId)
            .setValue(lastMessage)
    }

    override suspend fun getLastDialogMessages() {
        val dialogMessagesRef = firebaseDatabase
            .getReference("LastDialogMessages")

        dialogMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val messages = mutableListOf<LastMessage>()
                for (messageSnapshot in dataSnapshot.children) {
                    for (childrenSnapshot in messageSnapshot.children) {
                        val userId1 = childrenSnapshot.child("userId1").getValue(String::class.java)
                        val userId2 = childrenSnapshot.child("userId2").getValue(String::class.java)
                        val text = childrenSnapshot.child("text").getValue(String::class.java)
                        val timestamp = childrenSnapshot.child("timestamp").getValue(Long::class.java)

                        if (userId1 != null && userId2 != null && text != null && timestamp != null) {
                            val message = LastMessage(userId1, userId2, text, timestamp)
                            messages.add(message)
                        }
                    }
                }
                _lastDialogMessageList.value = messages
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок при чтении данных
                _lastDialogMessageList.value = emptyList()
            }
        })
    }
}
