package com.vortex.android.vortexchat.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.vortex.android.vortexchat.GlideApp
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.databinding.ListItemDialogLeftBinding
import com.vortex.android.vortexchat.databinding.ListItemDialogRightBinding
import com.vortex.android.vortexchat.model.Message
import com.vortex.android.vortexchat.util.getDateFromMilliseconds
import com.vortex.android.vortexchat.util.getTimeFromMilliseconds

const val LEFT_ITEM = 0
const val RIGHT_ITEM = 1

class DialogHolder(
    private val binding: ViewBinding,
    private val context: Context,
    private val storage: FirebaseStorage
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message, messages: List<Message>, position: Int) {

        val profilePicRef = storage.reference.child("ProfilePics/${message.senderUserId}")

        when(binding) {
            is ListItemDialogLeftBinding -> {
                binding.chatEntry.messageText.text = message.text
                binding.chatEntry.date.text = getTimeFromMilliseconds(
                    message.timestamp.toString().toLong(),
                    context
                )
                //Цвет фона сообщения
                binding.chatEntry.container.background =
                    AppCompatResources.getDrawable(context, R.drawable.view_chat_message_background_left)
                //Логика отображение делиметров даты
                if (position == 0) {
                    binding.dateDelimeter.text = getDateFromMilliseconds(
                        message.timestamp.toString().toLong(),
                        context
                    )
                    binding.dateDelimeter.visibility = View.VISIBLE
                    //Проверка на то, что у соседних сообщений отличаются даты
                } else if (getDateFromMilliseconds(message.timestamp.toString().toLong(), context) !=
                    getDateFromMilliseconds(messages[position - 1].timestamp.toString().toLong(), context)
                ) {

                    binding.dateDelimeter.text = getDateFromMilliseconds(
                        message.timestamp.toString().toLong(),
                        context
                    )
                    binding.dateDelimeter.visibility = View.VISIBLE

                }
                //Логика отображения фото профиля
                if (position == 0) {
                    GlideApp.with(context)
                        .load(profilePicRef)
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.profilePhoto)
                } else if (messages[position].senderUserId != messages [position - 1].senderUserId) {
                    GlideApp.with(context)
                        .load(profilePicRef)
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.profilePhoto)
                } else {
                    binding.profilePhoto.visibility = View.INVISIBLE
                }
            }
            is ListItemDialogRightBinding -> {
                binding.chatEntry.messageText.text = message.text
                binding.chatEntry.date.text = getTimeFromMilliseconds(
                    message.timestamp.toString().toLong(),
                    context
                )
                //Цвет фона сообщения
                binding.chatEntry.container.background =
                    AppCompatResources.getDrawable(context, R.drawable.view_chat_message_background_right)
                //Логика отображение делиметров даты
                if (position == 0) {
                    binding.dateDelimeter.text = getDateFromMilliseconds(
                        message.timestamp.toString().toLong(),
                        context
                    )
                    binding.dateDelimeter.visibility = View.VISIBLE
                    //Проверка на то, что у соседних сообщений отличаются даты
                } else if (getDateFromMilliseconds(message.timestamp.toString().toLong(), context) !=
                    getDateFromMilliseconds(messages[position - 1].timestamp.toString().toLong(), context)
                ) {

                    binding.dateDelimeter.text = getDateFromMilliseconds(
                        message.timestamp.toString().toLong(),
                        context
                    )
                    binding.dateDelimeter.visibility = View.VISIBLE

                }
            }
        }
    }
}

class DialogListAdapter(
    private val messages: List<Message>,
    private val context: Context,
    private val currentUserId: String?,
    private val storage: FirebaseStorage
) : RecyclerView.Adapter<DialogHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewBinding = when (viewType) {
            LEFT_ITEM -> ListItemDialogLeftBinding.inflate(inflater, parent, false)
            RIGHT_ITEM -> ListItemDialogRightBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return DialogHolder(binding, context, storage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderUserId == currentUserId) RIGHT_ITEM else LEFT_ITEM
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: DialogHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, messages, position)
    }
}