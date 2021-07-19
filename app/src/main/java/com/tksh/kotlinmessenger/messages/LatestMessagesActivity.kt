package com.tksh.kotlinmessenger.messages

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.models.ChatMessage
import com.tksh.kotlinmessenger.models.User
import com.tksh.kotlinmessenger.registerLogin.RegisterActivity
import com.tksh.kotlinmessenger.view.LatestMessageRow
import com.xwray.groupie.GroupieAdapter
import kotlinx.android.synthetic.main.activity_latest_messages.*


class LatestMessagesActivity : AppCompatActivity() {
    companion object {
        var newMessageNotif:Int? = null
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("New_Message", Activity.MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        val editor = sharedPreferences.edit()
        val newMessage= sharedPreferences.getInt("newMessageNotif",0)
        newMessageNotif = newMessage


        loginCheck()
        fetchCurrentUser()
        listenForLatestMessages()
        adapter.setOnItemClickListener { item, view ->
            editor.putInt("newMessageNotif",0)
            editor.apply()
            newMessageNotif = newMessage
            val row = item as LatestMessageRow
            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
        Log.d("NewMessageNotif","shared = $newMessage , $newMessageNotif")
    }
    val latestMessagesMap = HashMap<String, ChatMessage> ()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()

        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {


                val chatMessage = snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[snapshot.key!!] = chatMessage
                val sharedPreferences = getSharedPreferences("New_Message", Activity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val newMessage= sharedPreferences.getInt("newMessageNotif",0)
                editor.putInt("newMessageNotif",1)
                editor.apply()
                newMessageNotif = newMessage
                refreshRecyclerViewMessages()
                Log.d("NewMessageNotif","shared = $newMessage , $newMessageNotif")


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


    private val adapter = GroupieAdapter()



    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loginCheck() {

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

            }
            R.id.menu_new_message -> {
                val intent  = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }
}