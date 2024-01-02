package com.girls.HotRest

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.girls.HotRest.databinding.ActivityEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class email : AppCompatActivity() {
    lateinit var bind: ActivityEmailBinding
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(bind.root)
        var isPassHide = true
        db = FirebaseFirestore.getInstance()

        bind.imageButton11.setOnClickListener {
            finish()
        }

        bind.textView40.paintFlags = bind.textView40.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        bind.textView40.setOnClickListener {
            if(bind.email.text.isEmpty()){
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            }else{
                    resetPassword(bind.email.text.toString())
            }
        }

        bind.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    bind.imageButton12.visibility = View.VISIBLE
                } else {
                    bind.imageButton12.visibility = View.INVISIBLE
                }
            }
        })

        bind.button6.setOnClickListener {
            if(bind.email.text.isEmpty() || bind.password.text.isEmpty()){
                Toast.makeText(this, "Enter password and email", Toast.LENGTH_SHORT).show()
            }else{
               registerWithEmail(bind.email.text.toString(), bind.password.text.toString())
            }
        }

        bind.imageButton12.setOnClickListener {
            isPassHide = !isPassHide
            if (isPassHide && bind.password.text.isNotEmpty()) {
                bind.imageButton12.setImageResource(R.drawable.eye)
                bind.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            } else {
                bind.imageButton12.setImageResource(R.drawable.eye_no)

                bind.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            bind.password.setSelection(bind.password.text.length)
        }
    }

    private fun registerWithEmail(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
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
                                    finish()
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
                                            Log.d("TAG", "DocumentSnapshot successfully written!")
                                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error writing document", e)
                                        }
                                }
                            }
                    } else {
                        Log.d("TAG", "User is not authenticated or UID is null")
                    }
                } else {
                    Log.w("registerWithEmail", "createUserWithEmail:failure", task.exception)
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { signInTask ->
                                if (signInTask.isSuccessful) {
                                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

                    }
                }
            }
    }
    private fun resetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        // Проверяем, существует ли аккаунт с указанным email
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetTask ->
                                if (resetTask.isSuccessful) {
                                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    // Обработка ошибки при проверке email
                    Toast.makeText(this, "Error checking email", Toast.LENGTH_SHORT).show()
                }
            }
    }

}