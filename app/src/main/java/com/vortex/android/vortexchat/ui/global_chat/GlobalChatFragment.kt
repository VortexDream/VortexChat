package com.vortex.android.vortexchat.ui.global_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentGlobalChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GlobalChatFragment : Fragment() {

    private var _binding: FragmentGlobalChatBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val globalChatViewModel: GlobalChatViewModel by viewModels()

    @Inject
    lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).showBottomNavigation()
        (activity as MainActivity).supportActionBar?.show()
        _binding = FragmentGlobalChatBinding.inflate(layoutInflater, container,false)
        binding.globalChatRecyclerView.layoutManager = LinearLayoutManager(context)
            .apply { stackFromEnd = true }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalChatViewModel.getCurrentUser()
        registerObservers()
        binding.apply {
            sendButton.setOnClickListener {
                val messageField = binding.messageTextField.editText
                if (messageField!!.text.toString() != "") {
                    globalChatViewModel.sendMessage(messageField.text.toString())
                    messageField.text.clear()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                globalChatViewModel.globalChatMessageList.collect { messages ->
                    binding.globalChatRecyclerView.adapter = GlobalChatListAdapter(
                        messages,
                        requireContext(),
                        globalChatViewModel.currentUser.value?.uid,
                        storage
                    )
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
                globalChatViewModel.currentUser.collect { user ->
                    if (user == null) {
                        findNavController().navigate(GlobalChatFragmentDirections.globalChatToLogin())
                    }
                }
            }
        }
    }
}