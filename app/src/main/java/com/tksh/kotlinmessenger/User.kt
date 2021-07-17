package com.tksh.kotlinmessenger

class User(val uid :String,val username:String, val profileImageUrl:String,val email:String){
    constructor():this("","","","")
}