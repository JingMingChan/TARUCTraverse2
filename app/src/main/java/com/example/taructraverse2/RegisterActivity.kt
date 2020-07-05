package com.example.taructraverse2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernametxt:EditText
    private lateinit var passwordtxt:EditText
    private lateinit var emailtxt:EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var createUserBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        WolfRequest.init(this)

        usernametxt = findViewById(R.id.usernametxt)
        passwordtxt = findViewById(R.id.passwordtxt)
        emailtxt = findViewById(R.id.emailtxt)
        typeSpinner = findViewById(R.id.typeSpinner)

        createUserBtn = findViewById(R.id.addUserBtn)

        createUserBtn.setOnClickListener {

            if(usernametxt.text.toString().isEmpty() || passwordtxt.text.toString().isEmpty() || emailtxt.text.toString().isEmpty()){
                Toast.makeText(this,"Field cannot be left empty",Toast.LENGTH_SHORT).show()
            }else{
                register()
            }

        }
    }

    private fun register(){
        val username = usernametxt.text.toString().trim()
        val password = passwordtxt.text.toString().trim()
        val email = emailtxt.text.toString().trim()
        val type = typeSpinner.selectedItem.toString().trim()


        WolfRequest(Constants.URL_REGISTER,{
            Toast.makeText(this,it.getString("message"),Toast.LENGTH_SHORT).show()
            if(!it.getBoolean("error")){
                finish()//user success register
            }
        },{
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }).post("username" to username, "password" to password, "type" to type, "email" to email)

//        val jsonBody = JSONObject()
//        jsonBody.put("username",username)
//        jsonBody.put("password",password)
//        jsonBody.put("type",type)
//        jsonBody.put("email",email)

    }
}
