package com.example.taructraverse2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn:Button
    private lateinit var registerBtn:Button
    private lateinit var userName:EditText
    private lateinit var pass:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        WolfRequest.init(this)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registBtn)
        userName = findViewById(R.id.username)
        pass = findViewById(R.id.password)

        loginBtn.setOnClickListener(){
            if(userName.text.trim().toString().isEmpty() || pass.text.trim().toString().isEmpty()){
                Toast.makeText(this,"Please enter Username and password", Toast.LENGTH_SHORT).show()
            }else{
                login()
            }
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    fun login(){
        val username = userName.text.trim().toString()
        val password = pass.text.trim().toString()

        WolfRequest(Constants.URL_LOGIN,{
            Toast.makeText(this,it.getString("message"),Toast.LENGTH_SHORT).show()
            if(!it.getBoolean("error")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("UID",it.getInt("id"))
                startActivity(intent)
                finish()
            }
        },{
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }).post("username" to username, "password" to password)
    }
}
