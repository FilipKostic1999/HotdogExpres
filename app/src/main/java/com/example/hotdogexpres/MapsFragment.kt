package com.example.hotdogexpres

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.adapters.reviewAdapter
import com.example.hotdogexpres.classes.fastfoodPlace
import com.example.hotdogexpres.classes.mapSettings
import com.example.hotdogexpres.classes.review
import com.example.hotdogexpres.classes.userProfile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MapsFragment : Fragment(), reviewAdapter.OnViewClickListener {

    private lateinit var mapView: MapView
    private lateinit var textViewFastFoodName: TextView
    lateinit var whiteRectangleImg: ImageView
    lateinit var takeMeThereBtn: Button
    lateinit var typeFastFoodPlaceTxt: TextView
    lateinit var addresTxt: TextView
    lateinit var settingsImg: ImageView
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val hotDogTruckMarkers = mutableListOf<fastfoodPlace>()
    var fastFoodPlace = fastfoodPlace(0.0, 0.0,
        "", "", "",
        "", "")
   var lat = 0.0
    var long = 0.0

    lateinit var fastFoodPlaceFetchedData: fastfoodPlace
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: reviewAdapter
    private lateinit var listOfReviews : ArrayList<review>
    lateinit var reviewItem : review
    lateinit var writeReviewBtn: Button
    lateinit var ratingBar: RatingBar
    var selectedCompanyId = "id"
    var review = review("Alex", "Good place",
        3.14f, "")
    var mapSettings = mapSettings(true, "")

    private var snapshotListenerCounter = 0
    var isSwitchEnabled = true


    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore

    private lateinit var destinationLatLng: LatLng
    var userProfile = userProfile("", "", "",
        "", "", "", "", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser


        recyclerView = view.findViewById(R.id.reviewRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        listOfReviews = arrayListOf()
        myAdapter = reviewAdapter(userProfile.userId,listOfReviews)
        recyclerView.adapter = myAdapter
        myAdapter.setOnViewClickListener(this)
       // myAdapter.setOnViewClickListener(requireContext())

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        textViewFastFoodName = view.findViewById(R.id.textViewFastFoodName)
        whiteRectangleImg = view.findViewById(R.id.whiteRectangleImg)
        takeMeThereBtn = view.findViewById(R.id.takeMeThereBtn)
        typeFastFoodPlaceTxt = view.findViewById(R.id.typeFastFoodPlaceTxt)
        addresTxt = view.findViewById(R.id.addresTxt)
        settingsImg = view.findViewById(R.id.settingsImg)
        writeReviewBtn = view.findViewById(R.id.writeReviewBtn)
        ratingBar = view.findViewById(R.id.ratingBar)

        // Check and request location permission
        checkLocationPermission()

        // Initialize the map asynchronously
        mapView.getMapAsync { googleMap ->
            // Customize your map settings here
            googleMap.uiSettings.isZoomControlsEnabled = true
            // Enable the user's location on the map
            googleMap.isMyLocationEnabled = true



            googleMap.setOnMyLocationButtonClickListener {
                showCurrentLocation(googleMap)
                true
            }


            googleMap.setOnMapClickListener { latLng ->

                lat = latLng.latitude
                long = latLng.longitude

                listOfReviews.clear()
                myAdapter.notifyDataSetChanged()

                if (user != null) {
                    if (isSwitchEnabled) {
                        saveFastfoodPlace()
                    } else {
                        Toast.makeText(requireContext(), "Company is set as not editable on the map by settings!", Toast.LENGTH_SHORT).show()
                    }
                }
            }



            writeReviewBtn.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.dialog_write_new_review, null)

                builder.setView(dialogView)

                // Find the RatingBar in the dialogView
                val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

                // Set up the buttons
                builder.setPositiveButton("Save") { _, _ ->
                    val inputView = dialogView.findViewById<EditText>(R.id.reviewEditText)
                    val rating = ratingBar.rating
                    saveReview(inputView, rating)
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    // Cancel the review writing
                    dialog.dismiss()
                }

                // Create and show the dialog
                val dialog = builder.create()
                dialog.show()
            }







            database.collection("Hotdog Expres")
                .document("Fastfood places")
                .collection("All")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {

                        hotDogTruckMarkers.clear()
                        googleMap.clear()
                        snapshotListenerCounter = 0

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button
                            googleMap.clear()

                        } else {
                            for (document in snapshot.documents) {
                                val fastfood = document.toObject<fastfoodPlace>()!!
                                if (fastfood.latitude == 0.0 && fastfood.longitude == 0.0) {
                                    snapshotListenerCounter++
                                } else {
                                    hotDogTruckMarkers.add(fastfood)
                                    snapshotListenerCounter++
                                }
                            }

                            googleMap.clear()

                            val scope = CoroutineScope(Dispatchers.Main)

                            scope.launch {
                                if (snapshotListenerCounter == snapshot.size()) {
                                    hotDogTruckMarkers.forEach { hotDogTruck ->
                                        addMarkers(googleMap, hotDogTruckMarkers)
                                    }
                                }
                                delay(1000)
                            }
                        }
                    }
                }





            if (user != null) {
                database.collection("Hotdog Expres").document("Users")
                    .collection(user.uid)
                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            var documentCounter = 0

                            if (snapshot.isEmpty) {
                                // No documents in the snapshot, enable the button
                            } else {
                                for (document in snapshot.documents) {
                                    val profileUser = document.toObject<userProfile>()!!
                                    userProfile = profileUser
                                    documentCounter ++
                                    // Update the adapter to pass the review id after fetching
                                    myAdapter = reviewAdapter(userProfile.userId,listOfReviews)
                                    recyclerView.adapter = myAdapter
                                    myAdapter.setOnViewClickListener(this)
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
                                    val business = document.toObject<fastfoodPlace>()!!
                                    fastFoodPlaceFetchedData = business
                                }
                            }
                        }
                    }
            }




            if (user != null) {
                database.collection("Hotdog Expres").document("Users")
                    .collection(user.uid).document("User profile")
                    .collection("Map settings")
                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            if (snapshot.isEmpty) {
                                // No documents in the snapshot, enable the button

                            } else {
                                for (document in snapshot.documents) {
                                    mapSettings = document.toObject<mapSettings>()!!
                                    isSwitchEnabled = mapSettings.isCompanyPlacable
                                }
                            }
                        }
                    }
            }





            settingsImg.setOnClickListener {
                showSettingsDialog(isSwitchEnabled)
            }




            googleMap.setOnMarkerClickListener { marker ->

                listOfReviews.clear()
                myAdapter.notifyDataSetChanged()

                val title = marker.title
                val fastfoodId = marker.snippet

                for (place in hotDogTruckMarkers) {
                    if (place.documentId == fastfoodId) {
                        textViewFastFoodName.text = "$title"
                        addresTxt.text = "${place.fastFoodAddres}"  // Use 'place' instead of 'fastFoodPlace'
                        typeFastFoodPlaceTxt.text = "Type: ${place.typeFastfood}"
                        selectedCompanyId = place.documentId
                        Toast.makeText(requireContext(), selectedCompanyId, Toast.LENGTH_SHORT).show()

                        whiteRectangleImg.visibility = View.VISIBLE
                        textViewFastFoodName.visibility = View.VISIBLE
                        addresTxt.visibility = View.VISIBLE
                        typeFastFoodPlaceTxt.visibility = View.VISIBLE
                        takeMeThereBtn.visibility = View.VISIBLE

                        destinationLatLng = marker.position
                        getReviews()
                    }
                }
                true
            }







            takeMeThereBtn.setOnClickListener {
                // Check if destinationLatLng is initialized
                if (::destinationLatLng.isInitialized) {
                    // Open Google Maps with the selected marker's coordinates
                    val gmmIntentUri = Uri.parse("google.navigation:q=${destinationLatLng.latitude},${destinationLatLng.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    // Check if there's a Google Maps app installed
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        // Inform the user that Google Maps is not installed
                        Toast.makeText(requireContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Inform the user to select a marker first
                    Toast.makeText(requireContext(), "Please select a marker first", Toast.LENGTH_SHORT).show()
                }
            }





        }
    }

    private fun showSettingsDialog(initialSwitchState: Boolean) {
        val builder = AlertDialog.Builder(requireContext())

        // Inflate the layout for the dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.show_settings_dialog, null)
        builder.setView(dialogView)

        // Find the Switch in the inflated layout
        val switchButton: Switch = dialogView.findViewById(R.id.toogle)
        val saveChangesButton: Button = dialogView.findViewById(R.id.saveChangesButton)
        val removeCompany: Button = dialogView.findViewById(R.id.removeCompany)

        // Set the initial switch state
        switchButton.isChecked = initialSwitchState


        val dialog = builder.create()

        saveChangesButton.setOnClickListener {
            val updatedSwitchState = switchButton.isChecked
            saveSwitchStateToFirebase(updatedSwitchState)
            dialog.dismiss()
        }

        removeCompany.setOnClickListener {
            val user = auth.currentUser
            for (place in hotDogTruckMarkers) {
                if (place.documentId ==
                    fastFoodPlaceFetchedData.documentId) {
                    if (user != null) {
                        database.collection("Hotdog Expres")
                            .document("Fastfood places")
                            .collection("All")
                            .document(place.documentId)
                            .delete()
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(requireContext(), "Company removed from map!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                // Error adding document
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                            }

                        fastFoodPlaceFetchedData.latitude = 0.0
                        fastFoodPlaceFetchedData.longitude = 0.0

                        database.collection("Hotdog Expres").document("Users")
                            .collection(user.uid).document("User profile")
                            .collection("User companies")
                            .document(userProfile.userId)
                            .set(fastFoodPlaceFetchedData)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(requireContext(), "Company removed from map!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                // Error adding document
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                            }


                    }
                }
            }
        }


        // Set up any additional logic or event listeners here

        // Create and show the AlertDialog
        dialog.show()
    }




    private fun saveSwitchStateToFirebase(updatedSwitchState: Boolean) {
        val user = auth.currentUser
        mapSettings.isCompanyPlacable = updatedSwitchState

        if (user != null) {
            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("Map settings")
                .document(userProfile.userId).set(mapSettings)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show()
                }

        }

    }







    override fun onViewClick(reviews: review) {
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
                            Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Error adding document
                            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }



                    database.collection("Hotdog Expres").document("Users")
                        .collection(user.uid).document("User profile")
                        .collection("User reviews")
                        .document(selectedCompanyId).delete()
                        .addOnSuccessListener { documentReference ->
                            // Document added successfully
                            Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Error adding document
                            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
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
        }



    private fun showCurrentLocation(googleMap: GoogleMap) {
        // Get the last known location using FusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                // Create a LatLng object from the location
                val latLng = LatLng(location.latitude, location.longitude)

                // Add a marker at the current location without clearing existing markers
                val markerOptions = MarkerOptions().position(latLng).title("Current Location")
                val marker: Marker? = googleMap.addMarker(markerOptions)

                // Move the camera to the current location and zoom in
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Show an info window for the marker
                marker?.showInfoWindow()
            }
        }
    }


    private fun addMarkers(googleMap: GoogleMap, hotDogTruckMarkers: List<fastfoodPlace>) {

            for (hotDogTruck in hotDogTruckMarkers) {
                // Create a LatLng object from HotDogTruck
                val latLng = LatLng(hotDogTruck.latitude, hotDogTruck.longitude)
                var markerColor = BitmapDescriptorFactory.HUE_RED

                if (userProfile.userId == hotDogTruck.documentId) {
                    markerColor = BitmapDescriptorFactory.HUE_GREEN
                }

                // Add a single marker based on the determined color
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        .title(hotDogTruck.fastfoodPlaceName)
                        .snippet(hotDogTruck.documentId)// Set title for the marker
                )
            }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapView.getMapAsync { googleMap ->
                        // Customize your map settings here
                        googleMap.uiSettings.isZoomControlsEnabled = true
                        // Add markers, set camera position, etc.
                    }
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                }
            }
        }
    }


    // Remember to call the corresponding lifecycle methods for the MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun saveFastfoodPlace() {

        val user = auth.currentUser

        if (user != null) {

            fastFoodPlace.latitude = lat
            fastFoodPlace.longitude = long
            fastFoodPlace.documentId = userProfile.userId
            fastFoodPlace.fastfoodPlaceName = fastFoodPlaceFetchedData.fastfoodPlaceName
            fastFoodPlace.typeFastfood = fastFoodPlaceFetchedData.typeFastfood
            fastFoodPlace.fastFoodAddres = fastFoodPlaceFetchedData.fastFoodAddres

            // Get a reference to the Firestore collection
            database.collection("Hotdog Expres")
                .document("Fastfood places").collection("All")
                .document(userProfile.userId)
                .set(fastFoodPlace)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Place saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }



            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User companies")
                .document(userProfile.userId)
                .set(fastFoodPlace)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Place saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }



        }
    }


    private fun dateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0
    }





    fun getReviews() {
        database.collection("Hotdog Expres")
            .document("Fastfood places")
            .collection("All")
            .document(selectedCompanyId)
            .collection("Company reviews")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No documents in the snapshot, handle accordingly
                    listOfReviews.clear()
                    ratingBar.rating = 0f
                    myAdapter.notifyDataSetChanged()
                } else {
                    listOfReviews.clear()
                    var totalReviews = 0
                    var summedRatings = 0f
                    for (document in documents) {
                        val review = document.toObject<review>()
                        totalReviews++
                        summedRatings += review.reviewRating
                        listOfReviews.add(review)
                    }
                    val totalRating = summedRatings/totalReviews
                    ratingBar.rating = totalRating
                    listOfReviews.sortByDescending { it.date?.let { it1 -> dateToMillis(it1) } }
                    myAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }

    }




    private fun saveReview(inputView: EditText, rating: Float) {
        // Retrieve user input from the inputView
        val user = auth.currentUser
        val reviewText = inputView.text.toString()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        var review = review(userProfile.name, reviewText,
            rating, userProfile.userId, currentDate, selectedCompanyId)


        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Fastfood places").collection("All")
                .document(selectedCompanyId).collection("Company reviews")
                .document(userProfile.userId).set(review)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }



          //  val reviewId = System.currentTimeMillis().toString() + (0..99999999).random().toString()
            review = review(userProfile.name, reviewText,
                rating, selectedCompanyId, currentDate, selectedCompanyId)

            database.collection("Hotdog Expres").document("Users")
                .collection(user.uid).document("User profile")
                .collection("User reviews")
                .document(selectedCompanyId).set(review)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Review saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }


        }
    }





}


