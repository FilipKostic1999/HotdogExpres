package com.example.hotdogexpres

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.adapters.reviewAdapter
import com.example.hotdogexpres.classes.fastfoodPlace
import com.example.hotdogexpres.classes.review
import com.example.hotdogexpres.classes.userProfile
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Handler


class ProfileFragment : Fragment(), reviewAdapter.OnViewClickListener {

    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore

    private lateinit var nameEt: TextView
    private lateinit var cardNumber: TextView
    private lateinit var surname: TextView
    private lateinit var country: TextView
    private lateinit var dateOfBirth: TextView
    private lateinit var email: TextView
    private lateinit var address: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var titleTxt: TextView
    private lateinit var logInImg: ImageView
    private lateinit var logInOrCreateTxt: TextView
    private lateinit var saveBtn: Button
    private lateinit var nameCompanyEt: TextView
    private lateinit var typeActivityEt: TextView
    private lateinit var companyAddresEt: TextView
    private lateinit var createBusinessBtn: Button
    lateinit var profileUser: userProfile
    lateinit var company: fastfoodPlace
    private val hotDogTruckMarkers = mutableListOf<fastfoodPlace>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: reviewAdapter
    private lateinit var listOfReviews : ArrayList<review>
    lateinit var greenMapsImg: ImageView
    lateinit var greenMapsIconTxt: TextView
    lateinit var saveCompanyChangesBtn: Button
    lateinit var typeTxt: TextView
    lateinit var companyCostTxt: TextView



    var doseCompanyExist = false
    var userProfile = userProfile("", "", "",
        "", "", "", "", "")



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




