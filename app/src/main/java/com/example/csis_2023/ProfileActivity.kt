package com.example.csis_2023

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.csis_2023.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var bind : ActivityProfileBinding
    private lateinit var helpDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("token", TokenManager.getToken().toString())
        bind = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val token = TokenManager.getToken().toString()
        profileRequest(token)

        bind.helpButtonProfile.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Profile Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "From this page you can do a couple of things:\n" +
                    "1. Change your profile picture\n    -click the change picture button.\n" +
                    "2. See all of your fish and the locations you caught them at!\n    -click the button next to the fish caught text\n" +
                    "3. see all of your locations listed out\n    -click the button next to the locations saved text.\n" +
                    "4. see all locations saved on the app!\n    -click the locations button\n" +
                    "5. See every location on a map!\n    -click the map button.\n" +
                    "6. Logout\n    -press the logout button.\n\n" +
                    "You can also view your trust rating. This is the value under the change picture button and is calculated based on other user's ratings of your locations"

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        bind.ChangeProfilePicBtn.setOnClickListener{
            val intent = Intent(this,ImageActivity::class.java)
            startActivity(intent.putExtra("type","profile"))
        }

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

        //val url = "http://192.168.1.154/fishfinder/logout.php"

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

        //val url = "http://192.168.1.154/fishfinder/load_profile.php"

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