package com.girls.HotRest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.girls.HotRest.databinding.ActivityGalleryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class Gallery : AppCompatActivity() {
    lateinit var bind : ActivityGalleryBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        var isPro = false

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        if(currentUser!= null){
            val userRef = db.collection("Users").document(currentUser.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        isPro = document.getBoolean("isPro")!!
                        val imagesUrls = document.get("imagesUrls") as ArrayList<String>?
                        val listOfUrls = ArrayList<String>()

                        if (imagesUrls != null) {
                            listOfUrls.addAll(imagesUrls)
                            if (isPro == false) {
                                bind.button7.visibility = View.VISIBLE
                                bind.button8.visibility = View.INVISIBLE
                                bind.recycler.visibility = View.INVISIBLE

                            } else if(isPro && listOfUrls.isNotEmpty()){
                                Log.d("listOfUrls", listOfUrls.toString())

                                bind.button7.visibility = View.INVISIBLE
                                bind.button8.visibility = View.INVISIBLE
                                bind.constraintLayout.visibility = View.INVISIBLE
                                bind.recycler.visibility = View.VISIBLE
                                val adapter = galleryAdapter(listOfUrls)
                                bind.recycler.layoutManager = GridLayoutManager(this, 2)
                                bind.recycler.adapter = adapter

                                adapter.setOnItemClickListener { url->
                                    val toPhoto = Intent(this, GalleryPhoto::class.java)
                                    toPhoto.putExtra("photo", url)
                                    startActivity(toPhoto)
                                }
                            }else{
                                bind.button7.visibility = View.INVISIBLE
                                bind.button8.visibility = View.VISIBLE
                                bind.constraintLayout.visibility = View.VISIBLE
                                bind.recycler.visibility = View.INVISIBLE
                            }
                        } else {
                            Log.d("FirestoreData", "Документ не найден")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                }
        }else{
            bind.button7.visibility = View.VISIBLE
            bind.button8.visibility = View.INVISIBLE
            bind.recycler.visibility = View.INVISIBLE
        }


        bind.button7.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val preload = pro_screen()

            fragmentTransaction.replace(R.id.galleryPro, preload)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        bind.button8.setOnClickListener {
            finish()
        }


        bind.imageButton15.setOnClickListener {
            finish()
        }

        bind.imageButton10.setOnClickListener {
            val tosettings = Intent(this, settings::class.java)
            finish()
            startActivity(tosettings)

        }
    }
}