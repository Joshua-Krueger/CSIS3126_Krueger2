package com.example.csis_2023

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.csis_2023.databinding.ActivityUserFishBinding
import org.json.JSONObject

class UserFishActivity : AppCompatActivity() {
    private lateinit var bind : ActivityUserFishBinding
    private lateinit var helpDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("token", TokenManager.getToken().toString())
        bind = ActivityUserFishBinding.inflate(layoutInflater)
        setContentView(bind.root)

        displayFishImages()

        bind.helpButtonCoords.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Fish Picture Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "This page allows you to view or delete all of your fish!\n\n" +
                    "The back button will return you to your profile\n\n" +
                    "The delete button next to each picture will remove that from our database completely\n\n" +
                    "The area where all the pictures are displayed can be scrolled through to see them all"

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        bind.backBtnCoords.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }


    private fun displayFishImages() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.129.17.5/fishfinder/send_user_fish.php"

        val token = TokenManager.getToken()
        Log.e("sending token:", token.toString())

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Log.d("FishResponse", response)
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("images")
                for (i in 0 until jsonArray.length()) {
                    val imageUrl = jsonArray.getString(i)

                    val fishItem = layoutInflater.inflate(R.layout.user_fish_item, null)
                    val fishImage = fishItem.findViewById<ImageView>(R.id.fishImage)
                    val deleteButton = fishItem.findViewById<Button>(R.id.fishItemDeleteButton)

                    // Load image using Glide
                    Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(fishImage)

                    deleteButton.setOnClickListener {
                        deleteRequest(imageUrl, fishItem)
                    }

                    bind.fishContainer.addView(fishItem)
                }
            },
            Response.ErrorListener { error ->
                Log.e("error",error.message.toString())
                Toast.makeText(this, "Error displaying fish images: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = token.toString()
                return params
            }
        }

        queue.add(request)
    }



    private fun deleteRequest(imageUrl: String, fishItem: View) {
        // Instantiate the RequestQueue
        Log.e("token",TokenManager.getToken().toString())
        Log.e("imageUrl",imageUrl)
        val queue = Volley.newRequestQueue(this)

        // Set the URL of the PHP script
        val url = "http://10.129.17.5/fishfinder/delete_fish_image.php"

        // Set the POST parameters
        val params = HashMap<String, String>()
        params["token"] = TokenManager.getToken().toString()
        params["imageUrl"] = imageUrl

        // Create a new StringRequest with the POST method
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"succeeded in deleting image",Toast.LENGTH_SHORT).show()
                bind.fishContainer.removeView(fishItem)
                Log.e("Delete response", response)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"error deleting image",Toast.LENGTH_SHORT).show()
                Log.e("Delete error", error.toString())
            }) {
            // Override the getParams method to set the POST parameters
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        // Add the request to the RequestQueue
        queue.add(stringRequest)

    }


}