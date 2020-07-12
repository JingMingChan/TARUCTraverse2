package com.example.taructraverse2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class AddUpdateMapActivity : AppCompatActivity() {

    private var blockID:String?= null
    private lateinit var blockIDTxt:EditText
    private lateinit var blockNameTxt:EditText
    private lateinit var addressTxt:EditText
    private lateinit var latTxt:EditText
    private lateinit var longTxt:EditText
    private lateinit var blockIDSpn:Spinner
    private lateinit var insertLatLong:Button
    private lateinit var addLocation:Button
    private lateinit var editLocation:Button

    private var arrayAdapter:ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_map)

        blockIDTxt=findViewById(R.id.BIDEditText)
        blockNameTxt=findViewById(R.id.blockNameEditText)
        addressTxt=findViewById(R.id.addrresEditText)
        latTxt=findViewById(R.id.latEditText)
        longTxt=findViewById(R.id.longEditText)
        blockIDSpn=findViewById(R.id.blockIDSpinner)
        insertLatLong=findViewById(R.id.insertLatLongBtn)
        addLocation=findViewById(R.id.addLocationBtn)
        editLocation=findViewById(R.id.editLocationBtn)


        WolfRequest(Constants.URL_GET_BLOCKID,{
            if(!it.getBoolean("error")){
                blockID = "Add New Map Location"
                blockID += it.getString("blockId")
                val separated: List<String> = blockID!!.split("||")
                arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, separated)
                if(!arrayAdapter!!.isEmpty){
                    blockIDSpn.adapter = arrayAdapter
                }
            }
        },{
            Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
        }).post("1" to "1")


        blockIDSpn.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                if(parent.getItemAtPosition(position).toString().trim() =="Add New Map Location"){
                    blockIDTxt.isEnabled = true
                    addLocation.visibility = View.VISIBLE
                    editLocation.visibility = View.GONE

                    blockIDTxt.setText("")
                    blockNameTxt.setText("")
                    addressTxt.setText("")
                    latTxt.setText("")
                    longTxt.setText("")
                }else{
                    blockIDTxt.isEnabled = false
                    addLocation.visibility = View.GONE
                    editLocation.visibility = View.VISIBLE

                    WolfRequest(Constants.URL_GET_LOCATION,{
                        Toast.makeText(applicationContext,it.getString("message"), Toast.LENGTH_SHORT).show()
                        if(!it.getBoolean("error")){
                            blockIDTxt.setText(it.getString("BID"))
                            blockNameTxt.setText(it.getString("blockName"))
                            addressTxt.setText(it.getString("address"))
                            latTxt.setText(it.getDouble("latitude").toString())
                            longTxt.setText(it.getDouble("longitude").toString())
                        }
                    },{
                        Toast.makeText(applicationContext,it, Toast.LENGTH_SHORT).show()
                    }).post("BID" to parent.getItemAtPosition(position).toString().trim())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(0)
            }
        }


        insertLatLong.setOnClickListener {
            val intent = Intent(this, longLatActivity::class.java)
            startActivityForResult(intent,12)
        }

        addLocation.setOnClickListener {
            if(blockIDTxt.text.trim().toString().isNullOrBlank() || blockNameTxt.text.trim().toString().isNullOrBlank() ||addressTxt.text.trim().toString().isNullOrBlank() || latTxt.text.trim().toString().isNullOrBlank() || longTxt.text.trim().toString().isNullOrBlank()){
                Toast.makeText(this,"Field cannot be left empty", Toast.LENGTH_SHORT).show()
            }else{
                WolfRequest(Constants.URL_ADD_LOCATION,{
                    Toast.makeText(this,it.getString("message"), Toast.LENGTH_SHORT).show()
                    if(!it.getBoolean("error")){
                        finish()
                    }
                },{
                    Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                }).post(
                    "BID" to blockIDTxt.text.trim().toString(),
                    "blockName" to blockNameTxt.text.trim().toString(),
                    "address" to addressTxt.text.trim().toString(),
                    "lat" to latTxt.text.trim().toString(),
                    "long" to longTxt.text.trim().toString()
                )
            }
        }

        editLocation.setOnClickListener {
            if(blockNameTxt.text.trim().toString().isNullOrBlank() ||addressTxt.text.trim().toString().isNullOrBlank() || latTxt.text.trim().toString().isNullOrBlank() || longTxt.text.trim().toString().isNullOrBlank()){
                Toast.makeText(this,"Field cannot be left empty", Toast.LENGTH_SHORT).show()
            }else {

                WolfRequest(Constants.URL_UPDATE_LOCATION, {
                    Toast.makeText(this,it.getString("message"), Toast.LENGTH_SHORT).show()
                    if (!it.getBoolean("error")) {
                        finish()
                    }
                }, {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }).post(
                    "BID" to blockIDTxt.text.trim().toString(),
                    "blockName" to blockNameTxt.text.trim().toString(),
                    "address" to addressTxt.text.trim().toString(),
                    "lat" to latTxt.text.trim().toString(),
                    "long" to longTxt.text.trim().toString()
                )
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                latTxt.setText(data!!.getDoubleExtra("Latitude",0.0).toString())
                longTxt.setText(data!!.getDoubleExtra("Longitude",0.0).toString())
            }
        }
    }
}
