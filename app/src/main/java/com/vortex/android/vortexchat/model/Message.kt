package com.vortex.android.vortexchat.model

import com.google.firebase.database.ServerValue

data class Message(
    val senderUserId: String,
    val text: String,
    val timestamp: Any
) {
    constructor(senderUserId: String, text: String) : this(senderUserId, text, ServerValue.TIMESTAMP)
}
