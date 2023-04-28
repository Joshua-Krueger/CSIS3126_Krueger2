package com.example.csis_2023

import android.app.Dialog
import android.content.Intent
import android.media.session.MediaSession.Token
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.csis_2023.databinding.ActivityLocationDetailsBinding
import org.json.JSONObject

class LocationDetailsActivity : AppCompatActivity() {

    private lateinit var bind: ActivityLocationDetailsBinding
    private lateinit var helpDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("token", TokenManager.getToken().toString())
        bind = ActivityLocationDetailsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Initialize the grid view
        val gridView: GridView = bind.imageGrid

        val name = intent.getStringExtra("name")
        locationDetailRequest(name)

        bind.helpButtonLocationDetails.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Location Details Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "This page tells you everything the creator has listed about a location including:\n" +
                    "1. A thumbnail image of the location\n" +
                    "2. The location's name, location, and a brief description of it\n" +
                    "3. The location's rating - this is based on the average of all of the ratings of this location\n\n" +
                    "From this page, you can add click the rate button and fill out the page that shows up to rate the location\n" +
                    "You can also click the add fish image button to add pictures you've taken of fish you catch at the location!\n\n" +
                    "You can scroll through the pictures of fish people have added as well\n\n" +
                    "The back button will take you to the page you last came from."

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        bind.addFishImageBtn.setOnClickListener{
            val intent = Intent(this, ImageActivity::class.java).apply {
                putExtra("type", "fish")
                putExtra("locationName", intent.getStringExtra("name"))
            }
            startActivity(intent)
        }

        bind.locationDetailsRateBtn.setOnClickListener{
            showRatingOverlay(name)
        }

        bind.changeLocationImageBtn.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java).apply {
                putExtra("type", "location")
                putExtra("locationName", intent.getStringExtra("name"))
            }
            startActivity(intent)
        }

        bind.locationDetailsBackBtn.setOnClickListener {
            if (intent.getStringExtra("cameFrom") == "map"){
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, CoordinateActivity::class.java)
                startActivity(intent)
            }
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/get_image_links.php"

        Log.e("name being sent", name.toString())

        val params = JSONObject()
        params.put("name", name.toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                Log.e("parse message", "parsing image links")
                parseImageLinks(response, gridView)
            },
            { error ->
                Log.e("Error", "Failed to fetch image links: $error")
            })

        queue.add(request)
    }
    private fun locationDetailRequest(name: String?) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_location_data.php"

        Log.e("name being sent", name.toString())

        val params = JSONObject()
        params.put("name", name.toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                if (response.getString(("status")) == "success") {
                    val location = response.getJSONObject("location")
                    val town = location.getString("town")
                    Log.e("Response Data",town)
                    val state = location.getString("state")
                    Log.e("Response Data",state)
                    val rating = location.getString("rating").toFloat()
                    Log.e("Response Data",rating.toString())
                    val description = location.getString("description")
                    Log.e("Response Data",description)
                    val thumbnail = location.getString("thumbnail")
                    Log.e("Response Data",thumbnail)

                    Glide.with(this)
                        .load(thumbnail)
                        .into(bind.imageView)

                    bind.locationName.text = name
                    Log.e("num of stars", rating.toString())
                    bind.ratingBar.rating = rating
                    bind.locationTown.text = "$town, $state"
                    bind.locationDescription.text = description
                }
            },
            { error ->
                Log.e("error", error.toString())
                Toast.makeText(
                    this,
                    "something went wrong with displaying location details",
                    Toast.LENGTH_LONG
                ).show()
            })

        queue.add(request)
    }

    private fun parseImageLinks(imagesJson: JSONObject, gridView: GridView) {
        Log.e("parseImageLinks", "parsing image links")
        val images = mutableListOf<String>()
        Log.e("API Response", imagesJson.toString())
        if (imagesJson.has("images")) {
            Log.e("parseImageLinks", "imagesJson has images")
            val imagesArray = imagesJson.optJSONArray("images")
            if (imagesArray != null) {
                Log.e("parseImageLinks", "imagesArray is not null")
                for (i in 0 until imagesArray.length()) {
                    val imageUrl = imagesArray.optString(i)
                    if (!imageUrl.isNullOrEmpty()) {
                        images.add(imageUrl)
                    }
                }
            }
        }

        // Create an adapter and set it to the gridview
        val adapter = ImageAdapter(this, images)
        gridView.adapter = adapter
    }

    private fun showRatingOverlay(name: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.rating_overlay)

        val ratingBar = dialog.findViewById<RatingBar>(R.id.rating_bar)
        val descriptionEditText = dialog.findViewById<EditText>(R.id.description_edit_text)
        val submitButton = dialog.findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            // Get the rating and description values and do something with them
            val rating = ratingBar.rating
            if(rating == null){
                Toast.makeText(this,"Please select at least one star",Toast.LENGTH_SHORT).show()
            }else{
            val description = descriptionEditText.text.toString()

            ratingRequest(rating,description,name)
            // Dismiss the dialog
            dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun ratingRequest(rating: Float, description: String, name: String?) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_location_rating.php"

        val params = JSONObject()
        params.put("rating",rating.toString())
        params.put("ratingDescription",description)
        params.put("locationName",name.toString())
        params.put("token",TokenManager.getToken().toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                Log.e("response",response.toString())
                if (response.getString(("status")) == "success") {
                    Toast.makeText(this,"Rating Received",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,response.getString("message"),Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("error message: ", error.toString())
                Toast.makeText(this, "something went wrong with adding the rating", Toast.LENGTH_LONG).show()
            })
        queue.add(request)
    }

}
