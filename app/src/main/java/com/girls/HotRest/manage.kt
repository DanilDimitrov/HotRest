import android.util.Log
import com.girls.HotRest.Model
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class manage {
    suspend fun getAllStyles(): ArrayList<Model> = suspendCoroutine { continuation ->
        val stylesArray = arrayListOf<Model>()
        val db = FirebaseFirestore.getInstance()
        val hotRestRequestsRef = db.collection("hot_rest_requests")

        hotRestRequestsRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    if (data != null) {
                        val imagePath = data["image_path"].toString()
                        val collection = data["collection"].toString()
                        val model_id = data["model_id"].toString()
                        val prompt = data["prompt"].toString()
                        val type = data["type"]?.toString()


                        val model = Model(imagePath, model_id, collection, prompt, type)
                        stylesArray.add(model)
                    }
                }
                continuation.resume(stylesArray)
            }
            .addOnFailureListener { e ->
                Log.e("hot_rest_requests_ERROR", e.toString())
                continuation.resumeWithException(e)
            }
    }

}
