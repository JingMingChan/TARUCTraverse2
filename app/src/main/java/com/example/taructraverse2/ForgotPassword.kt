package com.example.taructraverse2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var genTempPass:Button
    private lateinit var email:EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        genTempPass = findViewById(R.id.genTempPassBtn)
        email = findViewById(R.id.emailTxt)


        genTempPass.setOnClickListener {
            if(email.text.trim().toString().isEmpty()){
                Toast.makeText(this,"Please enter Email", Toast.LENGTH_SHORT).show()
            }else{

                auth.sendPasswordResetEmail(email.text.trim().toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Check Email", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this,"Email not yet register.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
