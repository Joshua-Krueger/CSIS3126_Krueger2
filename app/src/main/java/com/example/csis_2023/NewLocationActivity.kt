package com.example.csis_2023

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.csis_2023.databinding.ActivityNewLocationBinding
//TODO figuring out how this works still
class NewLocationActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var bind : ActivityNewLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityNewLocationBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.newLocationBackBtn.setOnClickListener{
            val intent = Intent(this, CoordinateActivity::class.java)
            startActivity(intent)
        }

        // Create the LocationManager and LocationListener instances
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates here
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Check for the location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
            // Request location updates
            requestLocationUpdates()
        }
    }

    //suppression here because it will always check for permission before getting here
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        // Request location updates from the LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, // or LocationManager.GPS_PROVIDER
            0L, // min time interval between location updates
            0f, // min distance between location updates
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates()
            } else {
                // Permission denied, navigate back to previous activity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
