package com.girls.HotRest

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.girls.HotRest.databinding.ActivitySettingsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class settings : AppCompatActivity() {
    lateinit var bind: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    var isPro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        if(currentUser!= null){
            bind.cardView9.visibility = View.GONE
            val userRef = db.collection("Users").document(currentUser.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        isPro = document.getBoolean("isPro")!!

                            if (isPro == false) {
                                bind.constraint.setBackgroundResource(R.drawable.get_pro)
                                bind.imageView11.visibility = View.GONE
                                bind.textView31.text = getString(R.string.GetPRO)

                            } else if(isPro){
                            }
                        } else {
                            Log.d("FirestoreData", "Документ не найден")
                        }

                }
                .addOnFailureListener { exception ->
                    Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                }
        }else{
            bind.singoutCard.visibility = View.GONE
            bind.constraint.setBackgroundResource(R.drawable.get_pro)
            bind.imageView11.visibility = View.GONE
            bind.textView31.text = "Get PRO"
        }

        bind.cardView13.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.girls.HotRest"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        bind.cardView8.setOnClickListener {
            if (isPro == false) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.settingPro, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        bind.singoutCard.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            finish()
        }
        bind.cardView9.setOnClickListener {
            val toLogin = Intent(this, sign_up::class.java)
            startActivity(toLogin)
        }
        bind.cardView10.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "Some thing went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        bind.cardView11.setOnClickListener {
            sendEmail("support@reimageapp.ai", "RESTORE PURCHASE HOTREST", "I want to restore my purchase")
        }
        bind.cardView14.setOnClickListener {
            sendEmail("support@reimageapp.ai", "HELP SUPPORT HOTREST", "I need a help")
        }

        bind.cardView16.setOnClickListener {
            val url = "https://reimageapp.ai/hotrest_privacy-policy" // ваша ссылка
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        bind.cardView17.setOnClickListener {
            val url = "https://reimageapp.ai/Terms_Of_Use-hotrest" // ваша ссылка
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        bind.goToHome.setOnClickListener {
            finish()
        }


        bind.languageCard.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
    }

    fun sendEmail( email: String, subject: String, body: String) {
        // Создание интента для отправки email
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.setPackage("com.google.android.gm") // Указание пакета приложения Gmail

        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "Please install Gmail", Toast.LENGTH_SHORT).show()
        }
    }
}