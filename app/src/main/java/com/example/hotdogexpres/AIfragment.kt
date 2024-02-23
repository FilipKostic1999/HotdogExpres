package com.example.hotdogexpres

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            "about the Hotdog Express app: This is an app that helps people find fast foods, " +
            "there is a wrench icon that when clicked displays the settings " +
            "that can turn on or off the automatic placement of the users company " +
            "on the map " +
            "This is what you can answer about, refuse to answer questions that " +
            "are not coherent to the argument of this app or it´s data that i provided " +
            "be a kind chatbot and never break character. The following is the users input:"





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
        val apiKey = ""
        val url = "https://api.openai.com/v1/completions"
        val requestBody = """
            {
    "model": "gpt-3.5-turbo-instruct",
    "prompt": "$question",
    "max_tokens": 200,
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