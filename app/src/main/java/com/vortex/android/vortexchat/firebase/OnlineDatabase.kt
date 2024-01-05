package com.vortex.android.vortexchat.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    val currentUserId = repository.getCurrentUser()!!.uid

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
}
