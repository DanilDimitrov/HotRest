package com.girls.HotRest

import android.content.Intent
import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.girls.HotRest.databinding.ActivityGeneratePeocessBinding
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class generate_peocess : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    var count = 0

    lateinit var bind: ActivityGeneratePeocessBinding

    override fun onResume() {
        super.onResume()
        playVideo(count)
    }

    fun playVideo(count: Int){
        var videoPath = ""
        if(count == 0){
            videoPath = "android.resource://" + packageName + "/" + R.raw.loading4
        }else if(count == 1){
            videoPath = "android.resource://" + packageName + "/" + R.raw.loading5

        }
        else if(count == 2){
            videoPath = "android.resource://" + packageName + "/" + R.raw.loading6

        }else{
            videoPath = "android.resource://" + packageName + "/" + R.raw.loading8
        }


        bind.videoView.setVideoURI(Uri.parse(videoPath)) // Установите URI видео в VideoView

        bind.videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            bind.videoView.start()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityGeneratePeocessBinding.inflate(layoutInflater)
        setContentView(bind.root)

        auth = Firebase.auth
        currentUser = auth.currentUser!!

         count = (0..4).random()
        playVideo(count)

        val json = intent.getStringExtra("json")
        val isAnime = intent.getBooleanExtra("isAnime", false)
        val rasa = intent.getStringExtra("rasa")
        val gallery = intent.getBooleanExtra("gallery", false)


        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("keys").document("txt2img")

        fun createImageWithRetry(json: String, apiUrl: String, callback: (String) -> Unit) {


            val handler = Handler(Looper.getMainLooper())

            documentReference.get()
                .addOnSuccessListener { api ->
                    val apiKey = api.getString("api").toString()

                    Log.i("apiKey", apiKey.toString())
                    fun checkStatus(id: Int) {
                        Fuel.post("https://stablediffusionapi.com/api/v3/fetch/$id")
                            .header("Content-Type" to "application/json")
                            .timeout(20000)
                            .jsonBody("{\"key\": $apiKey}")
                            .response { _, _, result ->
                                if (!isDestroyed) {
                                    lifecycleScope.launch {
                                        progress(bind.progressBar4, 60)
                                    }

                                    when (result) {
                                        is Result.Failure -> {
                                            val e = result.error.exception
                                            Log.e("error", "API FAILED", e)
                                            handler.postDelayed({ checkStatus(id) }, 3000)
                                        }
                                        is Result.Success -> {
                                            val body = String(result.value)
                                            val jsonObject = JSONObject(body)
                                            Log.i("jsonObject", jsonObject.toString())
                                            val status = jsonObject.optString("status")
                                            lifecycleScope.launch {
                                                progress(bind.progressBar4, 70)
                                            }

                                            when (status) {
                                                "success" -> {
                                                    val output = jsonObject.optJSONArray("output")
                                                    if (output?.isNull(0) == false) {
                                                        val imageUrl = output.getString(0)
                                                        callback(imageUrl)
                                                        lifecycleScope.launch {
                                                            progress(bind.progressBar4, 100)
                                                        }
                                                    }
                                                }
                                                "processing" -> {
                                                    handler.postDelayed({ checkStatus(id) }, 3000)
                                                    Log.i("Process", status.toString())
                                                    lifecycleScope.launch {
                                                        progress(bind.progressBar4, 80)
                                                    }
                                                }
                                                else -> {
                                                    if(isAnime == false){

                                                        createImageWithRetry(
                                                            json, "https://stablediffusionapi.com/api/v3/text2img") { imageUrl ->

                                                            val toImageIntent = Intent(this, Photo_activity::class.java)
                                                            toImageIntent.putExtra("imageUrl", imageUrl)
                                                            toImageIntent.putExtra("styleName", rasa)
                                                            Log.i("rasa", rasa.toString())
                                                            startActivity(toImageIntent)
                                                            finish()

                                                            Log.i("imageUrl", imageUrl.toString())

                                                        }
                                                    }else{
                                                        createImageWithRetry(
                                                            json, "https://stablediffusionapi.com/api/v4/dreambooth") { imageUrl ->
                                                            val toImageIntent = Intent(this, Photo_activity::class.java)
                                                            toImageIntent.putExtra("imageUrl", imageUrl)
                                                            toImageIntent.putExtra("styleName", rasa)
                                                            Log.i("rasa", rasa.toString())
                                                            startActivity(toImageIntent)
                                                            finish()

                                                            Log.i("imageUrl", imageUrl.toString())

                                                        }
                                                    }
                                                    Log.i("Process", status.toString())
                                                    lifecycleScope.launch {
                                                        progress(bind.progressBar4, 80)
                                                    }
                                                }
                                            }
                                        }
                                    }}
                            }
                    }
                    fun sendRequest() {
                        Fuel.post(apiUrl)
                            .header("Content-Type" to "application/json")
                            .timeout(30000)
                            .jsonBody(json.trimIndent())
                            .response { _, response, result ->
                                if (!isDestroyed) {
                                    lifecycleScope.launch {
                                        progress(bind.progressBar4, 30)
                                    }

                                    when (result) {
                                        is Result.Failure -> {
                                            val e = result.error.exception
                                            Log.e("error", "API FAILED", e)

                                            handler.postDelayed({ sendRequest() }, 1000)
                                        }
                                        is Result.Success -> {
                                            val body = String(response.data)

                                            val jsonObject = JSONObject(body)
                                            Log.i("jsonObject", jsonObject.toString())
                                            Log.i("json", json.toString())
                                            lifecycleScope.launch {
                                                progress(bind.progressBar4, 40)
                                            }

                                            val status = jsonObject.optString("status")

                                            when (status) {
                                                "success" -> {
                                                    val output = jsonObject.optJSONArray("output")
                                                    if (output?.isNull(0) == false) {
                                                        val imageUrl = output.getString(0)
                                                        callback(imageUrl)
                                                        lifecycleScope.launch {
                                                            progress(bind.progressBar4, 100)
                                                        }
                                                    } else {
                                                    }
                                                }
                                                "processing" -> {
                                                    // Если статус "processing", получаем id и продолжаем проверку
                                                    val id = jsonObject.optInt("id")
                                                    lifecycleScope.launch {
                                                        progress(bind.progressBar4, 50)
                                                    }
                                                    checkStatus(id)
                                                    Log.i("Process", status.toString())
                                                }
                                                else -> {
                                                    handler.postDelayed({ sendRequest() }, 3000)
                                                    Log.i("Process", status.toString())
                                                }
                                            }
                                        }
                                    }
                                }
                            }}

                    sendRequest()
                }}

        if(isAnime == false){

            createImageWithRetry(
                json!!, "https://stablediffusionapi.com/api/v3/text2img") { imageUrl ->

            val toImageIntent = Intent(this, Photo_activity::class.java)
            toImageIntent.putExtra("imageUrl", imageUrl)
                val userRef = db.collection("Users").document(currentUser.uid)
                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val isPro = document.getBoolean("isPro")!!
                            if(!isPro || !gallery){
                                toImageIntent.putExtra("styleName", rasa)
                                Log.i("rasa", rasa.toString())
                                startActivity(toImageIntent)
                                finish()

                            }else{
                                userRef.update(
                                    "imagesUrls",
                                    FieldValue.arrayUnion(imageUrl)
                                )
                                    .addOnSuccessListener {
                                        Log.i("imagesUrls", imageUrl)
                                        toImageIntent.putExtra("styleName", rasa)
                                        Log.i("rasa", rasa.toString())
                                        startActivity(toImageIntent)
                                        finish()
                                    }
                                    .addOnFailureListener {

                                    }


                            }
                        } else {
                            Log.d("FirestoreData", "Документ не найден")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                    }

                Log.i("imageUrl", imageUrl.toString())

            }
        }else{
            createImageWithRetry(
                json!!, "https://stablediffusionapi.com/api/v4/dreambooth") { imageUrl ->
                val toImageIntent = Intent(this, Photo_activity::class.java)
                toImageIntent.putExtra("imageUrl", imageUrl)
                val userRef = db.collection("Users").document(currentUser.uid)
                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val isPro = document.getBoolean("isPro")!!
                            if(!isPro || !gallery){
                                toImageIntent.putExtra("styleName", rasa)
                                Log.i("rasa", rasa.toString())
                                startActivity(toImageIntent)
                                finish()

                            }else{
                                    userRef.update(
                                        "imagesUrls",
                                        FieldValue.arrayUnion(imageUrl)
                                    )
                                        .addOnSuccessListener {
                                            Log.i("imagesUrls", imageUrl)
                                            toImageIntent.putExtra("styleName", rasa)
                                            Log.i("rasa", rasa.toString())
                                            startActivity(toImageIntent)
                                            finish()
                                        }
                                        .addOnFailureListener {

                                        }

                                }

                        } else {
                            Log.d("FirestoreData", "Документ не найден")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                    }

                Log.i("imageUrl", imageUrl.toString())

                Log.i("imageUrl", imageUrl.toString())

            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        isDestroyed
        finish()
    }
    suspend fun progress(progressBar: ProgressBar, steps: Int) {
        try {
            for (i in progressBar.progress..steps) {
                delay(15)
                progressBar.progress = i
            }
        } catch (e: Exception) {
            Log.e("progress_ERROR", e.toString())
        }
    }


}