package com.example.hotdogexpres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    private lateinit var textView: TextView
    private lateinit var interactableButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access interactive elements by their IDs
        textView = view.findViewById(R.id.textView)
        interactableButton = view.findViewById(R.id.interactableButton)

        // Set up click listener for the button
        interactableButton.setOnClickListener {
            // Handle button click by changing the text of the TextView
            textView.text = "Button Clicked!"
        }
    }
}

