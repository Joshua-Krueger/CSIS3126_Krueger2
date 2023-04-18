package com.example.csis_2023

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.csis_2023.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var loadButton: Button
    lateinit var backButton: Button
    private lateinit var bind: ActivityImageBinding

    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityImageBinding.inflate(layoutInflater)

        setContentView(bind.root)

        //title = "KotlinApp"
        imageView = bind.imageView
        loadButton = bind.buttonLoadPicture
        backButton = bind.buttonBack

        backButton.setOnClickListener{
            onBackPressed()
        }

        loadButton.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}