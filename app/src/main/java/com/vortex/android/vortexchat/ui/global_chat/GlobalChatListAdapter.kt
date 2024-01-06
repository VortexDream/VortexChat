package com.vortex.android.vortexchat.ui.global_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vortex.android.vortexchat.databinding.ListItemDialogRightBinding
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.util.getTimeFromMilliseconds

class GlobalChatHolder(
    private val binding: ListItemDialogRightBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.chatEntry.messageText.text = message.text
        binding.chatEntry.date.text = getTimeFromMilliseconds(message.timestamp.toString().toLong(), context)
    }
}

class GlobalChatListAdapter(
    private val messages: List<Message>,
    private val context: Context,
) : RecyclerView.Adapter<GlobalChatHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDialogRightBinding.inflate(inflater, parent, false)
        return GlobalChatHolder(binding, context)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: GlobalChatHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }
}

