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
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager


class LoginActivity : AppCompatActivity(), PermissionsListener {

    private lateinit var loginBtn:Button
    private lateinit var registerBtn:Button
    private lateinit var userEmail:EditText
    private lateinit var pass:EditText
    private lateinit var forgotPass:TextView
    private lateinit var auth: FirebaseAuth

    private lateinit var permissionManager: PermissionsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registBtn)
        userEmail = findViewById(R.id.useremail)
        pass = findViewById(R.id.password)
        forgotPass = findViewById(R.id.forgotPassTxt)

        userEmail.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        loginBtn.setOnClickListener(){
            if(userEmail.text.trim().toString().isEmpty() || pass.text.trim().toString().isEmpty()){
                Toast.makeText(this,"Please enter Email and password", Toast.LENGTH_SHORT).show()
            }else{
                login(userEmail.text.trim().toString(),pass.text.trim().toString())
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
        checkPermission()
    }


    fun login(email :String, password:String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if(user.isEmailVerified){
                            Toast.makeText(baseContext, "Login Successful",
                                Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("UID",user.uid.toString())//fireID
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(baseContext, "Please Verify your email.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(baseContext, "Login Failed, Check your email or password.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun checkPermission(){
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }


    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            finish()
        }
    }
}
