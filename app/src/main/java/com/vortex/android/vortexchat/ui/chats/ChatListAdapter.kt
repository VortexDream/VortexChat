package com.vortex.android.vortexchat.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.databinding.ListItemChatBinding
import com.vortex.android.vortexchat.model.User
import java.util.UUID

class ChatHolder(
    private val binding: ListItemChatBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User, onChatClicked: (userId: String?) -> Unit) {

        binding.username.text = user.username
        /*binding.root.contentDescription = if (crime.isSolved) {
            context.getString(R.string.crime_item_description, crime.title)
        } else {
            context.getString(R.string.crime_item_description_not_solved, crime.title)
        }*/

        binding.root.setOnClickListener {
            onChatClicked(user.userId)
        }

        /*binding.crimeSolved.visibility = if (crime.isSolved) {
            View.VISIBLE
        } else {
            View.GONE
        }*/
    }
}

class ChatListAdapter(
    private val users: List<User>,
    private val context: Context,
    private val onChatClicked: (userId: String?) -> Unit
) : RecyclerView.Adapter<ChatHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatBinding.inflate(inflater, parent, false)
        return ChatHolder(binding, context)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val user = users[position]
        holder.bind(user, onChatClicked)
    }
}