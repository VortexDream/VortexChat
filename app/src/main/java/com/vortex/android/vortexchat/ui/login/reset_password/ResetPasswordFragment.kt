package com.vortex.android.vortexchat.ui.login.reset_password

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentResetPasswordBinding
import com.vortex.android.vortexchat.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding : FragmentResetPasswordBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val loginViewModel : LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).supportActionBar?.hide()
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            resetPasswordButtonOnEmail.setOnClickListener {
                progressBar.isVisible = true
                val email = emailField.text.toString()
                viewLifecycleOwner.lifecycleScope.launch {
                    loginViewModel.verifySendPasswordReset(email)
                }
                requireActivity().currentFocus?.let { view ->
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            returnButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        listenToChannels()
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.allEventsFlow.collect { event ->
                when(event){
                    is LoginViewModel.AllEvents.Message -> {
                        Snackbar.make(binding.root, getString(event.messageRes), Snackbar.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }
                    is LoginViewModel.AllEvents.Error -> {
                        binding.progressBar.isInvisible = true
                        Snackbar.make(binding.root, getString(event.errorRes), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is LoginViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1) {
                            binding.progressBar.isInvisible = true
                            Snackbar.make(binding.root, getString(R.string.error_empty_email), Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }
        }
    }
}