package com.example.csis_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.csis_2023.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var bind : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val token = TokenManager.getToken().toString()
        profileRequest(token)

        bind.mapBtn.setOnClickListener{
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }

        bind.locationBtn.setOnClickListener{
            val intent = Intent(this, CoordinateActivity::class.java)
            startActivity(intent)
        }
        bind.LogoutBtn.setOnClickListener{
            logoutRequest(token)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logoutRequest(token: String) {
        val url = "http://10.129.17.5/fishfinder/logout.php"
        //val url = "http://10.129.90.217/fishfinder/logout.php"


        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.e("response:",response)
                if (response.trim() == "success") {
                    //intent to logged in activity goes in here
                    Toast.makeText(this, "logout successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "logout failed", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = token
                return params
            }
            // passing data
        }
        requestQueue.add(stringRequest)
    }

    private fun profileRequest(token: String) {
        val url = "http://10.129.17.5/fishfinder/load_profile.php"
        //val url = "http://10.129.90.217/fishfinder/load_profile.php"


        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.e("response:", response)
                if (response.substringBefore(":") == "information") {
                    //intent to logged in activity goes in here
                    Toast.makeText(this, "profile retrieval successful", Toast.LENGTH_SHORT).show()
                    val link = response.substringAfterLast(",")
                    var rating = response.substringAfter(",")
                    rating = rating.substringBefore(",")
                    var name = response.substringAfter(":")
                    name = name.substringBefore(",")
                    //bind.ProfileName.setText(name)
                    //bind.ProfileRatingBar.rating = rating
                    Log.e("link:",link)
                    Glide.with(this)
                        .load(link)
                        .into(bind.ProfilePhoto)


                    bind.ProfileRatingBar.rating = rating.toFloat()
                    bind.ProfileName.setText(name)

                } else {
                    Toast.makeText(this, "profile retrieval failed", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = token
                return params
            }
            // passing data
        }
        requestQueue.add(stringRequest)
    }

}