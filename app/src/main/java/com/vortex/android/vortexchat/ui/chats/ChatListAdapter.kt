package com.vortex.android.vortexchat.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.GlideApp
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.databinding.ListItemChatBinding
import com.vortex.android.vortexchat.model.LastMessage
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.model.User
import com.vortex.android.vortexchat.util.getDateFromMilliseconds
import com.vortex.android.vortexchat.util.getTimeFromMilliseconds
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

class ChatHolder(
    private val binding: ListItemChatBinding,
    private val context: Context,
    private val storage: FirebaseStorage
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, currentUserId: String, lastMessages: List<LastMessage>, onChatClicked: (userId: String, displayName: String) -> Unit) {

        val profilePicRef = storage.reference.child("ProfilePics/${user.userId}")
        var lastMessage: LastMessage? = null

        //Выбор последнего сообщения
        run breaking@ {
            for (message in lastMessages) {
                if (((message.userId1 == currentUserId)
                    && (message.userId2 == user.userId))
                    || ((message.userId1 == user.userId)
                        && (message.userId2 == currentUserId))) {

                    lastMessage = message
                    return@breaking

                }
            }
        }

        val todayTime = Calendar.getInstance().timeInMillis
        if (lastMessage != null) {
            binding.lastMessage.text = lastMessage!!.text
            binding.date.text = if (getDateFromMilliseconds(lastMessage!!.timestamp.toString().toLong(), context)
                == getDateFromMilliseconds(todayTime, context)) {
                getTimeFromMilliseconds(lastMessage!!.timestamp.toString().toLong(), context)
            } else {
                getDateFromMilliseconds(lastMessage!!.timestamp.toString().toLong(), context)
            }

        } else {
            binding.lastMessage.text = context.getString(R.string.last_message_placeholder)
            binding.date.visibility = View.GONE
        }

        binding.username.text = user.username
        binding.root.setOnClickListener {
            onChatClicked(user.userId!!, user.username!!)
        }
        GlideApp.with(context)
            .load(profilePicRef)
            .circleCrop()
            .placeholder(R.drawable.ic_avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.profilePhoto)
    }
}

class ChatListAdapter(
    private val users: List<User>,
    private val lastMessages: List<LastMessage>,
    private val currentUserId: String,
    private val context: Context,
    private val storage: FirebaseStorage,
    private val onChatClicked: (userId: String, displayName: String) -> Unit
) : RecyclerView.Adapter<ChatHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatBinding.inflate(inflater, parent, false)
        return ChatHolder(binding, context, storage)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val user = users[position]
        val lastMessage = lastMessages
        holder.bind(user, currentUserId, lastMessages, onChatClicked)
    }
}