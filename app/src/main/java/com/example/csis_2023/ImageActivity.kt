package com.example.csis_2023

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.csis_2023.databinding.ActivityImageBinding
import java.io.ByteArrayOutputStream

class ImageActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bind: ActivityImageBinding
    private lateinit var imageView: ImageView
    private lateinit var helpDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("token", TokenManager.getToken().toString())
        bind = ActivityImageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val imageType = intent.getStringExtra("type")
        Log.e("image Type", imageType.toString())
        val locationName = intent.getStringExtra("locationName")
        Log.e("Location Name", locationName.toString())
        imageView = bind.imageView

        bind.helpButtonImage.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Image Upload Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "This page allows you to add images to the app.\n\n" +
                    "The app knows where you just came from so you don't need to specify the type of picture you're adding.\n\n" +
                    "All you need to do is press select image, pick the image from your library, and then hit upload image!\n\n" +
                    "The back button will bring you back to the page that you came from."

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        val btnSelectImage: Button = bind.btnSelectImage
        btnSelectImage.setOnClickListener {
            selectImage()
        }

        val btnUploadImage: Button = bind.btnUploadImage
        btnUploadImage.setOnClickListener {
            uploadImage(imageType,locationName)
        }

        val btnBack: Button = bind.btnBack
        btnBack.setOnClickListener{
            if (imageType == "profile"){
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else if (imageType == "location" || imageType == "fish"){
                val intent = Intent(this, CoordinateActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val imageStream = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(selectedImage)
        }
    }

    private fun uploadImage(imageType: String?, locationName: String?) {
        val url = "http://10.129.17.5/fishfinder/image_upload.php"

        //val url = "http://192.168.1.154/fishfinder/image_upload.php"

        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
        if (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    Log.e("response", response.toString())
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { error ->
                    Log.e("error with image upload", error.toString())
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["image"] = encodedImage
                    params["token"] = TokenManager.getToken().toString()
                    Log.e("image sending : Token", TokenManager.getToken().toString())
                    params["type"] = imageType.toString()
                    Log.e("image sending : Type", imageType.toString())
                    params["locationName"] = locationName.toString()
                    Log.e("image sending : Location Name", locationName.toString())
                    return params
                }
            }

            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        } else {
            Toast.makeText(applicationContext, "Please select an image first.", Toast.LENGTH_LONG).show()
        }
    }
}
