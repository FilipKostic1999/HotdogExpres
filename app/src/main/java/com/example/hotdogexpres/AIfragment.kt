package com.example.hotdogexpres

import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AIfragment : Fragment() {



    lateinit var askAnythingEt: TextView
    lateinit var sendBtn: Button


    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter




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


        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser


        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.chatRecyclerView)

        // Create a list of messages with sample data
        val messagesList = listOf(
            messages("1", "Hello! I'm the AI chatbot.", "", ""),
            messages("2", "Hi! Nice to meet you.", "", "")
            // Add more sample messages as needed
        )

        // Initialize the adapter with the list of messages
        adapter = MessagesAdapter(messagesList)

        // Set the adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        sendBtn.setOnClickListener {

            sendMessage()


        }


    }





    fun sendMessage() {




    }




}