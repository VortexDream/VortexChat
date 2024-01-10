package com.vortex.android.vortexchat.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vortex.android.vortexchat.GlideApp
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.activities.MainActivity
import com.vortex.android.vortexchat.databinding.FragmentProfileBinding
import com.vortex.android.vortexchat.firebase.OnlineStorage
import com.vortex.android.vortexchat.repository.GlideImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var storage: OnlineStorage

    //Контракт на выбор картинки для профиля
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            profileViewModel.uploadProfilePic(
                it,
                { Toast.makeText(
                    requireContext(),
                    getString(R.string.profile_pic_upload_fail),
                    Toast.LENGTH_SHORT
                ).show() },
                { updatePhoto() }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).showBottomNavigation()
        (activity as MainActivity).supportActionBar?.show()
        _binding = FragmentProfileBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getCurrentUser()
        registerObservers()
        updatePhoto()
        binding.apply {
            signOutButton.setOnClickListener {
                profileViewModel.signOut()
            }
            profilePhoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            username.text = profileViewModel.currentUser.value?.displayName
            email.text = profileViewModel.currentUser.value?.email
        }

    }

    private fun updatePhoto() {
        GlideApp.with(requireContext())
            .load(storage.profilePicRef)
            .listener(GlideImpl.OnCompleted {
                binding.profileFragment.contentDescription = getString(R.string.profile_photo_image_description)
                binding.profilePhotoCamera.visibility = View.GONE
            })
            .circleCrop()
            .placeholder(R.drawable.ic_avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.profilePhoto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Переход в другую активность при успешной регистрации
    private fun registerObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.currentUser.collect { user ->
                    if (user == null) {
                        findNavController().navigate(ProfileFragmentDirections.profileToLogin())
                    }
                }
            }
        }
    }
}