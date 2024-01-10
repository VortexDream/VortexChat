package com.vortex.android.vortexchat.firebase


import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import javax.inject.Inject

class OnlineStorage @Inject constructor(
    private val storage: FirebaseStorage,
    private val repository: BaseAuthRepository
) : BaseStorage {

    private val currentUserId = repository.getCurrentUser()!!.uid
    val profilePicRef = storage.reference.child("ProfilePics/${currentUserId}")

    override suspend fun uploadProfilePic(uri: Uri, toastCallback: () -> Unit, imageViewCallback: () -> Unit) {

        val uploadTask = profilePicRef.putFile(uri)

        uploadTask.addOnFailureListener {
            toastCallback()
        }.addOnSuccessListener {
            imageViewCallback()
        }
    }

}