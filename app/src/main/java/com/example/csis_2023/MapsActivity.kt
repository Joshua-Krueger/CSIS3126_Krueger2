package com.example.csis_2023

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
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
import com.google.android.gms.maps.model.Marker
import org.w3c.dom.Text

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var helpDialog: Dialog
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("token", TokenManager.getToken().toString())
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewDetailsButton.setOnClickListener {
            selectedMarker?.let { marker ->
                val intent = Intent(this, LocationDetailsActivity::class.java)
                intent.putExtra("name", marker.title)
                startActivity(intent)
            }
        }


        binding.helpButton.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Maps Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "This page is the map of all locations worldwide!\n\n" +
                    "From this page, you can press the arrow in the top left to return to your profile\n\n" +
                    "You can click on any marker to see its name and description\n\n" +
                    "After clicking on a marker, you can click the view details button to go to its page!"

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        // Set the Toolbar as the support action bar
        setSupportActionBar(binding.toolbar)

        // Show the up navigation arrow in the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.google.android.material.R.drawable.ic_arrow_back_black_24)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle the up navigation button press
        onBackPressed()
        return true
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

        map.setOnMarkerClickListener { marker ->
            selectedMarker = marker
            false
        }

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View? {
                val view = layoutInflater.inflate(R.layout.marker_info_window, null)
                val title = view.findViewById<TextView>(R.id.titleTextView)
                val snippet = view.findViewById<TextView>(R.id.snippetTextView)

                title.text = marker.title
                snippet.text = marker.snippet
                // TODO find a way to add the rating number from the database here so i can make the stars show up in the info window
                return view
            }

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }
        })
    }

    private fun displayLocations() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_locations.php"

        //val url = "http://192.168.1.154/fishfinder/send_locations.php"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val location = response.getJSONObject(i)
                    val name = location.getString("name")
                    val latitude = location.getString("latitude")
                    val longitude = location.getString("longitude")
                    val description = location.getString("description")

                    val newLocation = LatLng(latitude.toDouble(),longitude.toDouble())

                    val markerOptions = MarkerOptions().position(newLocation).title(name).snippet(description)
                    map.addMarker(markerOptions)
                    map.moveCamera(CameraUpdateFactory.newLatLng(newLocation))

                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }

}