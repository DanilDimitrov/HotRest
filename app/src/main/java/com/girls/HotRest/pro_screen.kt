package com.girls.HotRest

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authenticate
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class pro_screen : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var paymentSheet: PaymentSheet
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null
    var cost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        paymentSheet = PaymentSheet(this) { paymentResult ->
            when (paymentResult) {
                is PaymentSheetResult.Canceled -> {
                    // Оплата была отменена
                }
                is PaymentSheetResult.Failed -> {
                    // Оплата не удалась, обработка ошибки
                    val errorMessage = paymentResult.error
                    Log.e("errorMessage", errorMessage.toString())
                }
                is PaymentSheetResult.Completed -> {
                    if (cost == 9.99){
                        updatePlan(user!!, "month")
                        Log.d("UpdatePlan", "Plan updated for monthly subscription")
                    } else {
                        updatePlan(user!!, "year")
                        Log.d("UpdatePlan", "Plan updated for yearly subscription")
                    }
                }
            }
        }

        auth = Firebase.auth
        user = auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pro_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val close = view.findViewById<ImageButton>(R.id.imageButton5)
        close.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.frameLayout)
            val currentFragment2 = requireActivity().supportFragmentManager.findFragmentById(R.id.forPro)
            val photoFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.photoFragment)
            val galleryPro = requireActivity().supportFragmentManager.findFragmentById(R.id.galleryPro)
            val settingPro = requireActivity().supportFragmentManager.findFragmentById(R.id.settingPro)




            if (currentFragment != null) {
                transaction.remove(currentFragment)
                transaction.commit()
            }else if (currentFragment2 != null) {
                    transaction.remove(currentFragment2)
                    transaction.commit()
            }else if(photoFragment !=null){
                transaction.remove(photoFragment)
                transaction.commit()
            }else if(galleryPro !=null){
                transaction.remove(galleryPro)
                transaction.commit()
            }else{
                if (settingPro != null) {
                    transaction.remove(settingPro)
                    transaction.commit()
                }

            }

        }
        val Month: RadioButton = view.findViewById(R.id.radioButton)
        val Year: RadioButton = view.findViewById(R.id.radioButton2)

        Year.isChecked=true
        cost = 39.99

        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
        val colors = intArrayOf(0xFFff2d55.toInt(), 0xFFFD559A.toInt(), 0xFFff9f0a.toInt())
        Year.setBackgroundResource(R.drawable.selected_plan)


        val colorStateList = ColorStateList(states, colors)

        // Встановити градієнтний фон обрамлення RadioButton
        Year.buttonTintList = colorStateList


        val terms: TextView = view.findViewById(R.id.textView13)
        val policy: TextView = view.findViewById(R.id.textView14)
        val restore: TextView = view.findViewById(R.id.textView15)


        val contine: AppCompatButton = view.findViewById(R.id.button)

        terms.paintFlags = terms.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        terms.setOnClickListener {
            val url = "https://reimageapp.ai/Terms_Of_Use-hotrest"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        restore.paintFlags = restore.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        restore.setOnClickListener {
            val url = "https://reimageapp.ai/about"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        policy.paintFlags = policy.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        policy.setOnClickListener {
            val url = "https://reimageapp.ai/hotrest_privacy-policy"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        Month.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cost = 9.99
                Year.isChecked = false
                // Отримати стандартний обрамлений фон кнопки
                val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
                val colors = intArrayOf(0xFFff2d55.toInt(), 0xFFFD559A.toInt(), 0xFFff9f0a.toInt())
                Month.setBackgroundResource(R.drawable.selected_plan)


                val colorStateList = ColorStateList(states, colors)

                // Встановити градієнтний фон обрамлення RadioButton
                buttonView?.buttonTintList = colorStateList

            } else {
                Month.isChecked = false
                Month.setBackgroundResource(R.drawable.unselect_plan)

                // Стандартний фон для невибраного RadioButton
                buttonView?.buttonTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        Year.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cost = 39.99

                Month.isChecked = false
                // Отримати стандартний обрамлений фон кнопки
                val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
                val colors = intArrayOf(0xFFff2d55.toInt(), 0xFFFD559A.toInt(), 0xFFff9f0a.toInt())
                Year.setBackgroundResource(R.drawable.selected_plan)


                val colorStateList = ColorStateList(states, colors)

                // Встановити градієнтний фон обрамлення RadioButton
                buttonView?.buttonTintList = colorStateList

            } else {
                Year.isChecked = false
                Year.setBackgroundResource(R.drawable.unselect_plan)

                // Стандартний фон для невибраного RadioButton
                buttonView?.buttonTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }

        context?.let { PaymentConfiguration.init(it, "pk_live_51OTSD4H9HE7nkUR99EYeK1VykpXlqK32AdM10RyVk2C6qB8dS63135hL0am9eCgchMGTGQhh3SZry2cyFTPkjgnL00PhN7jLFD") }


        contine.setOnClickListener {

            if(checkUser(user) == true) {
                if (cost == 0.0)
                    Toast.makeText(context, "Please choose your plan", Toast.LENGTH_SHORT).show()
                else {
                   lifecycleScope.launch {
                        presentPaymentSheet()
                   }
                }
            }
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            pro_screen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private suspend fun presentPaymentSheet() {


        createCustomer { customerid ->
            lifecycleScope.launch {
                createEphemeralKey(customerid!!) { key ->
                    lifecycleScope.launch {
                        createPaymentIntent(
                            customerid,
                            (cost * 100).toInt(),
                            "usd"
                        ) { clientSecret ->


                            val paymentIntentClientSecret = clientSecret
                            val customerId = customerid
                            val ephemeralKeySecret = key!!

                            val customerConfig = PaymentSheet.CustomerConfiguration(
                                id = customerId,
                                ephemeralKeySecret = ephemeralKeySecret
                            )

                            // Представление PaymentSheet
                            paymentSheet.presentWithPaymentIntent(
                                paymentIntentClientSecret!!,
                                PaymentSheet.Configuration(
                                    merchantDisplayName = "HotRest",
                                    customer = customerConfig,
                                    allowsDelayedPaymentMethods = true // Установите в true, если нужно поддерживать отложенные методы оплаты
                                )
                            )
                        }
                    }
                }

            }
        }
    }

    fun checkUser(user: FirebaseUser?): Boolean{
        if(user == null){
            Toast.makeText(context, "Please Log In or Sign Up", Toast.LENGTH_SHORT).show()
            val toLogin = Intent(context, sign_up::class.java)
            toLogin.putExtra("from_pro", true)
            startActivity(toLogin)
            return false
        }
        else
            return true
    }

    fun updatePlan(user: FirebaseUser, type: String){
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("Users").document(user.uid)
        userDoc.get()
            .addOnSuccessListener {documentSnapshot ->
                val currentTime = com.google.firebase.Timestamp.now()
                val nextWeek = currentTime.seconds + (7 * 24 * 60 * 60)
                val nextYear = currentTime.seconds + (((7 * 24 * 60 * 60) *4)*12)

                userDoc.update("isPro", true)
                if(type == "month"){
                    userDoc.update("time", com.google.firebase.Timestamp(nextWeek, 0))
                }else{
                    userDoc.update("time", com.google.firebase.Timestamp(nextYear, 0))
                }
        }
    }


    suspend fun createCustomer(callback: (String?) -> Unit) {
        try {
            val apiKey = "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"
            val url = "https://api.stripe.com/v1/customers"

            val (_, response, result) = withContext(Dispatchers.IO) {
                Fuel.post(url)
                    .authenticate(apiKey, "")
                    .responseString()
            }

            Log.i("createCustomer", response.toString())
            Log.i("createCustomer", result.get().toString())

            result.fold(
                success = { data ->
                    Log.i("resultCustomer", data)
                    when (response.statusCode) {
                        200 -> {
                            val customerId = parseCustomerId(data)
                            callback(customerId)
                        }
                        else -> callback(null)
                    }
                },
                failure = { error ->
                    // Обработка ошибки при выполнении запроса
                    Log.e("resultCustomer", "Error: ${error.exception}")
                    callback(null)
                }
            )
        } catch (e: Exception) {
            // Обработка других исключений, если таковые возникнут
            Log.e("createCustomer", "Error: ${e.message}")
            callback(null)
        }
    }
    private fun parseCustomerId(responseBody: String): String? {
        val regex = Regex("""\"id\"\s*:\s*\"(\w+)\"""")
        val matchResult = regex.find(responseBody)
        return matchResult?.groupValues?.get(1)
    }
    suspend fun createEphemeralKey(customerId: String, callback: (String?) -> Unit) {
        try {
            val apiKey = "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"
            val url = "https://api.stripe.com/v1/ephemeral_keys"
            val stripeVersion = "2023-10-16"

            val (_, response, result) = withContext(Dispatchers.IO) {
                Fuel.post(url)
                    .authenticate(apiKey, "")
                    .header("Stripe-Version", stripeVersion)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body("customer=$customerId")
                    .responseString()
            }

            Log.i("createEphemeralKey", response.toString())
            Log.i("createEphemeralKey", result.get().toString())

            result.fold(
                success = { data ->
                    Log.i("createEphemeralKey", data)
                    when (response.statusCode) {
                        200 -> {
                            val key = parseCustomerId(data)
                            callback(key)
                        }
                        else -> callback(null)
                    }
                },
                failure = { error ->
                    // Обработка ошибки при выполнении запроса
                    Log.e("createEphemeralKey", "Error: ${error.exception}")
                    callback(null)
                }
            )
        } catch (e: Exception) {
            // Обработка других исключений, если таковые возникнут
            Log.e("createEphemeralKey", "Error: ${e.message}")
            callback(null)
        }
    }
    suspend fun createPaymentIntent(customerId: String, amount: Int, currency: String, callback: (String?) -> Unit) {
        try {
            val apiKey = "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"
            val url = "https://api.stripe.com/v1/payment_intents"
            val automaticPaymentEnabled = true

            val (_, response, result) = withContext(Dispatchers.IO) {
                Fuel.post(url)
                    .authenticate(apiKey, "")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(
                        "customer=$customerId" +
                                "&amount=$amount" +
                                "&currency=$currency" +
                                "&automatic_payment_methods[enabled]=$automaticPaymentEnabled"
                    )
                    .responseString()
            }
            Log.i("createPaymentIntent", response.toString())
            Log.i("createPaymentIntent", result.get().toString())

            result.fold(
                success = { data ->
                    Log.i("createPaymentIntent", data)
                    when (response.statusCode) {
                        200 -> {
                            val responseBody = data.trimIndent()
                            val jsonObject = JSONObject(responseBody)
                            val clientSecret = jsonObject.getString("client_secret")
                            callback(clientSecret)
                        }
                        else -> callback(null)
                    }
                },
                failure = { error ->
                    // Обработка ошибки при выполнении запроса
                    Log.e("createPaymentIntent", "Error: ${error.exception}")
                    callback(null)
                }
            )
        } catch (e: Exception) {
            // Обработка других исключений, если таковые возникнут
            Log.e("createPaymentIntent", "Error: ${e.message}")
            callback(null)
        }
    }


    override fun onResume() {
        super.onResume()
        user = auth.currentUser
    }



}