package com.vortex.android.vortexchat.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.firebase.BaseStorage
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
class ProfileViewModel @Inject constructor(
    private val repository: BaseAuthRepository,
    private val storage: BaseStorage
) : ViewModel() {

    private val TAG = "ProfileViewModel"

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser : StateFlow<FirebaseUser?>
        get() = _firebaseUser.asStateFlow()

    init {
        _firebaseUser.value = repository.getCurrentUser()
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()
            _firebaseUser.value = user
        } catch(e:Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signOutUser: ${error[1]}")
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }

    fun uploadProfilePic(uri: Uri, toastCallback: () -> Unit, imageViewCallback: () -> Unit) = viewModelScope.launch {
        storage.uploadProfilePic(uri, toastCallback, imageViewCallback)
    }
}