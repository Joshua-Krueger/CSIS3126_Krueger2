package com.example.csis_2023
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.csis_2023.databinding.ActivityLoginBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var bind : ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    // Suppression must be included because of the getColumnIndex check below this is ok to ignore because the only way it will ever reach
    // that check is by confirming there are any accounts made. Therefore, the potential of a -1 is not a threat and can be suppressed
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // The bind here is used instead of findviewbyid. This creates less overhead and allows me to refer to the class layout simply
        // with a root layout bound to a variable - creates way less overhead and is easier to use
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.loginEmail.setText(intent.getStringExtra("email"))

        // im using shared preferences to save userdata on the device in order to do a remember me button
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // Check if "rememberMe" flag is set to true
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            // Auto-fill login fields with email and password
            // keep remember me checked
            val email = sharedPreferences.getString("email", "")
            val password = sharedPreferences.getString("password", "")
            bind.loginEmail.setText(email)
            bind.loginPass.setText(password)
            bind.rememberMeCheck.isChecked = true
        }

        // listen for the click on login
        bind.loginBtn.setOnClickListener{
            // get relevant data
            val email = bind.loginEmail.text.toString()
            val password = bind.loginPass.text.toString()
            val token = Random.nextInt(0,99999)
            Log.e("login", token.toString())
            tokenCheck(token)
            //TODO ^trying to make this recursive^ -> so that if the token generated is already in use, it will generate another until it finds a free one
            TokenManager.setToken(token.toString())
            loginRequest(email,password, token)

        }
        // check to see if the user clicks on the register link
        bind.registrationLink.setOnClickListener {
            // switch to the registration activity
            val email = bind.loginEmail.text.toString()
            val intent = Intent(this, RegistrationActivity::class.java)
            // wanted to bring email from login page to registration
            /*if (bind.rememberMeCheck.isChecked && email == sharedPreferences.getString("email","")){
                intent.putExtra("email", email)
                startActivity(intent)
            }else {
                startActivity(intent)
            }*/
            startActivity(intent)
        }
    }
    private fun loginRequest(email : String,password : String, token: Int) {
        val url = "http://10.129.90.217/fishfinder/login.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.e("response:", response)
                if (response.trim() == "success") {
                    //intent to logged in activity goes in here
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // save the userdata to the phone so they can be retrieved on next open if user selected remember me
                    with(sharedPreferences.edit()) {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("rememberMe", bind.rememberMeCheck.isChecked)
                        apply()
                    }

                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                params["token"] = token.toString()
                return params
            }
            // passing data
        }
        Log.e("login request", stringRequest.toString())
        requestQueue.add(stringRequest)
    }

    private fun tokenCheck(token: Int) {
        val url = "http://10.129.90.217/fishfinder/token.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.e("response:",response)
                if (response.trim() == "success") {
                    Log.e("token","succeeded in token check")
                } else {
                    Log.e("token","failed in token check")
                }
            }, Response.ErrorListener { error ->
                Log.e("token", "error happened /_O_/")
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): HashMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = token.toString()
                return params
            }
            // passing data
        }
        requestQueue.add(stringRequest)
    }

}