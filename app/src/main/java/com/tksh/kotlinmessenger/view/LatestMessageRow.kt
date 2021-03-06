package com.tksh.kotlinmessenger.view

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.messages.LatestMessagesActivity
import com.tksh.kotlinmessenger.models.ChatMessage
import com.tksh.kotlinmessenger.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*


class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartnerUser:User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest.text = chatMessage.text
        val chatPartnerId = if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatMessage.toId
        } else chatMessage.fromId
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val view = viewHolder.itemView
                chatPartnerUser = snapshot.getValue(User::class.java)?:return
                viewHolder.itemView.username_textview_latest.text = chatPartnerUser?.username
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.imageview_latest_message)



                if (LatestMessagesActivity.newMessageNotif == 1){
                    view.new_message_popup.visibility = View.VISIBLE
                    view.username_textview_latest.setTypeface(null, Typeface.BOLD)
                    view.message_textview_latest.setTypeface(null,Typeface.BOLD)
                } else {
                    view.new_message_popup.visibility = View.GONE
                    view.username_textview_latest.setTypeface(null, Typeface.NORMAL)
                    view.message_textview_latest.setTypeface(null,Typeface.NORMAL)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }


}