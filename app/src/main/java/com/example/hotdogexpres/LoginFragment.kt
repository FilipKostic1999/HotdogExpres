package com.example.hotdogexpres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hotdogexpres.classes.userProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Random

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore

    lateinit var signUpTxt: TextView
    lateinit var logInTitleTxt: TextView
    lateinit var logInEmailEt: TextView
    lateinit var logInPasswordEt: TextView
    lateinit var logInBtn: Button

    lateinit var signUpTitleTxt: TextView
    lateinit var nameEt: TextView
    lateinit var surnameEt: TextView
    lateinit var signUpEmailEt: TextView
    lateinit var signUpPasswordEt: TextView
    lateinit var signUpConfPasswordEt: TextView
    lateinit var signUpBtn: Button
    lateinit var logInTxt: TextView


    lateinit var loggedInImg: ImageView
    lateinit var youAreLoggedTxt: TextView
    lateinit var userImg: ImageView
    lateinit var hotDogImg: ImageView
    lateinit var logOutBtn: Button


    lateinit var forgotPasswordTxt: TextView
    lateinit var backToLogInTxt: TextView
    lateinit var resetPasswordBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser


        // Access interactive elements by their IDs
        signUpTxt = view.findViewById(R.id.signUpTxt)
        logInTitleTxt = view.findViewById(R.id.logInTitleTxt)
        logInEmailEt = view.findViewById(R.id.logInEmailEt)
        logInPasswordEt = view.findViewById(R.id.logInPasswordEt)
        logInBtn = view.findViewById(R.id.logInBtn)
        nameEt = view.findViewById(R.id.nameEt)
        surnameEt = view.findViewById(R.id.surnameEt)
        signUpEmailEt = view.findViewById(R.id.signUpEmailEt)
        signUpPasswordEt = view.findViewById(R.id.signUpPasswordEt)
        signUpConfPasswordEt = view.findViewById(R.id.signUpConfPasswordEt)
        signUpBtn = view.findViewById(R.id.signUpBtn)
        logInTxt = view.findViewById(R.id.logInTxt)
        signUpTitleTxt = view.findViewById(R.id.signUpTitleTxt)
        loggedInImg = view.findViewById(R.id.loggedInImg)
        youAreLoggedTxt = view.findViewById(R.id.youAreLoggedTxt)
        userImg = view.findViewById(R.id.userImg)
        hotDogImg = view.findViewById(R.id.hotDogImg)
        logOutBtn = view.findViewById(R.id.logOutBtn)
        forgotPasswordTxt = view.findViewById(R.id.forgotPasswordTxt)
        resetPasswordBtn = view.findViewById(R.id.resetPasswordBtn)
        backToLogInTxt = view.findViewById(R.id.backToLogInTxt)



        showLayout()




        // Set up click listener for the button
        signUpTxt.setOnClickListener {
            showOnlySignUp()
        }

        logInTxt.setOnClickListener {
            showOnlyLogIn()
        }

        signUpBtn.setOnClickListener {
            createAccount()
        }

        logInBtn.setOnClickListener {
            logIn()
        }

        logOutBtn.setOnClickListener {
            auth.signOut()
            showOnlyLogIn()
        }

        forgotPasswordTxt.setOnClickListener {
            showResetPassword()
        }

        backToLogInTxt.setOnClickListener {
            showOnlyLogIn()
        }





        resetPasswordBtn.setOnClickListener {
            val email = logInEmailEt.text.toString().trim()

            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to send reset email. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Enter your email address.", Toast.LENGTH_SHORT).show()
            }
        }






    }



    fun showResetPassword() {
        nameEt.visibility = View.GONE
        surnameEt.visibility = View.GONE
        signUpEmailEt.visibility = View.GONE
        signUpPasswordEt.visibility = View.GONE
        signUpConfPasswordEt.visibility = View.GONE
        signUpBtn.visibility = View.GONE
        logInTxt.visibility = View.GONE
        signUpTitleTxt.visibility = View.GONE
        logInPasswordEt.visibility = View.GONE
        logInBtn.visibility = View.GONE
        signUpTxt.visibility = View.GONE
        loggedInImg.visibility = View.GONE
        youAreLoggedTxt.visibility = View.GONE
        forgotPasswordTxt.visibility = View.GONE


        logInTitleTxt.text = "Reset password"
        logInTitleTxt.visibility = View.VISIBLE
        logInEmailEt.visibility = View.VISIBLE
        resetPasswordBtn.visibility = View.VISIBLE
        hotDogImg.visibility = View.VISIBLE
        userImg.visibility = View.VISIBLE
        backToLogInTxt.visibility = View.VISIBLE
    }




    fun showOnlyLogIn() {
            nameEt.visibility = View.GONE
            surnameEt.visibility = View.GONE
            signUpEmailEt.visibility = View.GONE
            signUpPasswordEt.visibility = View.GONE
            signUpConfPasswordEt.visibility = View.GONE
            signUpBtn.visibility = View.GONE
            logInTxt.visibility = View.GONE
            signUpTitleTxt.visibility = View.GONE
            loggedInImg.visibility = View.GONE
            youAreLoggedTxt.visibility = View.GONE
            logOutBtn.visibility = View.GONE
            resetPasswordBtn.visibility = View.GONE
            backToLogInTxt.visibility = View.GONE

            logInTitleTxt.text = "Log in"
            logInTitleTxt.visibility = View.VISIBLE
            logInEmailEt.visibility = View.VISIBLE
            logInPasswordEt.visibility = View.VISIBLE
            logInBtn.visibility = View.VISIBLE
            signUpTxt.visibility = View.VISIBLE
            hotDogImg.visibility = View.VISIBLE
            forgotPasswordTxt.visibility = View.VISIBLE
            userImg.visibility = View.VISIBLE
    }


    fun showOnlySignUp() {
            logInTitleTxt.visibility = View.GONE
            logInEmailEt.visibility = View.GONE
            logInPasswordEt.visibility = View.GONE
            logInBtn.visibility = View.GONE
            signUpTxt.visibility = View.GONE
            loggedInImg.visibility = View.GONE
            youAreLoggedTxt.visibility = View.GONE
            logOutBtn.visibility = View.GONE
            forgotPasswordTxt.visibility = View.GONE
            resetPasswordBtn.visibility = View.GONE
            backToLogInTxt.visibility = View.GONE

            nameEt.visibility = View.VISIBLE
            surnameEt.visibility = View.VISIBLE
            signUpEmailEt.visibility = View.VISIBLE
            signUpPasswordEt.visibility = View.VISIBLE
            signUpConfPasswordEt.visibility = View.VISIBLE
            signUpBtn.visibility = View.VISIBLE
            logInTxt.visibility = View.VISIBLE
            signUpTitleTxt.visibility = View.VISIBLE
            hotDogImg.visibility = View.VISIBLE
            userImg.visibility = View.VISIBLE
    }


    fun showInloggedView() {

        val user = auth.currentUser

        if (user != null) {
            nameEt.visibility = View.GONE
            surnameEt.visibility = View.GONE
            signUpEmailEt.visibility = View.GONE
            signUpPasswordEt.visibility = View.GONE
            signUpConfPasswordEt.visibility = View.GONE
            signUpBtn.visibility = View.GONE
            logInTxt.visibility = View.GONE
            signUpTitleTxt.visibility = View.GONE
            logInTitleTxt.visibility = View.GONE
            logInEmailEt.visibility = View.GONE
            logInPasswordEt.visibility = View.GONE
            logInBtn.visibility = View.GONE
            signUpTxt.visibility = View.GONE
            loggedInImg.visibility = View.GONE
            youAreLoggedTxt.visibility = View.GONE
            userImg.visibility = View.GONE
            hotDogImg.visibility = View.GONE
            forgotPasswordTxt.visibility = View.GONE
            resetPasswordBtn.visibility = View.GONE
            backToLogInTxt.visibility = View.GONE

            loggedInImg.visibility = View.VISIBLE
            youAreLoggedTxt.visibility = View.VISIBLE
            logOutBtn.visibility = View.VISIBLE
        }
    }



    fun logIn() {
        if (logInEmailEt.text.toString().isNotEmpty()
            && logInPasswordEt.text.toString().isNotEmpty()) {
            auth.signInWithEmailAndPassword(logInEmailEt.text.toString(), logInPasswordEt.text.toString()).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        showInloggedView()
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong with your log in", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), authTask.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("!!!", authTask.exception.toString())
                }
            }
        } else {
            Toast.makeText(requireContext(), "There are empty fields", Toast.LENGTH_SHORT).show()
        }
    }



    fun createAccount() {


            if (signUpEmailEt.text.toString().isNotEmpty()
                && signUpPasswordEt.text.toString().isNotEmpty()
                && signUpConfPasswordEt.text.toString().isNotEmpty()
                && nameEt.text.toString().isNotEmpty()
                && surnameEt.text.toString().isNotEmpty()) {
                if (signUpPasswordEt.text.toString() == signUpConfPasswordEt.text.toString()) {
                    auth.createUserWithEmailAndPassword(signUpEmailEt.text.toString(),
                        signUpPasswordEt.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val user = auth.currentUser


                                    val timestamp = System.currentTimeMillis()
                                    val random = Random(timestamp)

                                    val codeID = StringBuilder()

                                    repeat(9) {
                                        val randomChar = if (random.nextBoolean()) {
                                            // Generate a random digit
                                            (random.nextInt(10) + '0'.toInt()).toChar()
                                        } else {
                                            // Generate a random uppercase letter
                                            (random.nextInt(26) + 'A'.toInt()).toChar()
                                        }
                                        codeID.append(randomChar)
                                    }

                                    val userId = codeID.toString()



                                if (user != null) {

                                    val userData = userProfile(nameEt.text.toString(),
                                        surnameEt.text.toString(), signUpEmailEt.text.toString(),
                                        "", "", "", "",
                                        userId)

                                    database.collection("Hotdog Expres")
                                        .document("Users")
                                        .collection(user.uid)
                                        .document("User profile").set(userData)
                                        .addOnSuccessListener {
                                            // Document successfully written
                                            Toast.makeText(requireContext(), "Profile created successfully", Toast.LENGTH_SHORT).show()
                                            showOnlyLogIn()
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle errors writing the document
                                        }


                                }
                            } else {
                                // Account creation failed
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "The passwords do not correspond", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "There are empty fields", Toast.LENGTH_SHORT).show()
            }

    }



    fun showLayout() {
        val user = auth.currentUser
        if (user != null) {
            showInloggedView()
        } else {
            showOnlyLogIn()
        }
    }



}

