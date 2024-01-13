package com.vortex.android.vortexchat.ui.login.sign_in

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentSignInBinding
import com.vortex.android.vortexchat.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val TAG = "SignInFragment"
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).supportActionBar?.hide()
        _binding = FragmentSignInBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToChannels()
        registerObservers()
        binding.apply {
            signInButton.setOnClickListener {
                progressBar.isVisible = true
                val email = emailField.text.toString()
                val password = passwordField.text.toString()
                viewLifecycleOwner.lifecycleScope.launch {
                    loginViewModel.signInUser(email, password)
                }
                requireActivity().currentFocus?.let { view ->
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }

            resetPasswordButton.setOnClickListener {
                findNavController().navigate(SignInFragmentDirections.resetPassword())
            }

            haveAccountButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    //Переход в другую активность при успешной регистрации
    private fun registerObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.currentUser.collect { user ->
                    user?.let {
                        Log.d(TAG, "HOMESCREEN")
                        Log.d(TAG, "$user, ${user.displayName.toString()}")
                        findNavController().navigate(SignInFragmentDirections.signInToHome())
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
                        binding.progressBar.isInvisible = true
                        Snackbar.make(binding.root, getString(event.errorRes), Snackbar.LENGTH_SHORT)
                            .setBackgroundTintMode(PorterDuff.Mode.SRC_OVER)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            .show()
                    }
                    is LoginViewModel.AllEvents.Message -> {
                        Snackbar.make(binding.root, getString(event.messageRes), Snackbar.LENGTH_SHORT)
                            .setBackgroundTintMode(PorterDuff.Mode.SRC_OVER)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            .show()
                    }
                    is LoginViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1) {
                            binding.progressBar.isInvisible = true
                            Snackbar.make(binding.root, getString(R.string.error_empty_email), Snackbar.LENGTH_SHORT)
                                .setBackgroundTintMode(PorterDuff.Mode.SRC_OVER)
                                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant))
                                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                .show()
                        }

                        if (event.code == 2) {
                            binding.progressBar.isInvisible = true
                            Snackbar.make(binding.root, getString(R.string.error_empty_password), Snackbar.LENGTH_SHORT)
                                .setBackgroundTintMode(PorterDuff.Mode.SRC_OVER)
                                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant))
                                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}