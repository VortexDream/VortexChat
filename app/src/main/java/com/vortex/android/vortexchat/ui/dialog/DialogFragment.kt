package com.vortex.android.vortexchat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogFragment : Fragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val dialogViewModel: DialogViewModel by viewModels()
    private val TAG = "DialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).supportActionBar?.show()
        _binding = FragmentDialogBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}