package com.vortex.android.vortexchat.ui.chats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.firebase.OnlineDatabase
import com.vortex.android.vortexchat.model.User
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import com.vortex.android.vortexchat.ui.login.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val database: OnlineDatabase,
    private val repository: BaseAuthRepository
) : ViewModel() {

    private val TAG = "ChatsViewModel"

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList : StateFlow<List<User>>
        get() = _userList.asStateFlow()

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser : StateFlow<FirebaseUser?>
        get() = _firebaseUser.asStateFlow()

    init {
        _firebaseUser.value = repository.getCurrentUser()
        viewModelScope.launch {
            database.getAllUsers()
            database.userList.collect {
                _userList.value = it
                Log.d(TAG, userList.value.toString())
            }
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }
}