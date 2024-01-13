package com.vortex.android.vortexchat.firebase


import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import javax.inject.Inject

class OnlineStorage @Inject constructor(
    storage: FirebaseStorage,
    private val repository: BaseAuthRepository
) : BaseStorage {

    private val storageRef = storage.reference

    override suspend fun uploadProfilePic(uri: Uri, toastCallback: () -> Unit, imageViewCallback: () -> Unit) {

        val currentUserId = repository.getCurrentUser()!!.uid
        val profilePicRef = storageRef.child("ProfilePics/${currentUserId}")
        val uploadTask = profilePicRef.putFile(uri)

        uploadTask.addOnFailureListener {
            toastCallback()
        }.addOnSuccessListener {
            imageViewCallback()
        }
    }

}