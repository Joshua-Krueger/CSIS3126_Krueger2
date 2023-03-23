package com.example.csis_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.csis_2023.databinding.ActivityCoordinateBinding

class CoordinateActivity : AppCompatActivity() {

    private lateinit var bind : ActivityCoordinateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bind = ActivityCoordinateBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.addBtnCoords.setOnClickListener{
            val intent = Intent(this, NewLocationActivity::class.java)
            startActivity(intent)
        }
        bind.backBtnCoords.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}