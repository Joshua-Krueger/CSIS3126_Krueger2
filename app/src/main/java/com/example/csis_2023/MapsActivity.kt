package com.example.csis_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.csis_2023.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        displayLocations()
    }

    private fun displayLocations() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_locations.php"

        //val url = "http://10.129.90.217/fishfinder/send_locations.php"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val location = response.getJSONObject(i)
                    val name = location.getString("name")
                    val town = location.getString("town")
                    val state = location.getString("state")
                    val latitude = location.getString("latitude")
                    val longitude = location.getString("longitude")
                    val description = location.getString("description")
                    val rating = location.getString("rating")

                    val newLocation = LatLng(latitude.toDouble(),longitude.toDouble())
                    map.addMarker(MarkerOptions().position(newLocation).title(name).snippet(description))
                    //map.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }

}