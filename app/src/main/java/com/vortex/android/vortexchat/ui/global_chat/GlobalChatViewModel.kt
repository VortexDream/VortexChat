package com.vortex.android.vortexchat.ui.global_chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ServerValue
import com.vortex.android.vortexchat.firebase.OnlineDatabase
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalChatViewModel @Inject constructor(
    private val database: OnlineDatabase,
    private val repository: BaseAuthRepository
) : ViewModel() {

    private val TAG = "GlobalChatViewModel"

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser : StateFlow<FirebaseUser?>
        get() = _firebaseUser.asStateFlow()

    private val _globalChatMessageList = MutableStateFlow<List<Message>>(emptyList())
    val globalChatMessageList : StateFlow<List<Message>>
        get() = _globalChatMessageList.asStateFlow()

    init {
        _firebaseUser.value = repository.getCurrentUser()
        viewModelScope.launch {
            database.getAllGlobalChatMessages()
            database.globalChatMessageList.collect {
                _globalChatMessageList.value = it
            }
        }
    }

    fun sendMessage(text: String) {
        val message = Message(
            senderUserId = currentUser.value!!.uid,
            text = text
        )
        viewModelScope.launch {
            database.sendMessageToGlobalChat(message)
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }

}