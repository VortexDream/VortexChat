package com.vortex.android.vortexchat.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
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


    //Проверка валидности
    fun signInUser(email: String , password: String) = viewModelScope.launch{
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
    fun signUpUser(email : String, password: String, username: String) = viewModelScope.launch {
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
    private fun actualSignInUser(email:String, password: String) = viewModelScope.launch {
        try {
            val user = repository.signInWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.value = it
                eventsChannel.send(AllEvents.Message("login success"))
            }
        } catch(e:Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    //Логика регистрации аккаунта
    private fun actualSignUpUser(email:String , password: String, username: String) = viewModelScope.launch {
        try {
            var user = repository.signUpWithEmailPassword(email, password)
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user?.updateProfile(profileUpdates)
            user = repository.getCurrentUser()
            user?.let {
                _firebaseUser.value = it
                eventsChannel.send(AllEvents.Message("sign up success"))
            }
        } catch(e:Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()
            user?.let {
                eventsChannel.send(AllEvents.Message("logout failure"))
            }?: eventsChannel.send(AllEvents.Message("sign out successful"))

            getCurrentUser()

        } catch(e:Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.value = user
    }

    fun verifySendPasswordReset(email: String){
        if(email.isEmpty()){
            viewModelScope.launch {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
        }else{
            sendPasswordResetEmail(email)
        }

    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = repository.sendResetPassword(email)
            if (result){
                eventsChannel.send(AllEvents.Message("reset email sent"))
            }else{
                eventsChannel.send(AllEvents.Error("could not send password reset"))
            }
        }catch (e : Exception){
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AllEvents.Error(error[1]))
        }
    }

    sealed class AllEvents {
        data class Message(val message : String) : AllEvents()
        data class ErrorCode(val code : Int): AllEvents()
        data class Error(val error : String) : AllEvents()
    }
}