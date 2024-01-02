package com.girls.HotRest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.FileAsyncHttpResponseHandler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.girls.HotRest.databinding.ActivityPhotoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.File


class Photo_activity : AppCompatActivity() {
    lateinit var bind: ActivityPhotoBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(bind.root)

        var isPro: Boolean = false
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(currentUser!!.uid)


        val photo = intent.getStringExtra("imageUrl")
        val styleName = intent.getStringExtra("styleName")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    isPro = document.getBoolean("isPro")!!
                    Log.i("isPro", isPro.toString())
                    if (isPro != false){
                        bind.Download.visibility = View.GONE
                        bind.textView13.visibility = View.GONE
                        bind.textView32.visibility = View.GONE
                        bind.switch1.visibility = View.GONE
                        Picasso.get().load(photo).into(bind.Photo)
                    }else{
                        addWatermarkWithGlide(photo!!, R.drawable.watermark, bind.Photo)
                    }
                } else {
                    Log.d("FirestoreData", "Документ не найден")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
            }


        bind.textView10.text = "Style: $styleName"

        bind.more.setOnClickListener {
            bind.moreImage.visibility = View.VISIBLE
        }
        bind.cancel.setOnClickListener {
            bind.moreImage.visibility = View.INVISIBLE

        }

        bind.goToHome.setOnClickListener {
            finish()
        }
        bind.textView31.setOnClickListener {
            finish()
        }

        bind.imageButton6.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.girls.HotRest"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        bind.switch1.setOnClickListener {
            bind.switch1.isChecked = false
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val preload = pro_screen()

            fragmentTransaction.replace(R.id.photoFragment, preload)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        bind.downloadHD.setOnClickListener {
            if (isPro == false) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.photoFragment, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }else{
                saveImageToGallery()
            }
        }

        bind.Download.setOnClickListener {
            saveImageToGallery()

        }
        bind.Share.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, photo)
                    type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share with"))
        }

    }



    fun saveImageToGallery() {
        val imageView = bind.Photo // Ваш ImageView

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(imageView.drawingCache)
        imageView.isDrawingCacheEnabled = false

        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Image Title",
            "Image Description"
        )

        if (savedImageURL != null) {
            Toast.makeText(
                this@Photo_activity,
                "Image downloaded and saved!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@Photo_activity,
                "Failed to download image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun addWatermarkWithGlide(imageUrl: String, watermarkResource: Int, imageView: ImageView) {
        Glide.with(imageView)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val watermark = BitmapFactory.decodeResource(resources, watermarkResource)

                    // Уменьшаем размер водяного знака до 50%
                    val scaledWatermark = Bitmap.createScaledBitmap(watermark, watermark.width, watermark.height, true)

                    val resultBitmap = Bitmap.createBitmap(resource.width, resource.height, resource.config)
                    val canvas = Canvas(resultBitmap)
                    canvas.drawBitmap(resource, 0f, 0f, null)

                    val margin = 50f
                    val left = resource.width - scaledWatermark.width - margin
                    val top = resource.height - scaledWatermark.height - margin
                    canvas.drawBitmap(scaledWatermark, left.toFloat(), top.toFloat(), null)

                    imageView.setImageBitmap(resultBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Выполнить какие-либо действия при сбросе загрузки
                }
            })
    }




    override fun onBackPressed() {
            super.onBackPressed()
            finish()
        }

}