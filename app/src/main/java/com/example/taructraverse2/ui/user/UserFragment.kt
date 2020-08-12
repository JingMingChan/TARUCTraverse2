package com.example.taructraverse2.ui.user

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taructraverse2.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class UserFragment : Fragment() {


    private var UID :String? = null
    private lateinit var username:TextView
    private lateinit var email:TextView
    private lateinit var logout:Button
    private lateinit var addupdateMap:Button
    private lateinit var profileImg:ImageView
    private lateinit var editLogin:Button
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        context?.let { WolfRequest.init(it) }

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        username = root.findViewById(R.id.txtUserName)
        email = root.findViewById(R.id.txtEmail)
        logout = root.findViewById(R.id.btnLogOut)
        addupdateMap = root.findViewById(R.id.addUpdateMapBtn)
        profileImg=root.findViewById(R.id.profileView)
        editLogin=root.findViewById(R.id.editLoginBtn)

        UID = (activity as MainActivity?)?.getUID()

        email.movementMethod = ScrollingMovementMethod()
        loadImg()
        profileImg.setOnClickListener {
            pickFromGallery(1)
        }

        addupdateMap.setOnClickListener {
            val intent = Intent(context, AddUpdateMapActivity::class.java)
            startActivity(intent)
        }


        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        editLogin.setOnClickListener {
            val intent = Intent(context, ForgotPassword::class.java)
            intent.putExtra("EditType","EditLogin")
            startActivity(intent)
        }


        return root
    }

    private fun loadImg(){
        val profileStorageRef = storage.reference.child("User/"+UID+"/profile.jpg")

        val ONE_MEGABYTE = (1024 * 1024).toLong()

        profileStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profileImg.setImageBitmap(bmp)
        }.addOnFailureListener {
                profileImg.setImageResource(R.drawable.ic_message)
            }
    }

    private fun pickFromGallery(int: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, int)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null && data.getData() != null && resultCode == Activity.RESULT_OK){

            val dataUri = data.data as Uri

            val storageRef = storage.reference.child("User/"+UID)

            if(requestCode == 1){
                profileImg.setImageURI(dataUri)

                val profileRef = storageRef.child("profile.jpg")

                profileRef.putFile(dataUri).addOnSuccessListener {
                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(context, "Image Not Saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    fun getUserDetails(){
        WolfRequest(Constants.URL_RETRIEVE_USER,{
            Toast.makeText(context,it.getString("message"), Toast.LENGTH_SHORT).show()
            if(!it.getBoolean("error")){

                username.text = it.getString("username")
                email.text = it.getString("email")
                if(it.getString("type") =="Staff"){
                    addupdateMap.visibility = View.VISIBLE
                }
            }
        },{
            Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
        }).post("UID" to UID)
    }


}
