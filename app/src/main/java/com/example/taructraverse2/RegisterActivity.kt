package com.example.taructraverse2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class RegisterActivity : AppCompatActivity() {

    private lateinit var username:EditText
    private lateinit var password:EditText
    private lateinit var email:EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var createUserBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.usernametxt)
        password = findViewById(R.id.passwordtxt)
        email = findViewById(R.id.emailtxt)

        typeSpinner = findViewById(R.id.typeSpinner)

        createUserBtn = findViewById(R.id.addUserBtn)
        
        createUserBtn.setOnClickListener {
            username.setText(typeSpinner.selectedItem.toString())
        }
    }
}
