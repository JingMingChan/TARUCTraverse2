package com.example.taructraverse2.ui.map

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.taructraverse2.Constants
import com.example.taructraverse2.R
import com.example.taructraverse2.WolfRequest
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class MapFragment : Fragment(), OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var txtLocation:AutoCompleteTextView
    private lateinit var btnSrch: ImageButton
    private lateinit var currentRoute: DirectionsRoute
    private lateinit var startBtn: Button

    private var newBlockName:String= "Others"
    private var arrayAdapter:ArrayAdapter<String>? = null

    private var locationEngine: LocationEngine? = null
    private var locationComponent: LocationComponent? = null
    private val callback = LocationListeningCallback(this)
    private var navigationMapRoute: NavigationMapRoute? = null
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val TAG = "DirectionsActivity"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(context!!,
            getString(R.string.mapBox_token))
        val root= inflater.inflate(R.layout.fragment_map, container, false)

        context?.let { WolfRequest.init(it) }
        txtLocation = root.findViewById(R.id.autoTextLocation)

        WolfRequest(Constants.URL_GET_BLOCKNAME,{
            if(!it.getBoolean("error")){

                val blockName = it.getString("blockName").trim().split("||")
                blockName.forEach { n -> newBlockName += ",$n" }

                val separated: List<String> = newBlockName.trim().split(",")
                arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, separated)
                if(!arrayAdapter!!.isEmpty){
                    txtLocation.setAdapter(arrayAdapter)
                }
            }
        },{
            Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
        }).post("1" to "1")

        btnSrch = root.findViewById(R.id.btnSrch)
        startBtn = root.findViewById(R.id.startButton)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS){
            enableLocationComponent(it)

            addDestinationIconSymbolLayer(it)

            btnSrch.setOnClickListener{

                if(txtLocation.text.trim().isNullOrEmpty()){
                    Toast.makeText(context, "Please key in location address", Toast.LENGTH_LONG).show()
                }else{
                    getAddress(txtLocation.text.trim().toString())
                    txtLocation.text.clear()
                }
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            }

            startBtn.setOnClickListener{
                val simulateRoute = false
                val options= NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    .shouldSimulateRoute(simulateRoute)
                    .build()
                NavigationLauncher.startNavigation(activity,options)
            }

        }
        map.addOnMapClickListener(this)
    }

    //@SuppressWarnings("MissingPermission")
    fun enableLocationComponent(style:Style){
        locationComponent = map.locationComponent
        val locationComponentOptions = LocationComponentOptions.builder(context!!)
            .foregroundDrawable(R.drawable.mapbox_user_icon)
            .foregroundDrawableStale(R.drawable.mapbox_user_icon_stale)
            .bearingDrawable(R.drawable.mapbox_user_bearing_icon)
            .accuracyAlpha(.3f)
            .pulseEnabled(true)
            .pulseColor(Color.YELLOW)
            .pulseAlpha(.4f)
            .build()

        val locationComponentActivationOptions = LocationComponentActivationOptions
            .builder(context!!, style)
            .locationComponentOptions(locationComponentOptions)
            .useDefaultLocationEngine(false)
            .build()

        locationComponent?.activateLocationComponent(locationComponentActivationOptions)

        // Enable to make component visible
        locationComponent?.isLocationComponentEnabled = true

        // Set the component's camera mode
        locationComponent?.cameraMode = CameraMode.TRACKING_GPS

        // Set the component's render mode
        locationComponent?.renderMode = RenderMode.COMPASS

        initializeLocationEngine()
    }

    fun initializeLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(context!!)
        val request =
            LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build()
        locationEngine!!.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine!!.getLastLocation(callback)
    }

    fun addDestinationIconSymbolLayer(loadedMapStyle:Style) {

        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.resources, R.drawable.mapbox_marker_icon_default))

        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)

        val destinationSymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
            PropertyFactory.iconImage("destination-icon-id"),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    fun getRoute(origin:Point, destination: Point){
        val source = map.style?.getSourceAs<GeoJsonSource>("destination-source-id")

        source?.setGeoJson(Feature.fromGeometry(destination))

        NavigationRoute.builder(context)
            .accessToken(getString(R.string.mapBox_token))
            .origin(origin)
            .destination(destination)
            .profile("walking")//allow route in walking area
            .build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    Toast.makeText(activity, "Destination Selected", Toast.LENGTH_LONG).show()
                    if (response.body() == null) {
                        Log.e(TAG, "No routes found, make sure you set the right user and access token.")
                        return
                    } else if (response.body()!!.routes().size < 1) {
                        Log.e(TAG, "No routes found")
                        return
                    }
                    currentRoute = response.body()!!.routes()[0]

                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute!!.updateRouteVisibilityTo(false)
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute)
                    }
                    navigationMapRoute!!.addRoute(currentRoute);
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Toast.makeText(activity, "Route not found", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun locationSrch(address:String, lat:Double, long:Double){

        val origin = Point.fromLngLat(map.locationComponent.lastKnownLocation!!.longitude, map.locationComponent.lastKnownLocation!!.latitude)


        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapBox_token))
            .query(address).autocomplete(true)
            .build()

        mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {

                val results = response.body()!!.features()
                if (results.size == 1) {//address is found one and only one, use mapbox geocoding point

                    val destination = Point.fromLngLat(results[0].center()?.longitude()!!, results[0].center()?.latitude()!!)
                    getRoute(origin, destination)
                    startBtn.visibility = View.VISIBLE
                    mapboxGeocoding.cancelCall()

                }else if(results.size > 1 || results.size == 0) {//if more than 1 address found on mapboxgeocoding, use own db point to query
                    val destination = Point.fromLngLat(long, lat)
                    getRoute(origin, destination)
                    startBtn.visibility = View.VISIBLE
                    mapboxGeocoding.cancelCall()
                }else{
                    Toast.makeText(context, "Location Not Exist", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(context, "Search Location Not Found", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getAddress(searchResult:String){

        WolfRequest(Constants.URL_LOCATION_SEARCH,{
            if(!it.getBoolean("error")){
                locationSrch(it.getString("address").toString(), it.getDouble("latitude"),it.getDouble("longitude"))
            }else{
                Toast.makeText(context, it.getString("message"), Toast.LENGTH_LONG).show()
            }
        },{
            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
        }).post("srchTxt" to searchResult)
    }


    override fun onMapClick(point: LatLng): Boolean {
        val origin = Point.fromLngLat(map.locationComponent.lastKnownLocation!!.longitude, map.locationComponent.lastKnownLocation!!.latitude)
        val destination = Point.fromLngLat(point.longitude, point.latitude)
        getRoute(origin, destination)
        startBtn.visibility = View.VISIBLE
        return true
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationEngine != null) {
            locationEngine?.removeLocationUpdates(callback)
        }
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }


    private class LocationListeningCallback internal constructor(fragment: MapFragment) :
        LocationEngineCallback<LocationEngineResult> {

        private val activityWeakReference: WeakReference<MapFragment>

        init {this.activityWeakReference = WeakReference(fragment)
        }
        //The LocationEngineCallback interface's method which fires when the device's location has changed.
        override fun onSuccess(result: LocationEngineResult) {
            val fragment = activityWeakReference.get()
            if(fragment !=null){
                val location = result.lastLocation

                if(location == null){
                    return
                }

                if(fragment.map != null && result.lastLocation != null){
                    fragment.map.locationComponent.forceLocationUpdate(result.lastLocation)
                }
            }
        }
        // The LocationEngineCallback interface's method which fires when the device's location can not be captured
        override fun onFailure(exception: Exception) {
            val fragment = activityWeakReference.get()
            if(fragment !=null){
                Toast.makeText(fragment.context,exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
