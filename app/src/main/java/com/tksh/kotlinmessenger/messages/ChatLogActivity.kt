package com.tksh.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.models.ChatMessage
import com.tksh.kotlinmessenger.models.User
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_log.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {
    companion object{
        const val TAG = "Chat Log"
    }
    private val adapter = GroupieAdapter()
    private var toUser : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerView_chatlog.adapter = adapter
        recyclerView_chatlog.scrollToPosition(adapter.itemCount -1)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) ?: return
        supportActionBar?.title = toUser?.username
//        setupDummyData(user)
        listenForMessages()
        sendtext_button_chat_log.setOnClickListener{
            Log.d(TAG,"Message Sent")
            performSendMessage()

        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val currentUser = LatestMessagesActivity.currentUser?:return
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
             if (chatMessage!= null){
                 if(chatMessage.fromId==FirebaseAuth.getInstance().uid) {
                     adapter.add(ChatFromItem(currentUser, chatMessage))
                 }
                 else {
                     adapter.add(ChatToItem(chatMessage))
                 }
            }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    private fun performSendMessage() {
        val message = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid?:return
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) ?: return
        val toId = user.uid



        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!,message,fromId,toId,System.currentTimeMillis()/1000)
        val toChatMessage = ChatMessage(reference.key!!,message,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Message sent : ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerView_chatlog.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(toChatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Message sent : ${reference.key}")
            }
    }

//    private fun setupDummyData(user:User) {
//        val adapter = GroupieAdapter()
//        adapter.add(ChatFromItem(user))
//        adapter.add(ChatToItem(user))
//        adapter.add(ChatFromItem(user))
//        adapter.add(ChatToItem(user))
//        adapter.add(ChatFromItem(user))
//        adapter.add(ChatToItem(user))
//        adapter.add(ChatFromItem(user))
//        adapter.add(ChatToItem(user))
//        recyclerView_chatlog.adapter = adapter
//    }
}
class ChatFromItem(private val user:User,private val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from_chatLog.text = chatMessage.text
    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_from_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem( private val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_chatLog.text = chatMessage.text

        Picasso.get().load(LatestMessagesActivity.currentUser!!.profileImageUrl).into(viewHolder.itemView.image_to_chatLog)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}