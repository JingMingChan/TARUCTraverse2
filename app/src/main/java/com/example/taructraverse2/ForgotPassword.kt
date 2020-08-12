package com.example.taructraverse2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var genTempPass:Button
    private lateinit var changeLogin:Button
    private lateinit var email:EditText
    private lateinit var pass1:EditText
    private lateinit var pass2:EditText
    private lateinit var auth: FirebaseAuth
    private var editType :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        WolfRequest.init(this)
        auth = FirebaseAuth.getInstance()
        genTempPass = findViewById(R.id.genTempPassBtn)
        changeLogin = findViewById(R.id.changeAuthBtn)
        email = findViewById(R.id.emailTxt)
        pass1 = findViewById(R.id.editPass1)
        pass2 = findViewById(R.id.editPass2)
        val extras = this.intent.extras
        editType = extras?.getString("EditType")

        if(editType != null){
            if(editType=="EditLogin"){
                genTempPass.visibility= View.GONE
                pass1.visibility= View.VISIBLE
                pass2.visibility= View.VISIBLE
                changeLogin.visibility= View.VISIBLE

                changeLogin.setOnClickListener {
                    if(email.text.trim().toString().isEmpty() || pass1.text.trim().toString().isEmpty() ||pass2.text.trim().toString().isEmpty() ||  pass1.text.trim().length < 6){
                        Toast.makeText(this,"Enter Value Error, Please enter New Login Details", Toast.LENGTH_SHORT).show()
                    }else{

                        if(pass1.text.trim().toString() == pass2.text.trim().toString()){

                            val user = auth.currentUser
                            user!!.updateEmail(email.text.trim().toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        user.updatePassword(pass1.text.trim().toString())
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val credential = EmailAuthProvider
                                                        .getCredential(email.text.trim().toString(), pass1.text.trim().toString())


                                                    user.reauthenticate(credential)
                                                        .addOnCompleteListener {

                                                            user.sendEmailVerification()
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {

                                                                        WolfRequest(Constants.URL_UPDATE_USER,{

                                                                            if(!it.getBoolean("error")){
                                                                                Toast.makeText(baseContext, "Edit Login Details Success. Please Logout to verify your new email",Toast.LENGTH_SHORT).show()
                                                                                finish()//user success register
                                                                            }else{
                                                                                Toast.makeText(this,it.getString("message"),Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        },{
                                                                            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                                                                        }).post("FireID" to user.uid.toString(), "email" to email.text.trim().toString())
                                                                    }else{
                                                                        Toast.makeText(baseContext, "Login Updated, Failed to sent email verification.",
                                                                            Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                        }

                                                }
                                            }
                                    }else{
                                        Toast.makeText(baseContext, "update Failed",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }

                        }else{
                            Toast.makeText(this,"Confirmation password is not the same",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{

            }
        }else{
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
}
