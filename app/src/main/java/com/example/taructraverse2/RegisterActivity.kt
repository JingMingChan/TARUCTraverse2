package com.example.taructraverse2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernametxt:EditText
    private lateinit var passwordtxt:EditText
    private lateinit var passwordtxt2:EditText
    private lateinit var emailtxt:EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var createUserBtn:Button
    private lateinit var saveEditBtn:Button
    private lateinit var txtUID:TextView
    private lateinit var chkBoxUsername:CheckBox
    private lateinit var chkBoxPass:CheckBox
    private lateinit var chkBoxEmail:CheckBox
    private var UID :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        WolfRequest.init(this)

        val extras = this.intent.extras
        UID = extras?.getString("UID")
        usernametxt = findViewById(R.id.usernametxt)
        passwordtxt = findViewById(R.id.passwordtxt)
        passwordtxt2 = findViewById(R.id.passwordtxt2)
        emailtxt = findViewById(R.id.emailtxt)
        typeSpinner = findViewById(R.id.typeSpinner)
        createUserBtn = findViewById(R.id.addUserBtn)



        if(!UID.isNullOrEmpty()){

            saveEditBtn=findViewById(R.id.saveBtn)
            saveEditBtn.visibility=View.VISIBLE

            chkBoxUsername=findViewById(R.id.checkBoxUserName)
            chkBoxPass=findViewById(R.id.checkBoxPassword)
            chkBoxEmail=findViewById(R.id.checkBoxEmail)
            chkBoxUsername.visibility = View.VISIBLE
            chkBoxPass.visibility = View.VISIBLE
            chkBoxEmail.visibility = View.VISIBLE
            showProfile()
            saveEditBtn.setOnClickListener {

                if(!chkBoxUsername.isChecked && !chkBoxPass.isChecked && !chkBoxEmail.isChecked){
                    Toast.makeText(this,"Please check at least one field to update",Toast.LENGTH_SHORT).show()
                }else{
                    var message:String =""
                    if(chkBoxUsername.isChecked){
                        if(usernametxt.text.trim().toString().isNullOrEmpty()){
                            message +="Username checked: Field cannot be left empty \n"
                        }else{
                            WolfRequest(Constants.URL_UPDATE_USER,{
                                message +=it.getString("message")+"\n"
                            },{
                                message +=it+"\n"
                            }).post("UID" to UID, "username" to usernametxt.text.toString().trim())
                        }
                    }

                    if(chkBoxEmail.isChecked){
                        if(emailtxt.text.trim().toString().isNullOrEmpty()){
                            message +="Email checked: Field cannot be left empty \n"
                        }else{
                            WolfRequest(Constants.URL_UPDATE_USER,{
                                message +=it.getString("message")+"\n"
                            },{
                                message +=it+"\n"
                            }).post("UID" to UID, "email" to emailtxt.text.toString().trim())
                        }
                    }

                    if(chkBoxPass.isChecked){
                        if(passwordtxt.text.trim().toString().isNullOrEmpty() || passwordtxt2.text.trim().toString().isNullOrEmpty()){
                            message +="Password checked: Field cannot be left empty \n"
                        }else{

                            if(passwordtxt.text.trim() == passwordtxt2.text.trim()){
                                WolfRequest(Constants.URL_UPDATE_USER,{
                                    message +=it.getString("message")+"\n"
                                },{
                                    message +=it+"\n"
                                }).post("UID" to UID, "password" to passwordtxt.text.toString().trim())
                            }else{
                                message +="Confirmation password Failed: Please checked Password \n"
                            }
                        }
                    }

                    if (message != ""){
                        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Update Successful",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }else{

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

    }

    private fun register(username :String, password:String, email:String, type:String){
        WolfRequest(Constants.URL_REGISTER,{
            Toast.makeText(this,it.getString("message"),Toast.LENGTH_SHORT).show()
            if(!it.getBoolean("error")){
                finish()//user success register
            }
        },{
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }).post("username" to username, "password" to password, "type" to type, "email" to email)
    }

    private fun showProfile(){
        txtUID = findViewById(R.id.uid)
        WolfRequest(Constants.URL_RETRIEVE_USER,{
            Toast.makeText(this,it.getString("message"), Toast.LENGTH_SHORT).show()
            if(!it.getBoolean("error")){
                txtUID.visibility=View.VISIBLE
                txtUID.text = it.getString("id")
                usernametxt.setText(it.getString("username"))
                emailtxt.setText(it.getString("email"))
                if(it.getString("type") =="Staff"){
                    typeSpinner.setSelection(1)
                }else{
                    typeSpinner.setSelection(0)
                }
                typeSpinner.isEnabled = false
                typeSpinner.isClickable = false
                createUserBtn.visibility = View.GONE
            }
        },{
            Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
        }).post("UID" to UID)
    }
}