        recyclerView = view.findViewById(R.id.yourReviewsRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        listOfReviews = arrayListOf()
        myAdapter = reviewAdapter("yourReviews",listOfReviews)
        recyclerView.adapter = myAdapter
        myAdapter.setOnViewClickListener(this)



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
        nameCompanyEt = view.findViewById(R.id.nameCompanyEt)
        typeActivityEt = view.findViewById(R.id.typeActivityEt)
        companyAddresEt = view.findViewById(R.id.companyAddresEt)
        createBusinessBtn = view.findViewById(R.id.createBusinessBtn)
        titleTxt = view.findViewById(R.id.titleTxt)
        greenMapsImg = view.findViewById(R.id.greenMapsImg)
        greenMapsIconTxt = view.findViewById(R.id.greenMapsIconTxt)
        saveCompanyChangesBtn = view.findViewById(R.id.saveCompanyChangesBtn)
        typeTxt = view.findViewById(R.id.typeTxt)
        companyCostTxt = view.findViewById(R.id.companyCostTxt)



        val accountDetailsTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_account_details, null)
        val showMyReviewsTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_your_reviews, null)
        val createCompanyTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_create_company, null)


        val accountDetailsTxt = tabLayout.newTab()
        accountDetailsTxt.customView = accountDetailsTab
        tabLayout.addTab(accountDetailsTxt)

        val showMyReviewsTxt = tabLayout.newTab()
        showMyReviewsTxt.customView = showMyReviewsTab
        tabLayout.addTab(showMyReviewsTxt)

        val createCompanyTxt = tabLayout.newTab()
        createCompanyTxt.customView = createCompanyTab
        tabLayout.addTab(createCompanyTxt)

        saveBtn.setOnClickListener {
            saveUser()
        }


        // Add a TabLayout.OnTabSelectedListener to listen for tab selection changes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Handle tab selection
                when (tab.position) {

                    0 -> {
                        showAccountDetails()
                    }

                    1 -> {
                        showMyReviews()
                    }

                    2 -> {
                        showMyBusiness()
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


        showLogInRequest()



        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button

                        } else {
                            for (document in snapshot.documents) {
                                profileUser = document.toObject()!!
                                nameEt.text = "${profileUser.name}"
                                surname.text = "${profileUser.surname}"
                                email.text = "${profileUser.email}"
                                address.text = "${profileUser.adress}"
                                phoneNumber.text = "${profileUser.phoneNumber}"
                                dateOfBirth.text = "${profileUser.dateBirth}"
                                country.text = "${profileUser.country}"
                                userProfile = profileUser
                            }
                        }
                    }
                }
        }




        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User companies")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button

                        } else {
                            for (document in snapshot.documents) {
                                company = document.toObject<fastfoodPlace>()!!
                                doseCompanyExist = true
                                nameCompanyEt.text = company.fastfoodPlaceName
                                typeActivityEt.text = company.typeFastfood
                                companyAddresEt.text = company.fastFoodAddres
                            }
                            for (document in snapshot.documents) {
                                val fastfood = document.toObject<fastfoodPlace>()!!
                                if (fastfood.latitude == 0.0 && fastfood.longitude == 0.0) {
                                    // do nothing
                                } else {
                                    greenMapsIconTxt.text = "Your company has been placed on the map and is now visible to all app users"
                                }
                            }
                        }
                    }
                }
        }





        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User reviews")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button
                        } else {
                            listOfReviews.clear()
                            for (document in snapshot.documents) {
                                val review = document.toObject<review>()!!
                                listOfReviews.add(review)
                            }

                            // Sort the reviews by date (assuming date is a String)
                            listOfReviews.sortByDescending { it.date?.let { it1 -> dateToMillis(it1) } }
                            myAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }




        saveBtn.setOnClickListener {
            saveUser()
        }

        createBusinessBtn.setOnClickListener {


            val cardNumberText = cardNumber.text.toString()
            val nameCompanyText = nameCompanyEt.text.toString()
            val typeActivityText = typeActivityEt.text.toString()
            val companyAddressText = companyAddresEt.text.toString()

            if (isValidField(cardNumberText) &&
                isValidField(nameCompanyText) &&
                isValidField(typeActivityText) &&
                isValidField(companyAddressText)) {
                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setMessage("Payment in progress...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                // Simulate payment processing for 3 seconds
                android.os.Handler().postDelayed({
                    progressDialog.dismiss()
                    createCompany()
                }, 3000)
            } else {
                Toast.makeText(requireContext(), "There are empty fields or some of them are too short", Toast.LENGTH_SHORT).show()
            }
        }



        saveCompanyChangesBtn.setOnClickListener {
            saveCompanyData()
        }



    }



    private fun isValidField(text: String): Boolean {
        return !TextUtils.isEmpty(text) && text.length >= 4
    }



    fun showAccountDetails() {
        val user = auth.currentUser
        titleTxt.text = "Account details"
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
            nameCompanyEt.visibility = View.GONE
            typeActivityEt.visibility = View.GONE
            companyAddresEt.visibility = View.GONE
            createBusinessBtn.visibility = View.GONE
            recyclerView.visibility = View.GONE
            saveCompanyChangesBtn.visibility = View.GONE
            greenMapsIconTxt.visibility = View.GONE
            greenMapsImg.visibility = View.GONE
            typeTxt.visibility = View.GONE
            companyCostTxt.visibility = View.GONE
        }
    }




    fun showMyReviews() {
        val user = auth.currentUser
        titleTxt.text = "Your reviews"
        if (user != null) {
            recyclerView.visibility = View.VISIBLE

            logInImg.visibility = View.GONE
            logInOrCreateTxt.visibility = View.GONE
            cardNumber.visibility = View.GONE
            nameCompanyEt.visibility = View.GONE
            typeActivityEt.visibility = View.GONE
            companyAddresEt.visibility = View.GONE
            createBusinessBtn.visibility = View.GONE
            nameEt.visibility = View.GONE
            surname.visibility = View.GONE
            country.visibility = View.GONE
            email.visibility = View.GONE
            address.visibility = View.GONE
            dateOfBirth.visibility = View.GONE
            phoneNumber.visibility = View.GONE
            saveBtn.visibility = View.GONE
            saveCompanyChangesBtn.visibility = View.GONE
            greenMapsIconTxt.visibility = View.GONE
            greenMapsImg.visibility = View.GONE
            typeTxt.visibility = View.GONE
            companyCostTxt.visibility = View.GONE
        }
    }



    fun showMyBusiness() {
        val user = auth.currentUser
        titleTxt.text = "My business"
        if (user != null) {
            cardNumber.visibility = View.VISIBLE
            nameCompanyEt.visibility = View.VISIBLE
            typeActivityEt.visibility = View.VISIBLE
            companyAddresEt.visibility = View.VISIBLE
            createBusinessBtn.visibility = View.VISIBLE
            typeTxt.visibility = View.VISIBLE
            companyCostTxt.visibility = View.VISIBLE

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
            recyclerView.visibility = View.GONE
            greenMapsIconTxt.visibility = View.GONE
            greenMapsImg.visibility = View.GONE
            saveCompanyChangesBtn.visibility = View.GONE
            if (doseCompanyExist) {
                greenMapsIconTxt.visibility = View.VISIBLE
                greenMapsImg.visibility = View.VISIBLE
                saveCompanyChangesBtn.visibility = View.VISIBLE
                createBusinessBtn.visibility = View.GONE
                companyCostTxt.visibility = View.GONE
            }
        }
    }


    fun showLogInRequest() {
        val user = auth.currentUser
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
            nameCompanyEt.visibility = View.GONE
            typeActivityEt.visibility = View.GONE
            companyAddresEt.visibility = View.GONE
            createBusinessBtn.visibility = View.GONE
            saveCompanyChangesBtn.visibility = View.GONE
            greenMapsIconTxt.visibility = View.GONE
            greenMapsImg.visibility = View.GONE
            typeTxt.visibility = View.GONE
            companyCostTxt.visibility = View.GONE
        }
    }




    fun saveUser() {

        val user = auth.currentUser

            val saveProfileUserChanges = userProfile(name = nameEt.text.toString(),
            surname = surname.text.toString(), email = email.text.toString(),
            adress = address.text.toString(), phoneNumber = phoneNumber.text.toString(),
            country = country.text.toString(), dateBirth = dateOfBirth.text.toString(),
                profileUser.userId)



        // Get a reference to the Firestore collection
        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Users").collection(user.uid)
                .document("User profile").set(saveProfileUserChanges)
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





    fun saveCompanyData() {

        val user = auth.currentUser

        company = fastfoodPlace(company.latitude, company.longitude,
            nameCompanyEt.text.toString(), "",
            typeActivityEt.text.toString(), companyAddresEt.text.toString(),
            profileUser.userId)

        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Fastfood places")
                .collection("All")
                .document(profileUser.userId).set(company)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Company data updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User companies")
                .document(profileUser.userId).set(company)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Company data updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }



        }



    }




    fun createCompany() {
        val user = auth.currentUser


            company = fastfoodPlace(0.0, 0.0,
            nameCompanyEt.text.toString(), "",
            typeActivityEt.text.toString(), companyAddresEt.text.toString(),
            profileUser.userId)



        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Fastfood places")
                .collection("All")
                .document(profileUser.userId).set(company)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Company created!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User companies")
                .document(profileUser.userId).set(company)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Company created!", Toast.LENGTH_SHORT).show()
                    cardNumber.text = ""
                    showMyBusiness()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }



        }



    }




    override fun onViewClick(reviews: review) {

        if (isNetworkConnected()) {

            var selectedCompanyId = ""

            for (review in listOfReviews) {
                if (reviews.receivingCompanyId == review.receivingCompanyId) {
                    selectedCompanyId = reviews.receivingCompanyId
                }
            }

            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.dialog_confirmation, null)

            builder.setView(dialogView)
            val alertDialog = builder.create()

            // Set custom background for the dialog
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Find views in the custom dialog layout
            val messageTextView: TextView = dialogView.findViewById(R.id.messageTextView)
            val yesButton: Button = dialogView.findViewById(R.id.yesButton)
            val noButton: Button = dialogView.findViewById(R.id.noButton)

            // Set the message
            messageTextView.text = "Are you sure you want to delete this item?"

            // Set click listeners
            yesButton.setOnClickListener {

                alertDialog.dismiss()

                val user = auth.currentUser

                if (user != null) {
                    database.collection("Hotdog Expres")
                        .document("Fastfood places").collection("All")
                        .document(selectedCompanyId).collection("Company reviews")
                        .document(userProfile.userId).delete()
                        .addOnSuccessListener { documentReference ->
                            // Document added successfully
                            Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT)
                                .show()
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



                    database.collection("Hotdog Expres").document("Users")
                        .collection(user.uid).document("User profile")
                        .collection("User reviews")
                        .document(selectedCompanyId).delete()
                        .addOnSuccessListener { documentReference ->
                            // Document added successfully
                            Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT)
                                .show()
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
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }

            noButton.setOnClickListener {
                // User clicked No, dismiss the dialog
                alertDialog.dismiss()
            }

            alertDialog.show()
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }


    }



    private fun isNetworkConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }



    private fun dateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0
    }



}