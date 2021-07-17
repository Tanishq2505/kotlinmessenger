package com.tksh.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun fetchUsers() {
        val database = FirebaseDatabase.getInstance().getReference("/users")
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupieAdapter()


                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user!=null) {
                        adapter.add(UserItem(user))
                    }
                }
                recyclerview_new_message.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
class UserItem(val user: User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val item = viewHolder.itemView
        item.username_new_message_text_view.text = user.username
        Picasso.get().load(user.profileImageUrl).into(item.image_new_message_image_view)
    }

    override fun getLayout(): Int  = R.layout.user_row_new_message

}