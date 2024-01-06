package com.vortex.android.vortexchat.ui.dialog

import androidx.lifecycle.ViewModel
import com.vortex.android.vortexchat.firebase.OnlineDatabase
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val database: OnlineDatabase,
    private val repository: BaseAuthRepository
) : ViewModel() {

    private val TAG = "ChatsViewModel"

}