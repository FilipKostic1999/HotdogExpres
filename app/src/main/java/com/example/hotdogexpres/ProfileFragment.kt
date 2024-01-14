package com.example.hotdogexpres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hotdogexpres.classes.userProfile
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore

    private lateinit var nameEt: EditText
    private lateinit var cardNumber: EditText
    private lateinit var surname: EditText
    private lateinit var country: EditText
    private lateinit var dateOfBirth: EditText
    private lateinit var email: EditText
    private lateinit var address: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var logInImg: ImageView
    private lateinit var logInOrCreateTxt: TextView
    private lateinit var saveBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser


        // Access interactive elements by their IDs
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)
        nameEt = view.findViewById(R.id.nameEt)
        cardNumber = view.findViewById(R.id.cardNumberEt)
        surname = view.findViewById(R.id.surnameEt)
        country = view.findViewById(R.id.countryEt)
        dateOfBirth = view.findViewById(R.id.dateOfBirthEt)
        email = view.findViewById(R.id.emailEt)
        address = view.findViewById(R.id.adressEt)
        phoneNumber = view.findViewById(R.id.phoneNumberEt)
        saveBtn = view.findViewById(R.id.saveBtn)
        logInImg = view.findViewById(R.id.logInImg)
        logInOrCreateTxt = view.findViewById(R.id.logInOrCreateTxt)



        val accountDetailsTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_account_details, null)
        val creditCardTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_creditcard, null)
        // Add tabs with custom views
        val accountDetailsTxt = tabLayout.newTab()
        accountDetailsTxt.customView = accountDetailsTab
        tabLayout.addTab(accountDetailsTxt)

        val creditCardTxt = tabLayout.newTab()
        creditCardTxt.customView = creditCardTab
        tabLayout.addTab(creditCardTxt)

        saveBtn.setOnClickListener {
            save()
        }


        // Add a TabLayout.OnTabSelectedListener to listen for tab selection changes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Handle tab selection
                when (tab.position) {

                    0 -> {
                        if (user != null) {
                            nameEt.visibility = View.VISIBLE
                            surname.visibility = View.VISIBLE
                            country.visibility = View.VISIBLE
                            email.visibility = View.VISIBLE
                            address.visibility = View.VISIBLE
                            dateOfBirth.visibility = View.VISIBLE
                            phoneNumber.visibility = View.VISIBLE
                            saveBtn.visibility = View.VISIBLE
                            logInImg.visibility = View.GONE
                            logInOrCreateTxt.visibility = View.GONE
                            cardNumber.visibility = View.GONE
                        }
                    }

                    1 -> {
                        if (user != null) {
                            cardNumber.visibility = View.VISIBLE
                            nameEt.visibility = View.GONE
                            surname.visibility = View.GONE
                            country.visibility = View.GONE
                            email.visibility = View.GONE
                            address.visibility = View.GONE
                            dateOfBirth.visibility = View.GONE
                            phoneNumber.visibility = View.GONE
                            saveBtn.visibility = View.GONE
                            logInImg.visibility = View.GONE
                            logInOrCreateTxt.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselection
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection
            }
        })


        if (user == null) {

            logInImg.visibility = View.VISIBLE
            logInOrCreateTxt.visibility = View.VISIBLE

            cardNumber.visibility = View.GONE
            nameEt.visibility = View.GONE
            surname.visibility = View.GONE
            country.visibility = View.GONE
            email.visibility = View.GONE
            address.visibility = View.GONE
            dateOfBirth.visibility = View.GONE
            phoneNumber.visibility = View.GONE
            saveBtn.visibility = View.GONE
        }


    }


    fun save() {

        val user = auth.currentUser

        val userProfile = userProfile(name = nameEt.text.toString(),
            surname = surname.text.toString(), email = email.text.toString(),
            adress = address.text.toString(), phoneNumber = phoneNumber.text.toString(),
            country = country.text.toString(), dateBirth = dateOfBirth.text.toString())

        // Get a reference to the Firestore collection
        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Users").collection("User profile")
                .document(user.uid).set(userProfile)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Data saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }
        }
    }


}