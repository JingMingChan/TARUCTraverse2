package com.example.taructraverse2

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class AddUpdateMapActivity : AppCompatActivity() {

    private var blockID:String?= null
    private lateinit var blockNametxt:EditText
    private lateinit var blockIDSpn:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_map)

        blockNametxt=findViewById(R.id.blockNameEditText)
        blockIDSpn=findViewById(R.id.blockIDSpinner)
//check spanner hav value
//        blockIDSpn.setOnItemClickListener { parent, view, position, id ->
//
//        }


        WolfRequest(Constants.URL_GET_BLOCKID,{
            if(!it.getBoolean("error")){
                blockID = "Add New Map Location"
                blockID += it.getString("blockId")
                val separated: List<String> = blockID!!.split("||")
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, separated)
                blockIDSpn.adapter = arrayAdapter
            }
        },{
            Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
        }).post("1" to "1")
    }
}
