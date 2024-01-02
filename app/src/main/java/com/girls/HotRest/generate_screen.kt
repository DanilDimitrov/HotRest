package com.girls.HotRest

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.girls.HotRest.databinding.ActivityGenerateScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class generate_screen : AppCompatActivity() {
    lateinit var bind: ActivityGenerateScreenBinding
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    lateinit var model: Model
    var isPro: Boolean = false
    var attempts = 0
    var images: ArrayList<String> = ArrayList<String>()
    lateinit var styles: ArrayList<Model>
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityGenerateScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)
        auth = Firebase.auth
        var width = 0
        var height = 0
        var sizeForGeneration = ""

        val isAnime = intent.getStringExtra("anime")
        var apiValue: String = ""
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("keys").document("txt2img")

        styles = intent.getSerializableExtra("styles") as ArrayList<Model>
        Log.i("generate_styles", styles.size.toString())
        currentUser = auth.currentUser
        if(currentUser == null){
            isPro = false
            UpdateUI(isPro, styles)
        }else{
            val userRef = db.collection("Users").document(currentUser!!.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        isPro = document.getBoolean("isPro")!!
                        attempts = document.get("attempt")!!.toString().toInt()
                        val listOfUrls = document.get("imagesUrls")!! as ArrayList<String>
                        images = ArrayList<String>()
                        images.addAll(listOfUrls)

                        Log.i("isPro", isPro.toString())
                        UpdateUI(isPro, styles)
                    } else {
                        Log.d("FirestoreData", "Документ не найден")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                }
        }

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    apiValue = document.getString("api").toString()
                    Log.i("key", apiValue)
                } else {
                    Log.d("FirestoreData", "Документ не найден")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
            }
        Log.i("key", apiValue)



        val inspireMeList = arrayListOf<String>("cute girl", "beautiful girl", "brown hair girl", "office asia lady", "smile girl on beach", "bikini girl", "anime girl",
            "cyberpunk girl", "doll girl", "european cute girl ", "school girl", "girl on the beach", "yoga sitting", "girl 18 years",
            "barbie", "girl on a horse", "cute superhero girl", "girl with tattoos", "asian blonde girl", "girl fitness")

        if(isAnime == "anime"){
            bind.b.visibility = View.GONE
            bind.la.visibility = View.GONE
            bind.eu.visibility = View.GONE
            bind.asian.visibility = View.GONE
            bind.textView26.visibility = View.GONE
            bind.textView27.visibility = View.GONE
        }

        when(styles?.get(0)?.collection){
            "european" -> runOnUiThread { bind.textView21.text = "European girl" }
            "asian" -> runOnUiThread { bind.textView21.text = "Asian girl" }
            "anime" -> runOnUiThread { bind.textView21.text = "Anime girl" }
            "black" -> runOnUiThread { bind.textView21.text = "Black girl" }
            "latina" -> runOnUiThread { bind.textView21.text = "Latina girl" }
        }

        bind.b.setOnClickListener {
            bind.prompt.setText("Black girl, " + bind.prompt.text.toString())
        }
        bind.la.setOnClickListener {
            if(isPro != false) {
                bind.prompt.setText("Latina girl, " + bind.prompt.text.toString())
            }
            else{
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.forPro, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

        }
        bind.eu.setOnClickListener {
            if(isPro != false) {
                bind.prompt.setText("European girl, " + bind.prompt.text.toString())
            }
            else{
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.forPro, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
        bind.asian.setOnClickListener {
            if(isPro != false) {
                bind.prompt.setText("Latina girl, " + bind.prompt.text.toString())
            }
            else{
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.forPro, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        val inspireMe = inspire_me_adapter(inspireMeList)
        bind.inspireMe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bind.inspireMe.adapter = inspireMe

        inspireMe.setOnItemClickListener { prompt->
            runOnUiThread {bind.prompt.setText(bind.prompt.text.toString() + " , " + prompt)}
        }


        fun selectSize() {
            bind.apply {

                runOnUiThread {
                    button4.setBackgroundResource(R.drawable.gradient)
                    val shader = LinearGradient(
                        0f, 0f, 0f, bind.button4.textSize,
                        intArrayOf(Color.parseColor("#ffffff"),Color.parseColor("#ffffff")), null,
                        Shader.TileMode.CLAMP
                    )

                    val paint =  bind.button4.paint
                    paint.shader = shader
                    bind.button4.invalidate()
                    if (size11.isChecked) {
                        size2.isChecked = false
                        size34.isChecked = false
                        size43.isChecked = false
                        size23.isChecked = false
                        width = 512
                        height = 512
                        size11.setBackgroundResource(R.drawable.radio_back_select)
                        size2.setBackgroundResource(R.drawable.unselect_size)
                        size34.setBackgroundResource(R.drawable.unselect_size)
                        size43.setBackgroundResource(R.drawable.unselect_size)
                        size23.setBackgroundResource(R.drawable.unselect_size)
                        sizeForGeneration = size11.text.toString()
                        Log.i("sizeForGeneration", sizeForGeneration)
                    } else if (size2.isChecked) {
                        size11.isChecked = false
                        size34.isChecked = false
                        size43.isChecked = false
                        size23.isChecked = false
                        width = 768
                        height = 512
                        size11.setBackgroundResource(R.drawable.unselect_size)
                        size2.setBackgroundResource(R.drawable.radio_back_select)
                        size34.setBackgroundResource(R.drawable.unselect_size)
                        size43.setBackgroundResource(R.drawable.unselect_size)
                        size23.setBackgroundResource(R.drawable.unselect_size)
                        sizeForGeneration = size2.text.toString()
                        Log.i("sizeForGeneration", sizeForGeneration)


                    } else if (size34.isChecked) {
                        size2.isChecked = false
                        size11.isChecked = false
                        size43.isChecked = false
                        size23.isChecked = false

                        width = 768
                        height = 1024
                        size11.setBackgroundResource(R.drawable.unselect_size)
                        size2.setBackgroundResource(R.drawable.unselect_size)
                        size34.setBackgroundResource(R.drawable.radio_back_select)
                        size43.setBackgroundResource(R.drawable.unselect_size)
                        size23.setBackgroundResource(R.drawable.unselect_size)
                        sizeForGeneration = size34.text.toString()
                        Log.i("sizeForGeneration", sizeForGeneration)

                    } else if (size43.isChecked) {
                        size2.isChecked = false
                        size34.isChecked = false
                        size11.isChecked = false
                        size23.isChecked = false

                        width = 1024
                        height = 768
                        size11.setBackgroundResource(R.drawable.unselect_size)
                        size2.setBackgroundResource(R.drawable.unselect_size)
                        size34.setBackgroundResource(R.drawable.unselect_size)
                        size43.setBackgroundResource(R.drawable.radio_back_select)
                        size23.setBackgroundResource(R.drawable.unselect_size)
                        sizeForGeneration = size43.text.toString()
                        Log.i("sizeForGeneration", sizeForGeneration)

                    } else if (size23.isChecked) {
                        size2.isChecked = false
                        size34.isChecked = false
                        size43.isChecked = false
                        size11.isChecked = false

                        width = 512
                        height = 768
                        size11.setBackgroundResource(R.drawable.unselect_size)
                        size2.setBackgroundResource(R.drawable.unselect_size)
                        size34.setBackgroundResource(R.drawable.unselect_size)
                        size43.setBackgroundResource(R.drawable.unselect_size)
                        size23.setBackgroundResource(R.drawable.radio_back_select)
                        sizeForGeneration = size23.text.toString()
                        Log.i("sizeForGeneration", sizeForGeneration)

                    }
                }
            }
        }
        bind.size11.setOnClickListener {
            bind.size11.isChecked = true
            bind.size23.isChecked = false
            bind.size2.isChecked = false
            bind.size43.isChecked = false
            bind.size34.isChecked = false
            selectSize() }
        bind.size2.setOnClickListener {
            bind.size11.isChecked = false
            bind.size23.isChecked = false
            bind.size2.isChecked = true
            bind.size43.isChecked = false
            bind.size34.isChecked = false
            selectSize() }
        bind.size23.setOnClickListener {
            bind.size11.isChecked = false
            bind.size23.isChecked = true
            bind.size2.isChecked = false
            bind.size43.isChecked = false
            bind.size34.isChecked = false
            selectSize() }
        bind.size43.setOnClickListener {
            bind.size11.isChecked = false
            bind.size23.isChecked = false
            bind.size2.isChecked = false
            bind.size43.isChecked = true
            bind.size34.isChecked = false
            selectSize() }
        bind.size34.setOnClickListener {
            bind.size11.isChecked = false
            bind.size23.isChecked = false
            bind.size2.isChecked = false
            bind.size43.isChecked = false
            bind.size34.isChecked = true
            selectSize()
        }

        val shader = LinearGradient(
            0f, 0f, 0f, bind.button4.textSize,
            intArrayOf(Color.parseColor("#bf5af2"),
                Color.parseColor("#ff2d55"),
                Color.parseColor("#ff9f0a")), null,
            Shader.TileMode.CLAMP
        )

        val paint =  bind.button4.paint
        paint.shader = shader
        bind.button4.invalidate()

        bind.imageButton7.setOnClickListener {
            finish()
        }
        bind.textView21.setOnClickListener {
            finish()
        }


        bind.button4.setOnClickListener {
            if(currentUser == null){
                val tosign_up = Intent(this, sign_up::class.java)
                startActivity(tosign_up)
            }else {
                if (attempts > 0 || isPro) {
                    if (images.size > 9) {
                        val dialog = Dialog(this)
                        dialog.setContentView(R.layout.alert_galerry_full)
                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


                        val cancel = dialog.findViewById<TextView>(R.id.CancelGallery)
                        val ok = dialog.findViewById<TextView>(R.id.OkGallery)

                        cancel.setOnClickListener {
                            val fragmentManager = supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            val preload = pro_screen()

                            fragmentTransaction.replace(R.id.forPro, preload)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                            dialog.dismiss()
                        }
                        ok.setOnClickListener {
                            dialog.dismiss()
                            Log.i("images", images.size.toString())

                            val userRef = db.collection("Users").document(currentUser!!.uid)
                            if (sizeForGeneration != "" && isAnime == null && bind.prompt.text.toString() != "") {
                                val json = """
                      {
                        "key": "$apiValue",
                        "prompt": "${
                                    bind.prompt.text.toString().replace("girl", "girlfriend", true)
                                        .replace("school", "university", true)
                                }",
    "negative_prompt": "nsfw, [deformed | disfigured], poorly drawn, [bad : wrong] anatomy,  [extra | missing | floating | disconnected ] limb, (mutated hands and fingers and legs), blurry, nsfw, glare",
                        "width": "$width",
                        "height": "$height",
                        "samples": "1",
  "num_inference_steps": "25",
  "safety_checker": "no",
  "enhance_prompt": "yes",
  "seed": null,
  "guidance_scale": 7.5,
  "multi_lingual": "yes",
  "panorama": "no",
  "self_attention": "no",
  "upscale": "no",
  "embeddings_model": null,
  "webhook": null,
  "track_id": null
                      }
                    """.trimIndent()
                                val toGenerate = Intent(this, generate_peocess::class.java)
                                toGenerate.putExtra("json", json)
                                toGenerate.putExtra("rasa", bind.textView21.text.toString())
                                toGenerate.putExtra("gallery", false)
                                Log.i("json", json.toString())
                                if (isPro) {
                                    startActivity(toGenerate)
                                } else {
                                    userRef.update("attempt", attempts - 1)
                                        .addOnSuccessListener {
                                            startActivity(toGenerate)
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(
                                                this,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }


                            } else if (sizeForGeneration != "" && isAnime == "anime" && bind.prompt.text.toString() != "") {
                                val json = """
                    {
                      "key": "$apiValue",
                      "prompt": "${bind.prompt.text}",
    "negative_prompt": "nsfw, [deformed | disfigured], poorly drawn, [bad : wrong] anatomy,  [extra | missing | floating | disconnected ] limb, (mutated hands and fingers and legs), blurry, nsfw, glare",
                      "model_id": "toonyou",
                      "width": "$width",
                       "height": "$height",
                      "samples": "1",
                      "num_inference_steps": "25",
                      "safety_checker": "no",
                      "enhance_prompt": "yes",
                      "seed": null,
                      "guidance_scale": 7,
                      "multi_lingual": "yes",
                      "panorama": "no",
                      "self_attention": "no",
                      "upscale": "no",
                      "embeddings_model": null,
                      "lora_model": null,
                      "use_karras_sigmas": "yes",
                      "vae": null,
                      "lora_strength": null,
                      "webhook": null,
                      "track_id": null
                    }
                """.trimIndent()
                                val toGenerate = Intent(this, generate_peocess::class.java)
                                toGenerate.putExtra("json", json)
                                Log.i("json", json.toString())
                                toGenerate.putExtra("rasa", bind.textView21.text.toString())
                                toGenerate.putExtra("gallery", false)

                                toGenerate.putExtra("isAnime", true)
                                if (isPro) {
                                    startActivity(toGenerate)
                                } else {
                                    userRef.update("attempt", attempts - 1)
                                        .addOnSuccessListener {
                                            startActivity(toGenerate)
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(
                                                this,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Select size and enter your prompt",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                        dialog.show()
                    } else {


                        Log.i("images", images.size.toString())

                        val userRef = db.collection("Users").document(currentUser!!.uid)
                        if (sizeForGeneration != "" && isAnime == null && bind.prompt.text.toString() != "") {
                            val json = """
                      {
                        "key": "$apiValue",
                        "prompt": "${
                                bind.prompt.text.toString().replace("girl", "girlfriend", true)
                                    .replace("school", "university", true)
                            }",
    "negative_prompt": "nsfw, [deformed | disfigured], poorly drawn, [bad : wrong] anatomy,  [extra | missing | floating | disconnected ] limb, (mutated hands and fingers and legs), blurry, nsfw, glare",
                        "width": "$width",
                        "height": "$height",
                        "samples": "1",
  "num_inference_steps": "25",
  "safety_checker": "no",
  "enhance_prompt": "yes",
  "seed": null,
  "guidance_scale": 7.5,
  "multi_lingual": "yes",
  "panorama": "no",
  "self_attention": "no",
  "upscale": "no",
  "embeddings_model": null,
  "webhook": null,
  "track_id": null
                      }
                    """.trimIndent()
                            val toGenerate = Intent(this, generate_peocess::class.java)
                            toGenerate.putExtra("json", json)
                            toGenerate.putExtra("rasa", bind.textView21.text.toString())
                            toGenerate.putExtra("gallery", true)
                            Log.i("json", json.toString())
                            if (isPro) {
                                startActivity(toGenerate)
                            } else {
                                userRef.update("attempt", attempts - 1)
                                    .addOnSuccessListener {
                                        startActivity(toGenerate)
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }


                        } else if (sizeForGeneration != "" && isAnime == "anime" && bind.prompt.text.toString() != "") {
                            val json = """
                    {
                      "key": "$apiValue",
                      "prompt": "${bind.prompt.text}",
    "negative_prompt": "nsfw, [deformed | disfigured], poorly drawn, [bad : wrong] anatomy,  [extra | missing | floating | disconnected ] limb, (mutated hands and fingers and legs), blurry, nsfw, glare",
                      "model_id": "toonyou",
                      "width": "$width",
                       "height": "$height",
                      "samples": "1",
                      "num_inference_steps": "25",
                      "safety_checker": "no",
                      "enhance_prompt": "yes",
                      "seed": null,
                      "guidance_scale": 7,
                      "multi_lingual": "yes",
                      "panorama": "no",
                      "self_attention": "no",
                      "upscale": "no",
                      "embeddings_model": null,
                      "lora_model": null,
                      "use_karras_sigmas": "yes",
                      "vae": null,
                      "lora_strength": null,
                      "webhook": null,
                      "track_id": null
                    }
                """.trimIndent()
                            val toGenerate = Intent(this, generate_peocess::class.java)
                            toGenerate.putExtra("json", json)
                            Log.i("json", json.toString())
                            toGenerate.putExtra("rasa", bind.textView21.text.toString())
                            toGenerate.putExtra("gallery", true)

                            toGenerate.putExtra("isAnime", true)
                            if (isPro) {
                                startActivity(toGenerate)
                            } else {
                                userRef.update("attempt", attempts - 1)
                                    .addOnSuccessListener {
                                        startActivity(toGenerate)
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Select size and enter your prompt",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                else {


                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.alert_limit)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


                    val cancel = dialog.findViewById<TextView>(R.id.Cancel)
                    val continueButton = dialog.findViewById<TextView>(R.id.textView40)

                    cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    continueButton.setOnClickListener {
                        val fragmentManager = supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        val preload = pro_screen()

                        fragmentTransaction.replace(R.id.forPro, preload)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }

        }

    }
    fun UpdateUI(isPro: Boolean, styles: ArrayList<Model>){
        val style_adapter = style_adapter(styles, isPro)
        bind.styles.layoutManager = GridLayoutManager(this, 2)
        bind.styles.adapter = style_adapter

        style_adapter.setOnItemClickListener { m->
            model = m

            if((isPro == false && model.type == "free") || (isPro == true)) {


                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val customView = inflater.inflate(R.layout.about_style_popup, null)

                val closeButton = customView.findViewById<ImageButton>(R.id.close)
                val text = customView.findViewById<TextView>(R.id.textView29)
                val image = customView.findViewById<ImageView>(R.id.imageView7)
                val tryButton = customView.findViewById<AppCompatButton>(R.id.button5)

                Log.i("promptPopup", model.toString())
                val popupHeight = resources.getDimensionPixelOffset(R.dimen.popup_height)
                val popupMargin = resources.getDimensionPixelOffset(R.dimen.popup_margin)

                val popupWindow =
                    PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, popupHeight)
                popupWindow.isOutsideTouchable = true
                popupWindow.isFocusable = true

                closeButton.setOnClickListener {
                    popupWindow.dismiss()
                }
                tryButton.setOnClickListener {
                    bind.prompt.setText(model.prompt)
                    bind.scroll.post {
                        bind.scroll.smoothScrollTo(0, 0)
                        popupWindow.dismiss()
                    }
                }
                runOnUiThread {
                    Picasso.get()
                        .load(model.imagePath)
                        .into(image, object : Callback {
                            override fun onSuccess() {
                                popupWindow.showAtLocation(
                                    bind.root,
                                    Gravity.CENTER,
                                    popupMargin,
                                    popupMargin
                                )

                            }

                            override fun onError(e: Exception?) {
                                Log.i("promptImage", "false")

                            }
                        })
                    text.text = model.prompt

                }
            }
            else if(isPro == false && model.type == null){
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val preload = pro_screen()

                fragmentTransaction.replace(R.id.forPro, preload)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

        }
        if(isPro == true){
            bind.alPro.visibility = View.GONE
            bind.asPro.visibility = View.GONE
            bind.imageView5.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        auth = Firebase.auth
        currentUser = auth.currentUser
        if(currentUser == null){
            isPro = false
            UpdateUI(isPro, styles)
        }else{
            val userRef = db.collection("Users").document(currentUser!!.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        isPro = document.getBoolean("isPro")!!
                        attempts = document.get("attempt")!!.toString().toInt()

                        Log.i("isPro", isPro.toString())
                        UpdateUI(isPro, styles)
                    } else {
                        Log.d("FirestoreData", "Документ не найден")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FirestoreData", "Ошибка при получении данных: ", exception)
                }
        }
    }

}