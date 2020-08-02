package com.example.taructraverse2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    private lateinit var errMsgTxt:TextView
    private lateinit var confrimBtn:Button
    private lateinit var clearBtn:Button

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

        errMsgTxt=findViewById(R.id.txtErrorMsg)
        confrimBtn=findViewById(R.id.btnConfirm)
        clearBtn=findViewById(R.id.btnClear)


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
                    errMsgTxt.text = ""
                    confrimBtn.visibility = View.GONE
                    clearBtn.visibility = View.GONE

                    blockIDTxt.setText("")
                    blockNameTxt.setText("")
                    addressTxt.setText("")
                    latTxt.setText("")
                    longTxt.setText("")
                }else{
                    blockIDTxt.isEnabled = false
                    addLocation.visibility = View.GONE
                    editLocation.visibility = View.VISIBLE
                    errMsgTxt.text = ""
                    confrimBtn.visibility = View.GONE
                    clearBtn.visibility = View.GONE

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
                checkLocation(addressTxt.text.trim().toString(),latTxt.text.trim().toString(),longTxt.text.trim().toString(),Constants.URL_ADD_LOCATION,"ADD")
            }
        }

        editLocation.setOnClickListener {
            if(blockNameTxt.text.trim().toString().isNullOrBlank() ||addressTxt.text.trim().toString().isNullOrBlank() || latTxt.text.trim().toString().isNullOrBlank() || longTxt.text.trim().toString().isNullOrBlank()){
                Toast.makeText(this,"Field cannot be left empty", Toast.LENGTH_SHORT).show()
            }else {
                checkLocation(addressTxt.text.trim().toString(),latTxt.text.trim().toString(),longTxt.text.trim().toString(),Constants.URL_UPDATE_LOCATION,"EDIT")
            }
        }

        confrimBtn.setOnClickListener {
            if(blockIDSpn.selectedItemPosition == 0){
                updateOrAddMap(Constants.URL_ADD_LOCATION)
            }else{
                updateOrAddMap(Constants.URL_UPDATE_LOCATION)
            }
        }

        clearBtn.setOnClickListener {
            if(blockIDSpn.selectedItemPosition == 0){
                addLocation.visibility = View.VISIBLE
                editLocation.visibility = View.GONE
            }else{
                addLocation.visibility = View.GONE
                editLocation.visibility = View.VISIBLE
            }
            addressTxt.isEnabled = true
            insertLatLong.isEnabled = true
            errMsgTxt.text=""
            confrimBtn.visibility = View.GONE
            clearBtn.visibility = View.GONE
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

    fun checklatlong(lat:String,long:String):Boolean{
        val latitude = lat.toDouble()
        val longitude = long.toDouble()
        return !(longitude>101.737927 || longitude<101.724101 || latitude>3.219567 || latitude<3.212926)
    }

    fun checkLocation(address:String,lat:String,long:String,link:String,hideCond:String){
        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapBox_token))
            .query(address).autocomplete(true)
            .build()

        mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {

                val results = response.body()!!.features()
                if (results.size == 1){
                    if(checklatlong(results[0].center()?.latitude()!!.toString(),results[0].center()?.longitude()!!.toString())){//address ok,is within campus
                        if(checklatlong(lat,long)){//given latlong within campus
                            updateOrAddMap(link)
                            mapboxGeocoding.cancelCall()
                        }else{
                            if(hideCond=="ADD"){
                                addLocation.visibility = View.GONE
                            }else{
                                editLocation.visibility = View.GONE
                            }
                            confrimBtn.visibility = View.VISIBLE
                            clearBtn.visibility = View.VISIBLE
                            addressTxt.isEnabled = false
                            insertLatLong.isEnabled = false
                            errMsgTxt.text = "Longitude and Latitude is not within campus"
                            mapboxGeocoding.cancelCall()
                        }
                    }else{
                        if(hideCond=="ADD"){
                            addLocation.visibility = View.GONE
                        }else{
                            editLocation.visibility = View.GONE
                        }
                        confrimBtn.visibility = View.VISIBLE
                        clearBtn.visibility = View.VISIBLE
                        addressTxt.isEnabled = false
                        insertLatLong.isEnabled = false
                        if(checklatlong(lat,long)){//given latlong within campus
                            errMsgTxt.text = "Address is not within campus"
                            mapboxGeocoding.cancelCall()
                        }else{
                            errMsgTxt.text = "Address is not within campus, Longitude and Latitude is not within campus"
                            mapboxGeocoding.cancelCall()
                        }
                    }
                }else{
                    if(checklatlong(results[0].center()?.latitude()!!.toString(),results[0].center()?.longitude()!!.toString())){//address ok,is within campus
                        if(checklatlong(lat,long)){//given latlong within campus
                            updateOrAddMap(link)
                            mapboxGeocoding.cancelCall()
                        }else{
                            if(hideCond=="ADD"){
                                addLocation.visibility = View.GONE
                            }else{
                                editLocation.visibility = View.GONE
                            }
                            confrimBtn.visibility = View.VISIBLE
                            clearBtn.visibility = View.VISIBLE
                            addressTxt.isEnabled = false
                            insertLatLong.isEnabled = false
                            errMsgTxt.text = "Longitude and Latitude is not within campus"
                            mapboxGeocoding.cancelCall()
                        }
                    }else{
                        if(hideCond=="ADD"){
                            addLocation.visibility = View.GONE
                        }else{
                            editLocation.visibility = View.GONE
                        }
                        confrimBtn.visibility = View.VISIBLE
                        clearBtn.visibility = View.VISIBLE
                        addressTxt.isEnabled = false
                        insertLatLong.isEnabled = false
                        if(checklatlong(lat,long)){//given latlong within campus
                            errMsgTxt.text = "Address is not within campus"
                            mapboxGeocoding.cancelCall()
                        }else{
                            errMsgTxt.text = "Address is not within campus, Longitude and Latitude is not within campus"
                            mapboxGeocoding.cancelCall()
                        }
                    }
                }


            }
            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(applicationContext, "Geocoding Not Work", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun updateOrAddMap(link:String){
        WolfRequest(link,{
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
