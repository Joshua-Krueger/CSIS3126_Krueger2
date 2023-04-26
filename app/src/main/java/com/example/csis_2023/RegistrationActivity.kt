package com.example.csis_2023

import android.app.Dialog
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.csis_2023.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var bind : ActivityRegistrationBinding
    private lateinit var helpDialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(bind.root)
//todo work on all the toast messages
//todo fix the thing in here that's supposed to send the user back to the login page when they register with the email already filled out
        bind.helpButtonRegistration.setOnClickListener{
            helpDialog = Dialog(this)
            helpDialog.setContentView(R.layout.help_overlay)

            val helpTitle = helpDialog.findViewById<TextView>(R.id.titleHelp)
            helpTitle.text = "Registration Help"

            val closeButton = helpDialog.findViewById<Button>(R.id.closeHelpButton)

            val helpMessage = helpDialog.findViewById<TextView>(R.id.messageHelp)
            helpMessage.text = "Full Name: Please enter your first and last name(I.E. John Smith)\nEmail: Please enter your email(I.E. Jsmith@gmail.com)\nPassword: Please enter a password that has the following:\n    A capital letter(I.E. A,B,C,D,E,...ETC)\n    A lowercase letter(I.E. a,b,c,d,e,...etc)\n    A special character(I.E. !,@,#,$,%,...etc)\n    A number(I.E. 1,2,3,4,5,6,...etc)\n    At least 8 characters\n    I.E. J0hnSm1th!\nThen click the register button!\n\nIf you need to go back to the login page, click the back button at the top!"

            closeButton.setOnClickListener {
                helpDialog.dismiss()
            }

            helpDialog.show()
        }

        // this allows me to retrieve the email that the user tried to login with if they try to register an account
        bind.RegistrationEmail.setText(intent.getStringExtra("email"))

        bind.backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        bind.RegisterBtn.setOnClickListener {
            Log.d("RegistrationActivity","register button was clicked")
            val name = bind.registrationName.text.toString()
            val email = bind.RegistrationEmail.text.toString()
            val password = bind.RegistrationPass.text.toString()
            val passCheck = bind.RegistrationPassCheck.text.toString()
            var message = ""

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passCheck.isNotEmpty()){
                // is the information good
                message += checkData(password, passCheck, email)
                if (message == ""){
                    registerRequest(name, email, password)
                }else{
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            }
        }
    }
//--------------------------------------------------------------------------------------------------\\
    private fun registerRequest(name : String, email : String, password : String) {
        val url = "http://10.129.17.5/fishfinder/register.php"

        //val url = "http://192.168.1.154/fishfinder/register.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.e("register response: ",response)
                if (response.trim() == "success") {
                    //intent to logged in activity goes in here
                    Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email",email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                Log.e("error message", error.message.toString())
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["email"] = email
                params["password"] = password
                return params
            }
            // passing data
        }
        requestQueue.add(stringRequest)
    }
//--------------------------------------------------------------------------------------------------\\
    private fun checkData(password: String, passCheck : String, email : String) : String{
        var message = ""
        var hasUpper = false
        var hasNum = false
        var hasSpecial = false
        var hasLower = false

        // does the email have a valid format?
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            message += "Please enter a valid email\n"
        }
        if (password.length < 8){
            message += "Password must be at least 8 characters\n"
        }
        for (x in password){
            Log.d("RegistrationActivity",x.toString())
            // is there an upper case letter?
            Log.d("RegistrationActivity","is it upper case: " + (x.isUpperCase()).toString())
            if (x.isUpperCase()){
                hasUpper = true
            }else{
                Log.d("RegistrationActivity","is it lower case: " + (x.isLowerCase()).toString())
                if (x.isLowerCase()){
                    hasLower = true
                }else{
                    Log.d("RegistrationActivity","is it a digit: " + x.isDigit().toString())
                    if (x.isDigit()){
                        hasNum = true
                    }else{
                        Log.d("RegistrationActivity","is it a special character: " + (!x.isLetterOrDigit()).toString())
                        if (!x.isLetterOrDigit()) {
                            hasSpecial = true
                        }
                    }
                }
            }
        }
    if (!hasUpper){
        message += "Password must have a letter in upper case\n"
    }
    if (!hasLower){
        message += "Password must have a letter in lower case\n"
    }
    if (!hasNum){
        message += "Password must have a number\n"
    }
    if (!hasSpecial){
        message += "Password must have a special character\n"
    }
    // do the passwords match
    if (password != passCheck){
        message += "Passwords must match\n"
    }
    return message
    }
}