package com.vortex.android.vortexchat.ui.chats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentChatsBinding
import com.vortex.android.vortexchat.databinding.FragmentSignUpBinding
import com.vortex.android.vortexchat.ui.login.sign_up.SignUpFragmentDirections
import com.vortex.android.vortexchat.ui.profile.ProfileFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val chatsViewModel: ChatsViewModel by viewModels()
    private val TAG = "ChatsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).showBottomNavigation()
        (activity as MainActivity).supportActionBar?.show()
        _binding = FragmentChatsBinding.inflate(layoutInflater, container,false)
        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatsViewModel.getCurrentUser()
        registerObservers()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatsViewModel.userList.collect { users ->
                    binding.chatsRecyclerView.adapter = ChatListAdapter(users, requireContext()) { userId ->

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatsViewModel.currentUser.collect { user ->
                    if (user == null) {
                        findNavController().navigate(ChatsFragmentDirections.chatsToLogin())
                    }
                }
            }
        }
    }
}