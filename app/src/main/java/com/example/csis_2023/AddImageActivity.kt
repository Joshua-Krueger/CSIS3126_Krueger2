package com.example.csis_2023

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.csis_2023.databinding.ActivityAddImageBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddImageActivity : AppCompatActivity() {

    // Firebase Storage reference
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    // Views
    private lateinit var imageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var bind : ActivityAddImageBinding

    // URI of the selected image
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Initialize views
        imageView = bind.imageView
        chooseImageButton = bind.chooseImageButton
        uploadImageButton = bind.uploadImageButton

        // Set click listeners for the buttons
        chooseImageButton.setOnClickListener { chooseImage() }
        uploadImageButton.setOnClickListener { uploadImage() }
    }

    // Launches the image picker
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handles the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            imageUri = data.data

            // Set the image view to display the selected image
            imageView.setImageURI(imageUri)
        }
    }

    // Uploads the selected image to Firebase Storage
    private fun uploadImage() {
        if (imageUri != null) {
            val imageRef = storageRef.child("images/${UUID.randomUUID()}")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image to upload", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}