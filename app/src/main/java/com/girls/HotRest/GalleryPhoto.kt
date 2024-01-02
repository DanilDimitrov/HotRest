package com.girls.HotRest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.girls.HotRest.databinding.ActivityGalleryPhotoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.FileAsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import java.io.File

class GalleryPhoto : AppCompatActivity() {
    lateinit var bind: ActivityGalleryPhotoBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityGalleryPhotoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        auth = Firebase.auth
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()


        val photo = intent.getStringExtra("photo")!!
        Picasso.get().load(photo).into(bind.Photo)


        bind.imageButton10.setOnClickListener {
            bind.moreImage.visibility = View.VISIBLE
        }

        bind.cancel.setOnClickListener {
            bind.moreImage.visibility = View.INVISIBLE
        }

        bind.imageButton15.setOnClickListener {
            finish()
        }

        bind.SharePhoto.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, photo)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share with"))
        }
        bind.Save.setOnClickListener {
            saveImageToGallery(photo)
        }

        bind.Delete.setOnClickListener {
            if(user!= null) {
                val userRef = db.collection("Users").document(user.uid)
                userRef.get().addOnSuccessListener { documentSnapshot ->
                        val imagesUrls = documentSnapshot.get("imagesUrls") as ArrayList<String>?

                        imagesUrls?.remove(photo)

                        userRef.update("imagesUrls", imagesUrls)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@GalleryPhoto,
                                    "Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@GalleryPhoto,
                                    "Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

            }
        }
    }
    fun saveImageToGallery(imageUrl: String) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val client = AsyncHttpClient()
        client.get(imageUrl, object : FileAsyncHttpResponseHandler(directory) {

            override fun onSuccess(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                file: File?
            ) {
                Toast.makeText(
                    this@GalleryPhoto,
                    "Image downloaded and saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onFailure(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                throwable: Throwable?,
                file: File?
            ) {
                // Уведомляем пользователя о неудачной загрузке
                Toast.makeText(
                    this@GalleryPhoto,
                    "Failed to download image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}