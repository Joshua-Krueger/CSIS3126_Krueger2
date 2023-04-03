package com.example.csis_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.csis_2023.databinding.ActivityLocationDetailsBinding

class LocationDetailsActivity : AppCompatActivity() {

    private lateinit var bind : ActivityLocationDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityLocationDetailsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        prepareFields()


        bind.locationDetailsBackBtn.setOnClickListener{
            val intent = Intent(this,CoordinateActivity::class.java)
            startActivity(intent)
        }


    }

    private fun prepareFields(){
        var name: String? = null
        var town: String? = null
        var state: String? = null
        var description: String? = null

        val intent = intent ?: return

        name = intent.getStringExtra("name")
        town = intent.getStringExtra("town")
        state = intent.getStringExtra("state")
        description = intent.getStringExtra("description")

        bind.locationState.setText(state)
        bind.locationTown.setText(town)
        bind.locationName.setText(name)
        bind.locationDescription.setText(description)
    }
}