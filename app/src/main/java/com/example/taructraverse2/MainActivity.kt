package com.example.taructraverse2


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taructraverse2.ui.map.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsManager


class MainActivity : AppCompatActivity() {
    private lateinit var permissionManager: PermissionsManager
    private var UID :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = this.intent.extras
        UID = extras?.getInt("UID").toString()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_map, R.id.navigation_chatbot, R.id.navigation_user))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun getUID():String?{
        return UID
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       // MapFragment().onRequestPermissionsResult(requestCode, permissions, grantResults)

//        ActivityCompat.requestPermissions(this,permissions,123)
//        MapFragment.p
            //.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }
}
