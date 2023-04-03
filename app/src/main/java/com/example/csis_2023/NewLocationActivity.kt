package com.example.csis_2023

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import java.math.BigDecimal
import java.math.RoundingMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.csis_2023.databinding.ActivityNewLocationBinding
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.util.*

//TODO figuring out how this works still
class NewLocationActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

    private var currentLocation : LatLng? = null
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

        bind.newLocationSaveBtn.setOnClickListener{
            val name = bind.locationName.text.toString()
            val description = bind.locationDescription.text.toString()
            if (name == "" || description == ""){
                Toast.makeText(this,"all fields must be filled",Toast.LENGTH_LONG).show()
            }else{
            newLocationRequest()
            }
        }

        // Create the LocationManager and LocationListener instances
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates here
                currentLocation = LatLng(location.latitude, location.longitude)

                val latitude = BigDecimal(location.latitude).setScale(5, RoundingMode.HALF_EVEN)
                val longitude = BigDecimal(location.longitude).setScale(5, RoundingMode.HALF_EVEN)
                Log.e("latitude",latitude.toString())
                bind.latitudeText.setText(latitude.toString())
                Log.e("longitude",longitude.toString())
                bind.longitudeText.setText(longitude.toString())
                Log.e("current location",currentLocation.toString())

                // Reverse geocode the location to get an address
                val geocoder = Geocoder(this@NewLocationActivity, Locale.getDefault())
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val state = address?.adminArea
                        val town = address?.locality
                        Log.e("state", state ?: "")
                        bind.locationState.setText(state)
                        Log.e("town", town ?: "")
                        bind.locationTown.setText(town)
                    }
                }
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

            // Get the last known location
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                currentLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                Log.e("last known latitude", lastKnownLocation.latitude.toString())
                Log.e("last known longitude", lastKnownLocation.longitude.toString())
                Log.e("current location", currentLocation.toString())
            }
        }
    }

    private fun newLocationRequest() {
        val token = TokenManager.getToken()
        val name = bind.locationName.text.toString()
        val state = bind.locationState.text.toString()
        val town = bind.locationTown.text.toString()
        val latlng = bind.latitudeText.text.toString() + "," + bind.longitudeText.text.toString()
        val latitude = bind.latitudeText.text.toString()
        val longitude = bind.longitudeText.text.toString()
        val description = bind.locationDescription.text.toString()
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("token", token)
        Log.e("send", token.toString())
        params.put("name", name)
        Log.e("send", name)
        params.put("state", state)
        Log.e("send", state)
        params.put("town", town)
        Log.e("send", town)
        params.put("latlng",latlng)
        Log.e("send",latlng)
        params.put("latitude", latitude)
        Log.e("send", latitude)
        params.put("longitude", longitude)
        Log.e("send", longitude)
        params.put("description", description)
        Log.e("send", description)

        val url = "http://10.129.17.5/fishfinder/add_location.php"
        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                Log.e("response",response.toString())
                val status = response.getString("status")
                Log.e("status", status)
                if (status == "success") {
                    Log.e("new location", "location should be added")
                    Toast.makeText(this, "Successfully added location", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CoordinateActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
                val responseString = response.toString()
                Log.d("response", responseString)
            },
            { error ->
                Toast.makeText(this, "Error with response", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }

        //suppression here because it will always check for permission before getting here
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        // Check if GPS provider is available
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Request location updates from the GPS provider
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10L,
                10f,
                locationListener
            )
        } else {
            // Neither network nor GPS provider is available, show an error message
            Toast.makeText(this, "Location provider not available", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("MissingPermission")
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

                // Get the last known location

                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastKnownLocation != null) {
                    currentLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    Log.e("last known latitude", lastKnownLocation.latitude.toString())
                    Log.e("last known longitude", lastKnownLocation.longitude.toString())
                    Log.e("current location", currentLocation.toString())
                }
            } else {
                // Permission denied, navigate back to previous activity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}