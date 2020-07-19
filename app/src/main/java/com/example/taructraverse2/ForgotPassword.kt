package com.example.taructraverse2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ForgotPassword : AppCompatActivity() {

    private lateinit var genTempPass:Button
    private lateinit var email:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        genTempPass = findViewById(R.id.genTempPassBtn)
        email = findViewById(R.id.emailTxt)

        if(email.text.trim().toString().isEmpty()){
            Toast.makeText(this,"Please enter Email", Toast.LENGTH_SHORT).show()
        }else{
            genTempPass.setOnClickListener {
                WolfRequest(Constants.URL_FORGOT_PASSWORD,{
                    Toast.makeText(this,it.getString("message"), Toast.LENGTH_SHORT).show()
                    if(!it.getBoolean("error")){
                        finish()
                    }
                },{
                    Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                }).post("email" to email.text.trim().toString())
            }
        }

    }
}
