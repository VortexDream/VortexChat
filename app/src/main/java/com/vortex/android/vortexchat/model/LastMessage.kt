package com.vortex.android.vortexchat.model

import com.google.firebase.database.ServerValue

data class LastMessage(
    val userId1: String,
    val userId2: String,
    val text: String,
    val timestamp: Any
) {
    constructor(userId1: String, userId2: String, text: String) : this(userId1, userId2, text, ServerValue.TIMESTAMP)
}
