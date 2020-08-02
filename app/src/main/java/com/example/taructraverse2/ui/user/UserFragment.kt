package com.example.taructraverse2.ui.user

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.taructraverse2.*
import com.google.firebase.storage.FirebaseStorage


class UserFragment : Fragment() {


    private var UID :String? = null
    private lateinit var uid:TextView
    private lateinit var username:TextView
    private lateinit var email:TextView
    private lateinit var logout:Button
    private lateinit var updateProfile:Button
    private lateinit var addupdateMap:Button
    private lateinit var profileImg:ImageView

    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        context?.let { WolfRequest.init(it) }

        storage = FirebaseStorage.getInstance()

        uid = root.findViewById(R.id.txtUID)
        username = root.findViewById(R.id.txtUserName)
        email = root.findViewById(R.id.txtEmail)
        logout = root.findViewById(R.id.btnLogOut)
        addupdateMap = root.findViewById(R.id.addUpdateMapBtn)
        updateProfile = root.findViewById(R.id.updateProfileBtn)
        profileImg=root.findViewById(R.id.profileView)

        UID = (activity as MainActivity?)?.getUID()

        loadImg()
        profileImg.setOnClickListener {
            pickFromGallery(1)
        }

        addupdateMap.setOnClickListener {
            val intent = Intent(context, AddUpdateMapActivity::class.java)
            startActivity(intent)
        }


        logout.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        updateProfile.setOnClickListener {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra("UID",UID)
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
        getUserDetails()
        super.onResume()

    }

    private fun getUserDetails(){
        WolfRequest(Constants.URL_RETRIEVE_USER,{
            if(!it.getBoolean("error")){

                uid.text = it.getString("id")
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
