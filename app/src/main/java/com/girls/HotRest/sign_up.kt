package com.girls.HotRest

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.girls.HotRest.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class sign_up : AppCompatActivity() {

    lateinit var bind: ActivitySignUpBinding
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bind.root)
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        bind.textView42.paintFlags = bind.textView42.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        bind.textView42.setOnClickListener {
            val url = "https://reimageapp.ai/hotrest_privacy-policy"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        bind.textView44.paintFlags = bind.textView44.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        bind.textView44.setOnClickListener {
            val url = "https://reimageapp.ai/Terms_Of_Use-hotrest"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("766883714048-tbs1jhlh43uusos2i0c63se1601ridic.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        bind.imageButton9.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                startActivityForResult(signInIntent, 5)
            }
        }
        bind.imageButton10.setOnClickListener {
            val toEmail = Intent(this, email::class.java)
            startActivity(toEmail)
        }
        bind.imageButton8.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 5) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken!!)
            } catch (e: ApiException) {
                Log.w("google_signUp", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        db.collection("Users")
                            .document(uid)
                            .get()
                            .addOnCompleteListener { document ->
                                if (document.result.exists()) {
                                    Log.d("TAG", "User already exists")
                                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                } else {
                                    val userData = hashMapOf(
                                        "isPro" to false,
                                        "uuid" to uid,
                                        "attempt" to 3,
                                        "imagesUrls" to ArrayList<String>()
                                    )
                                    db.collection("Users")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            // Успешное создание документа
                                            Log.d("TAG", "DocumentSnapshot successfully written!")
                                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                            onBackPressed()
                                        }
                                        .addOnFailureListener { e ->
                                            // Обработка ошибки при создании документа
                                            Log.w("TAG", "Error writing document", e)
                                        }
                                }
                            }
                    } else {
                        // UID пользователя не получен или пользователь не аутентифицирован
                        Log.d("TAG", "User is not authenticated or UID is null")
                    }

                } else {
                    Log.w("google_signUp", "signInWithCredential:failure", task.exception)
                }
            }
    }
}