package com.vortex.android.vortexchat.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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
class DialogViewModel @Inject constructor(
    private val database: OnlineDatabase,
    private val repository: BaseAuthRepository
) : ViewModel() {

    private val TAG = "DialogViewModel"

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser : StateFlow<FirebaseUser?>
        get() = _firebaseUser.asStateFlow()

    private val _dialogMessageList = MutableStateFlow<List<Message>>(emptyList())
    val dialogMessageList : StateFlow<List<Message>>
        get() = _dialogMessageList.asStateFlow()


    init {
        _firebaseUser.value = repository.getCurrentUser()
    }

    fun subscribeToDialogMessages(otherUserId: String) {
        viewModelScope.launch {
            database.getAllDialogMessages(otherUserId)
            database.dialogMessageList.collect {
                _dialogMessageList.value = it
            }
        }
    }

    fun sendMessage(text: String, otherUserId: String) {
        val message = Message(
            senderUserId = currentUser.value!!.uid,
            text = text
        )
        viewModelScope.launch {
            database.sendMessageToDialog(message, otherUserId)
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }

}