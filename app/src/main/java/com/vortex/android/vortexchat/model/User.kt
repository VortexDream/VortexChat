package com.vortex.android.vortexchat.model

data class User(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var lastSeenTimestamp: Any? = null,
    var photoUrl: String? = null
) {
}