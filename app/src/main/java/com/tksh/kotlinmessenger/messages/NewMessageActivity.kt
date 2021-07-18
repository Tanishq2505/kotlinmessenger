package com.tksh.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.models.User
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    private val adapter = GroupieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_new_message)

        fetchUsers()
        swipeRefresh.setOnRefreshListener {
            recyclerview_new_message.visibility = View.GONE
            shimmer_layout_new_messages.visibility = View.VISIBLE
            shimmer_layout_new_messages.startShimmer()
            Handler(Looper.myLooper()!!).postDelayed({
                recyclerview_new_message.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
                shimmer_layout_new_messages.stopShimmer()
                shimmer_layout_new_messages.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            },1000)

        }
    }
    companion object{
        val USER_KEY = "USER_NAME_KEY"
    }

    private fun fetchUsers() {
        val database = FirebaseDatabase.getInstance().getReference("/users")
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val shimmerFrame = findViewById<ShimmerFrameLayout>(R.id.shimmer_layout_new_messages)

                p0.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user!=null) {
                        shimmerFrame.stopShimmer()
                        shimmerFrame.visibility = View.GONE
                        adapter.add(UserItem(user))
                    }
                }
                recyclerview_new_message.adapter = adapter
                adapter.setOnItemClickListener { item, view ->
                    val userData = item as UserItem
                    val intent = Intent(this@NewMessageActivity,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userData.user)
                    startActivity(intent)
                    finish()
                }
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