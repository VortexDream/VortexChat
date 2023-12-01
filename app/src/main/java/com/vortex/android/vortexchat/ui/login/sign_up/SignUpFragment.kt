package com.vortex.android.vortexchat.ui.login.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentSignUpBinding
import com.vortex.android.vortexchat.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val TAG = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObservers()
        listenToChannels()
        binding.apply {
            signUpButton.setOnClickListener {
                progressBar.isVisible = true
                val username = usernameField.text.toString()
                val email = emailField.text.toString()
                val password = passwordField.text.toString()
                loginViewModel.signUpUser(email , password , username)
            }

            haveAccountButton.setOnClickListener {
                findNavController().navigate(SignUpFragmentDirections.signInScreen())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Переход в другую активность при успешной регистрации
    private fun registerObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.currentUser.collect { user ->
                    user?.let {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    //Слушатель событий
    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.allEventsFlow.collect { event ->
                when(event){
                    is LoginViewModel.AllEvents.Error -> {
                        binding.apply {
                            errorTxt.text = event.error
                            progressBar.isInvisible = true
                        }
                    }
                    is LoginViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding.apply {
                                emailField.error = "email should not be empty"
                                progressBar.isInvisible = true
                            }


                        if(event.code == 2)
                            binding.apply {
                                passwordField.error = "password should not be empty"
                                progressBar.isInvisible = true
                            }

                        /*if(event.code == 3)
                            binding.apply {
                                confirmPasswordField.error = "passwords do not match"
                                progressBar.isInvisible = true
                            }*/
                    }
                }

            }
        }
    }
}