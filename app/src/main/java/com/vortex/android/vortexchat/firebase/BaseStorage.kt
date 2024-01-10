package com.vortex.android.vortexchat.firebase

import android.net.Uri

//Файлохранилище Firebase
interface BaseStorage {

    suspend fun uploadProfilePic(uri: Uri, toastCallback: () -> Unit, imageViewCallback: () -> Unit)

}