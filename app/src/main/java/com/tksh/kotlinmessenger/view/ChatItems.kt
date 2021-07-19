package com.tksh.kotlinmessenger.view

import com.squareup.picasso.Picasso
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.messages.LatestMessagesActivity
import com.tksh.kotlinmessenger.models.ChatMessage
import com.tksh.kotlinmessenger.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem(val user: User,val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from_chatLog.text = chatMessage.text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_from_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem( private val chatMessage: ChatMessage, val user: User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_chatLog.text = chatMessage.text

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_to_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}