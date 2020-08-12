package com.example.taructraverse2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernametxt:EditText
    private lateinit var passwordtxt:EditText
    private lateinit var passwordtxt2:EditText
    private lateinit var emailtxt:EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var createUserBtn:Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        WolfRequest.init(this)
        auth = FirebaseAuth.getInstance()

        usernametxt = findViewById(R.id.usernametxt)
        passwordtxt = findViewById(R.id.passwordtxt)
        passwordtxt2 = findViewById(R.id.passwordtxt2)
        emailtxt = findViewById(R.id.emailtxt)
        typeSpinner = findViewById(R.id.typeSpinner)
        createUserBtn = findViewById(R.id.addUserBtn)

        createUserBtn.setOnClickListener {

            if(usernametxt.text.toString().isEmpty() || passwordtxt.text.toString().isEmpty() || passwordtxt2.text.toString().isEmpty() || emailtxt.text.toString().isEmpty()){
                Toast.makeText(this,"Field cannot be left empty",Toast.LENGTH_SHORT).show()
            }else{
                if(passwordtxt.text.trim() == passwordtxt2.text.trim()){
                   register(usernametxt.text.toString().trim(),passwordtxt.text.toString().trim(),emailtxt.text.toString().trim(),typeSpinner.selectedItem.toString().trim())
                }else{
                    Toast.makeText(this,"Confirmation password is not the same",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun register(username :String, password:String, email:String, type:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    if (user != null) {
                        user.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    WolfRequest(Constants.URL_REGISTER,{
                                        Toast.makeText(this,it.getString("message"),Toast.LENGTH_SHORT).show()
                                        if(!it.getBoolean("error")){
                                            auth.signOut()
                                            finish()//user success register
                                        }
                                    },{
                                        Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                                    }).post("fireID" to user.uid.toString(), "username" to username, "type" to type, "email" to email)

                                }else{
                                    Toast.makeText(baseContext, "Account Created, Failed to sent email verification.",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "Registration Error.",
                            Toast.LENGTH_SHORT).show()
                    }
                    auth.signOut()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Registration Error. Please check your email and Password",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}
