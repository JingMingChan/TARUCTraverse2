package com.example.taructraverse2.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.taructraverse2.*


class UserFragment : Fragment() {

    private lateinit var userModel: UserModel
    private var UID :String? = null
    private lateinit var uid:TextView
    private lateinit var username:TextView
    private lateinit var email:TextView
    private lateinit var logout:Button
    private lateinit var updateProfile:Button
    private lateinit var addupdateMap:Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userModel =
                ViewModelProviders.of(this).get(UserModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)

        userModel.text.observe(viewLifecycleOwner, Observer {

        })
        context?.let { WolfRequest.init(it) }

        uid = root.findViewById(R.id.txtUID)
        username = root.findViewById(R.id.txtUserName)
        email = root.findViewById(R.id.txtEmail)
        logout = root.findViewById(R.id.btnLogOut)
        addupdateMap = root.findViewById(R.id.addUpdateMapBtn)
        updateProfile = root.findViewById(R.id.updateProfileBtn)

        UID = (activity as MainActivity?)?.getUID()

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
