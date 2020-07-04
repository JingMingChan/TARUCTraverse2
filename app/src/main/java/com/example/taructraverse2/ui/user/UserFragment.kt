package com.example.taructraverse2.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.taructraverse2.MainActivity
import com.example.taructraverse2.R


class UserFragment : Fragment() {

    private lateinit var userModel: UserModel
    private lateinit var uid:TextView

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

        uid = root.findViewById(R.id.txtUID)
        uid.text = (activity as MainActivity?)?.test()
        return root
    }
}
