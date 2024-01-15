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
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.hotdogexpres.classes.fastfoodPlace
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


class MapsFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var textViewFastFoodName: TextView
    lateinit var whiteRectangleImg: ImageView
    lateinit var takeMeThereBtn: Button
    lateinit var typeFastFoodPlaceTxt: TextView
    lateinit var addresTxt: TextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val hotDogTruckMarkers = mutableListOf<fastfoodPlace>()
    var fastFoodPlace = fastfoodPlace(0.0, 0.0,
        "", "", "", "")
    var fastFoodPlaceFetchedData = fastfoodPlace(0.0, 0.0,
        "", "", "", "")

    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseFirestore

    private lateinit var destinationLatLng: LatLng
    var userProfile = userProfile("", "", "",
        "", "", "", "", 0)

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



        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        textViewFastFoodName = view.findViewById(R.id.textViewFastFoodName)
        whiteRectangleImg = view.findViewById(R.id.whiteRectangleImg)
        takeMeThereBtn = view.findViewById(R.id.takeMeThereBtn)
        typeFastFoodPlaceTxt = view.findViewById(R.id.typeFastFoodPlaceTxt)
        addresTxt = view.findViewById(R.id.addresTxt)

        // Check and request location permission
        checkLocationPermission()

        // Initialize the map asynchronously
        mapView.getMapAsync { googleMap ->
            // Customize your map settings here
            googleMap.uiSettings.isZoomControlsEnabled = true
            // Enable the user's location on the map
            googleMap.isMyLocationEnabled = true

            // Set a listener for the location button
            googleMap.setOnMyLocationButtonClickListener {
                // Handle the location button click
                // Show the user's location on the map without adding a new marker
                showCurrentLocation(googleMap)
                true
            }

            // Set a listener for map clicks
            googleMap.setOnMapClickListener { latLng ->
                // Add a new HotDogTruck marker to the list and display a red marker
                    fastFoodPlace = fastfoodPlace(latLng.latitude, latLng.longitude,
                    fastFoodPlaceFetchedData.fastfoodPlaceName,
                        "",
                    fastFoodPlaceFetchedData.typeFastfood,
                        fastFoodPlaceFetchedData.fastFoodAddres)

                if (userProfile.checkmarksAvailable > 0 && user != null) {
                    saveFastfoodPlace()
                }
               // addRedMarker(googleMap, hotDogTruck)
            }




            database.collection("Hotdog Expres")
                .document("Fastfood places")
                .collection("All")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {

                        hotDogTruckMarkers.clear()

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button
                            googleMap.clear()

                        } else {
                            for (document in snapshot.documents) {
                                val fastfood = document.toObject<fastfoodPlace>()!!
                                hotDogTruckMarkers.add(fastfood)
                            }

                            googleMap.clear()

                            hotDogTruckMarkers.forEach { hotDogTruck ->
                                addRedMarker(googleMap, hotDogTruck)
                            }
                        }
                    }
                }



            if (user != null) {
                database.collection("Hotdog Expres").document("Users")
                    .collection(user.uid)
                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            if (snapshot.isEmpty) {
                                // No documents in the snapshot, enable the button

                            } else {
                                for (document in snapshot.documents) {
                                    val profileUser = document.toObject<userProfile>()!!
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
                                    val business = document.toObject<fastfoodPlace>()!!
                                    fastFoodPlaceFetchedData = business
                                }
                            }
                        }
                    }
            }






            googleMap.setOnMarkerClickListener { marker ->
                val title = marker.title
                for (place in hotDogTruckMarkers) {
                    if (place.fastfoodPlaceName == title) {
                        textViewFastFoodName.text = "$title"
                        addresTxt.text = "Addres: ${place.fastFoodAddres}"  // Use 'place' instead of 'fastFoodPlace'
                        typeFastFoodPlaceTxt.text = "Type: ${place.typeFastfood}"

                        whiteRectangleImg.visibility = View.VISIBLE
                        textViewFastFoodName.visibility = View.VISIBLE
                        addresTxt.visibility = View.VISIBLE
                        typeFastFoodPlaceTxt.visibility = View.VISIBLE
                        takeMeThereBtn.visibility = View.VISIBLE

                        destinationLatLng = marker.position
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



            /*
                        // Display existing markers on the map
                        hotDogTruckMarkers.forEach { hotDogTruck ->
                            addRedMarker(googleMap, hotDogTruck)
                        }

             */
        }
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


    private fun addRedMarker(googleMap: GoogleMap, hotDogTruck: fastfoodPlace) {
        // Create a LatLng object from HotDogTruck
        val latLng = LatLng(hotDogTruck.latitude, hotDogTruck.longitude)

        // Add a red marker at the specified location with a title
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(hotDogTruck.fastfoodPlaceName)  // Set title for the marker
        )
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



    fun saveFastfoodPlace() {

        val user = auth.currentUser


        // Get a reference to the Firestore collection
            database.collection("Hotdog Expres")
                .document("Fastfood places").collection("All")
                .add(fastFoodPlace)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    Toast.makeText(requireContext(), "Place saved!", Toast.LENGTH_SHORT).show()
                    saveUser()
                }
                .addOnFailureListener { e ->
                    // Error adding document
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }
    }



    fun saveUser() {

        val user = auth.currentUser

        userProfile.checkmarksAvailable --

        // Get a reference to the Firestore collection
        if (user != null) {
            database.collection("Hotdog Expres")
                .document("Users").collection(user.uid)
                .document("User profile").set(userProfile)
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


