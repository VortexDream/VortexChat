package com.vortex.android.vortexchat.ui.login

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
//Общая ViewModel для трёх фрагментов
class LoginViewModel @Inject constructor(
    private val repository : BaseAuthRepository
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser : StateFlow<FirebaseUser?>
        get() = _firebaseUser.asStateFlow()

    //Канал для передачи ивентов
    private val eventsChannel = Channel<AllEvents>()
    val allEventsFlow = eventsChannel.receiveAsFlow()

    init {
        _firebaseUser.value = repository.getCurrentUser()
    }

    //Проверка валидности
    suspend fun signInUser(email: String , password: String) = viewModelScope.launch{
        when {
            email.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(2))
            }
            else -> {
                actualSignInUser(email , password)
            }
        }
    }

    //Проверка валидности
    suspend fun signUpUser(email : String, password: String, username: String) = viewModelScope.launch {
        when{
            email.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(2))
            }
            //Если захочу добавить подтверждение пароля
            /*password != confirmPass ->{
                eventsChannel.send(AllEvents.ErrorCode(3))
            }*/
            else -> {
                actualSignUpUser(email, password, username)
            }
        }
    }

    //Логика входа в аккаунт
    private suspend fun actualSignInUser(email:String, password: String) = viewModelScope.launch {
        try {
            val user = repository.signInWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.value = it
                eventsChannel.send(AllEvents.Message(R.string.login_success_message))
            }
        } catch(e:Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(R.string.error_signin))
        }
    }

    //Логика регистрации аккаунта
    private suspend fun actualSignUpUser(email:String , password: String, username: String) = viewModelScope.launch {
        try {
            val user = repository.signUpWithEmailPassword(email, password, username)
            user?.let {
                _firebaseUser.value = it
                eventsChannel.send(AllEvents.Message(R.string.sign_up_success_message))
            }
        } catch(e:Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(R.string.error_signup))
        }
    }

    suspend fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()
            user?.let {
                eventsChannel.send(AllEvents.Message(R.string.error_signout))
            }?: eventsChannel.send(AllEvents.Message(R.string.sign_out_success_message))

            getCurrentUser()

        } catch(e:Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(R.string.error_signout))
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }

    suspend fun verifySendPasswordReset(email: String){
        if(email.isEmpty()){
            viewModelScope.launch {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
        }else{
            sendPasswordResetEmail(email)
        }

    }

    private suspend fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = repository.sendResetPassword(email)
            if (result){
                eventsChannel.send(AllEvents.Message(R.string.reset_password_success_message))
            } else {
                eventsChannel.send(AllEvents.Error(R.string.error_reset_password))
            }
        } catch (e : Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(R.string.error_reset_password))
        }
    }

    sealed class AllEvents {
        //Viewmodel не должна работать со строками
        data class Message(@StringRes val messageRes : Int) : AllEvents()
        data class ErrorCode(val code : Int): AllEvents()
        data class Error(@StringRes val errorRes : Int) : AllEvents()
    }
}