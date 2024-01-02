package com.girls.HotRest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.girls.HotRest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val styles: ArrayList<Model>? = intent.getSerializableExtra("styles") as? ArrayList<Model>
        Log.i("styles", styles?.size.toString())

        val europeanList: ArrayList<Model> = ArrayList(styles?.filter { it.collection == "european" })
        val asianList: ArrayList<Model> = ArrayList(styles?.filter { it.collection == "asian" })
        val animeList: ArrayList<Model> = ArrayList(styles?.filter { it.collection == "anime" })
        val blackList: ArrayList<Model> = ArrayList(styles?.filter { it.collection == "black" })
        val latinaList: ArrayList<Model> = ArrayList(styles?.filter { it.collection == "latina" })

        binding.imageButton6.setOnClickListener {
            val toProfile = Intent(this, Gallery::class.java)
            startActivity(toProfile)
        }

        binding.cardView.setOnClickListener {
            val toGenerate_Screen = Intent(this, generate_screen::class.java)
            toGenerate_Screen.putExtra("styles", europeanList)
            startActivity(toGenerate_Screen)
        }
        binding.asian.setOnClickListener {
            val toGenerate_Screen = Intent(this, generate_screen::class.java)
            toGenerate_Screen.putExtra("styles", asianList)
            startActivity(toGenerate_Screen)
        }
        binding.anime.setOnClickListener {
            val toGenerate_Screen = Intent(this, generate_screen::class.java)
            toGenerate_Screen.putExtra("styles", animeList)
            toGenerate_Screen.putExtra("anime", "anime")
            startActivity(toGenerate_Screen)
        }
        binding.black.setOnClickListener {
            val toGenerate_Screen = Intent(this, generate_screen::class.java)
            toGenerate_Screen.putExtra("styles", blackList)
            startActivity(toGenerate_Screen)
        }
        binding.latina.setOnClickListener {
            val toGenerate_Screen = Intent(this, generate_screen::class.java)
            toGenerate_Screen.putExtra("styles", latinaList)
            startActivity(toGenerate_Screen)
        }

        binding.button3.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val preload = pro_screen()

            fragmentTransaction.replace(R.id.frameLayout, preload)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val isFragmentViewed = sharedPref.getBoolean("isFragmentViewed", false)
        if (isFragmentViewed) {

        } else {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val preload = preload()

            fragmentTransaction.replace(R.id.frameLayout, preload)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }




    }

}