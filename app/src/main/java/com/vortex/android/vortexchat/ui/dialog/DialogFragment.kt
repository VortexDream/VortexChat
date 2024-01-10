package com.vortex.android.vortexchat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DialogFragment : Fragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val dialogViewModel: DialogViewModel by viewModels()
    private val TAG = "DialogFragment"
    private val args: DialogFragmentArgs by navArgs()
    private lateinit var otherUserId: String

    @Inject
    lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).supportActionBar?.apply {
            show()
            title = args.displayName
        }
        _binding = FragmentDialogBinding.inflate(layoutInflater, container,false)
        binding.dialogRecyclerView.layoutManager = LinearLayoutManager(context)
            .apply { stackFromEnd = true }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otherUserId = args.userId
        dialogViewModel.subscribeToDialogMessages(otherUserId)
        dialogViewModel.getCurrentUser()
        binding.apply {
            sendButton.setOnClickListener {
                val messageField = binding.messageTextField.editText
                if (messageField!!.text.toString() != "") {
                    dialogViewModel.sendMessage(messageField.text.toString(), otherUserId)
                    messageField.text.clear()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dialogViewModel.dialogMessageList.collect { messages ->
                    binding.dialogRecyclerView.adapter = DialogListAdapter(
                        messages,
                        requireContext(),
                        dialogViewModel.currentUser.value?.uid,
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
}
