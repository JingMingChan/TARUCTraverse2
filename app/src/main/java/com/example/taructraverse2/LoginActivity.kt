package com.example.taructraverse2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn:Button
    private lateinit var registerBtn:Button
    private lateinit var userName:EditText
    private lateinit var pass:EditText
    private lateinit var forgotPass:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        WolfRequest.init(this)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registBtn)
        userName = findViewById(R.id.username)
        pass = findViewById(R.id.password)
        forgotPass = findViewById(R.id.forgotPassTxt)


        userName.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        loginBtn.setOnClickListener(){
            if(userName.text.trim().toString().isEmpty() || pass.text.trim().toString().isEmpty()){
                Toast.makeText(this,"Please enter Username and password", Toast.LENGTH_SHORT).show()
            }else{
                login(userName.text.trim().toString(),pass.text.trim().toString())
            }
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }


    fun login(username :String, password:String){
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
