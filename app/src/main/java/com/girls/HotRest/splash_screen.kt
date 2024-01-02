package com.girls.HotRest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.girls.HotRest.databinding.SplashScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import manage

class splash_screen : AppCompatActivity() {
    private lateinit var bind : SplashScreenBinding
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SplashScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val uiIntarface = UI()
        val db = FirebaseFirestore.getInstance()
        val toMainActivity = Intent(this, MainActivity::class.java)
        auth = Firebase.auth
        user = auth.currentUser

        if(user != null){
            val userDoc = db.collection("Users").document(user!!.uid)
            userDoc.get()
                .addOnSuccessListener { documentSnapshot ->
                    val currentTime = Timestamp.now()
                    val time = documentSnapshot.getTimestamp("time")

                    if (time != null) {
                        if(time.seconds < currentTime.seconds){
                            userDoc.update("isPro", false)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Now you are not a Pro user", Toast.LENGTH_LONG).show()
                                    Log.i("USER INFORMATION", "Data written successfully")
                                }
                                .addOnFailureListener {
                                    Log.e("USER INFORMATION", "Failed to write data")
                                }
                        }
                    }
                }
        }

        // UI
        bind.textView.paint?.shader = uiIntarface.textApp(bind.textView, "AI REALISTIC GIRL GENERATOR")
        lifecycleScope.launch {
            progress(bind.progressBar, 100, toMainActivity)
        }


    }

    suspend fun progress(progressBar: ProgressBar, steps: Int, intent: Intent) {
        val manage = manage()
        try {
            val styles = manage.getAllStyles()
            Log.i("stylesProgress", styles.toString())

            for (i in 0..steps) {
                delay(1)
                progressBar.progress = i
                if (i == 100) {
                    intent.putExtra("styles", styles)
                    startActivity(intent)
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.e("progress_ERROR", e.toString())
        }
    }


}