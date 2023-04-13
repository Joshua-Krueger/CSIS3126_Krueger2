package com.example.csis_2023

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.csis_2023.databinding.ActivityCoordinateBinding

class CoordinateActivity : AppCompatActivity() {

    private lateinit var bind : ActivityCoordinateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityCoordinateBinding.inflate(layoutInflater)
        setContentView(bind.root)

        displayLocations()
        //testing search bar shenanigans
        val searchBar = findViewById<EditText>(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                filterLayouts(s.toString())
            }
        })
        // search bar shenanigans^
        bind.addBtnCoords.setOnClickListener{
            val intent = Intent(this, NewLocationActivity::class.java)
            startActivity(intent)
        }
        bind.backBtnCoords.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    //function for the search bar to filter with
    private fun filterLayouts(searchText: String) {
        // gets all of the layouts added by the display function
        val coordsHolder = findViewById<LinearLayout>(R.id.coordsContainer)
        val childCount = coordsHolder.childCount

        // looks through them
        for (i in 0 until childCount) {
            val childView = coordsHolder.getChildAt(i)
            val nameTextView = childView.findViewById<TextView>(R.id.nameTextView)
            val townStateTextView = childView.findViewById<TextView>(R.id.townStateTextView)
            val descriptionTextView = childView.findViewById<TextView>(R.id.descriptionTextView)

            // finds the ones that have the desired text
            if (nameTextView.text.contains(searchText, ignoreCase = true) ||
                townStateTextView.text.contains(searchText, ignoreCase = true) ||
                descriptionTextView.text.contains(searchText, ignoreCase = true)) {
                childView.visibility = View.VISIBLE
            } else {
                // if it doesn't find the desired text it makes them invisible
                childView.visibility = View.GONE
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    private fun displayLocations() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_locations.php"

        //val url = "http://10.129.90.217/fishfinder/send_locations.php"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val location = response.getJSONObject(i)
                    val name = location.getString("name")
                    val town = location.getString("town")
                    val state = location.getString("state")
                    val description = location.getString("description")

                    val locationRow = layoutInflater.inflate(R.layout.location_row, null)
                    locationRow.findViewById<TextView>(R.id.nameTextView).text = name
                    locationRow.findViewById<TextView>(R.id.townStateTextView).text = "$town, $state"
                    locationRow.findViewById<TextView>(R.id.descriptionTextView).text = description

                    val viewDetailsButton = locationRow.findViewById<Button>(R.id.viewDetailsButton)
                    viewDetailsButton.setOnClickListener {
                        val intent = Intent(this, LocationDetailsActivity::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("town", town)
                        intent.putExtra("state", state)
                        intent.putExtra("description", description)
                        startActivity(intent)
                    }

                    bind.coordsContainer.addView(locationRow)
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }


}