package com.example.taructraverse2

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class longLatActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var confimrBtn: Button

    private var lat:Double?=null
    private var long:Double?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_lat)
        Mapbox.getInstance(this,
            getString(R.string.mapBox_token))


        mapView = findViewById(R.id.mapViewLatLong)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS){


            it.addImage("destination-icon-id", BitmapFactory.decodeResource(this.resources, R.drawable.mapbox_marker_icon_default))

            val geoJsonSource = GeoJsonSource("destination-source-id")
            it.addSource(geoJsonSource)

            val destinationSymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
            destinationSymbolLayer.withProperties(
                PropertyFactory.iconImage("destination-icon-id"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
            it.addLayer(destinationSymbolLayer)


            confimrBtn=findViewById(R.id.confirmButton)
            confimrBtn.setOnClickListener{
                val intent = Intent()
                intent.putExtra("Latitude", lat)
                intent.putExtra("Longitude", long)
                setResult(RESULT_OK, intent);
                finish()
            }

        }
        map.addOnMapClickListener(this)

     }

    override fun onMapClick(point: LatLng): Boolean {

        lat = point.latitude
        long = point.longitude
        val destination = Point.fromLngLat(point.longitude, point.latitude)
        val source = map.style?.getSourceAs<GeoJsonSource>("destination-source-id")

        source?.setGeoJson(Feature.fromGeometry(destination))
        confimrBtn.visibility = View.VISIBLE
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
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
