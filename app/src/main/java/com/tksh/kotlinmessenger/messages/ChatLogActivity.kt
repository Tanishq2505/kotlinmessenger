package com.tksh.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.models.User
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) ?: return
        supportActionBar?.title = user?.username
        val adapter = GroupieAdapter()
        adapter.add(ChatFromItem(user))
        adapter.add(ChatToItem(user))
        adapter.add(ChatFromItem(user))
        adapter.add(ChatToItem(user))
        adapter.add(ChatFromItem(user))
        adapter.add(ChatToItem(user))
        adapter.add(ChatFromItem(user))
        adapter.add(ChatToItem(user))
        recyclerView_chatlog.adapter = adapter
    }
}
class ChatFromItem(private val user:User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_from_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem(private val user: User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_to_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}