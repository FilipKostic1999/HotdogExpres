package com.example.hotdogexpres

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.adapters.MessagesAdapter
import com.example.hotdogexpres.classes.messages
import com.example.hotdogexpres.classes.review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.logging.Handler


class AIfragment : Fragment() {



    lateinit var askAnythingEt: TextView
    lateinit var sendBtn: Button


    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    lateinit var messagesList : ArrayList<messages>


    private val client = OkHttpClient()
    var dataAI = "You are an AI that provides customer service to clients " +
            "who have questions about the Hotdog Express app. " +
            "Your customer service consists in answering questions about the " +
            "functionality of the app. This is the following data that you have " +
            "about the Hotdog Express app: " +




            "This is an app that helps people find fast foods, " +
            "there is a wrench icon on the bottom left corner of the map that when clicked " +
            "displays the settings " +
            "that can turn on or off the function that places the user´s company where " +
            "the user clicks on the map " +
            "When the user clicks somewhere on the map (assuming the user has " +
            "registered a company and is logged in) the company marker automatically " +
            "goes to that position setting that place´s address " +
            "for the user´s company automatically. " +
            "The user cannot interact or view his/her profile or data or company data unless" +
            " they are " +
            "logged in to their account. " +
            "The only functions that the app allows to be used when the user is not logged in " +
            "are the viewing of all the companies on the map, being taken there with the " +
            "(take me there) button, viewing the selected company´s menu," +
            " viewing the selected company´s reviews, create account and log in. " +
            "The log in, create account and reset password are on the bottom right corner of the " +
            "bottom navigation bar where they can be accessed by clicking on the door icon. " +
            "On the left of the door icon there is a profile icon where " +
            "the user can view their profile " +
            "when they are logged in, along with their reviews and company " +
            "(creating a company is optional and can be done only when the user has created " +
            "an account and has logged in it). " +
            "On the left of the profile icon there is a bot icon where the user can " +
            "interact with the AI of the app, " +
            "on the left of the bot icon there is a map icon where the user can " +
            "view the map and the companies on it." +
            "The app shows all companies on the map with red markers, only " +
            "the current user´s company (assuming he/she has one) is shown green, " +
            "but it is shown green only if the user is logged in. " +
            "Otherwise even the user´s company is also shown as a red marker. " +
            "When the user´s company appears on the map it becomes visible to all users " +
            "immediately. " +
            "To remove the company from the map the user who that company belongs to " +
            "must be logged in and can access the settings on the wrench icon " +
            "from there he/she can remove the company from the map and nobody will be " +
            "able to see it any longer on the app. The company can be added again " +
            "or removed from the map any time. " +
            "When the user writes an address for their company manually, the map will " +
            "automatically set the user´s company to that address´ location " +
            "and show it there on the map. The address for the company should be " +
            "written precisely to make sure the map understands where they want to " +
            "set their company on the map, however the address does not need to " +
            "be written perfectly, the map can understand the address even " +
            "if the address is not precise or is written in not precise order. " +
            "When the user wants to make a company, they can do so by logging in " +
            "and going to the profile icon and clicking on the (My business) " +
            "on the menu bar of the profile and they will compile their company´s " +
            "info and set it´s address and pay 20 euro to create the company " +
            "and if the creation is successfully their company will automatically" +
            "be set in that address on the map that they have written when they " +
            "created the company " +
            "To be guided to a fastfood you need to press the fastfood you are interested in on the map " +
            "and a display of the fastfoods information will appear below the map " +
            "from there you can click the (take me there) button and google maps will guide " +
            "you there" +




            "This is what you can answer about, refuse to answer questions that " +
            "are not coherent to the argument of this app or it´s data that i provided ." +
            "Only give simple short answers and answer only to what the user asks, " +
            "keep it short and simple. " +
            "Be a kind chatbot and never break character. The following is the users input:"





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a_ifragment, container, false)
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        askAnythingEt = view.findViewById(R.id.askAnythingEt)
        sendBtn = view.findViewById(R.id.sendBtn)
        messagesList = arrayListOf()


        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.chatRecyclerView)

        // Initialize the adapter with the list of messages
        adapter = MessagesAdapter(messagesList)


        // Set the adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

// Inside onViewCreated after setting up adapter and layout manager





        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User messages")
                .collection("Chat")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        if (snapshot.isEmpty) {
// No documents in the snapshot, enable the button
                            messagesList.clear()
                            adapter.notifyDataSetChanged()
                        } else {
                            messagesList.clear()
                            for (document in snapshot.documents) {
                                val message = document.toObject<messages>()!!
                                messagesList.add(message)
                            }
// Sort the reviews by date (assuming date is a String)
                            messagesList.sortBy { it.messageOrder }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        }







        sendBtn.setOnClickListener {

            val questionUser = askAnythingEt.text.toString()

            if (questionUser.isNotEmpty()) {
                sendMessage(questionUser, "2")

                // AI
                var question = dataAI
                question += questionUser

                askAnythingEt.text = ""

                getResponse(question) {response ->
                    activity?.runOnUiThread {
                        android.os.Handler().postDelayed({
                            sendMessageAI(response, "1")
                        }, 1500)
                    }
                }
            }
        }




    }



    fun getMessages() {


        val user= auth.currentUser


        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User messages")
                .collection("Chat")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // No documents in the snapshot, handle accordingly
                        messagesList.clear()
                        adapter.notifyDataSetChanged()
                    } else {
                        messagesList.clear()
                        adapter.notifyDataSetChanged()
                        for (document in documents.documents) {
                            val message = document.toObject<messages>()!!
                            messagesList.add(message)
                        }

                        // Sort the reviews by date (assuming date is a String)
                        messagesList.sortBy { it.messageOrder }
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }

    }






    fun sendMessage(questionUser : String, id : String) {

        val user = auth.currentUser


        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val currentDateTime = dateFormat.format(calendar.time)

        val message = messages(id, questionUser, "", currentDateTime)



        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Users")
                .collection(user.uid)
                .document("User messages")
                .collection("Chat")
                .document().set(message)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }


    }


    fun sendMessageAI(questionUser : String, id : String) {

        val user = auth.currentUser


        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val currentDateTime = dateFormat.format(calendar.time)

        val message = messages(id, questionUser, "", currentDateTime)



        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Users")
                .collection(user.uid)
                .document("User messages")
                .collection("Chat")
                .document().set(message)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }


    }




    fun getResponse(question: String, callBack: (String) -> Unit) {
        val apiKey = "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"
        val url = "https://api.openai.com/v1/completions"
        val requestBody = """
            {
    "model": "gpt-3.5-turbo-instruct",
    "prompt": "$question",
    "max_tokens": 300,
    "temperature": 0
  }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "API error")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("Data", body)
                    var jsonObject = JSONObject(body)
                    val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                    val text = jsonArray.getJSONObject(0).getString("text")
                    callBack(text)
                } else {
                    Log.v("Data", "Empty")
                }



            }

        })

    }






}