package com.submision.coursestory.view.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.submision.coursestory.R
import com.submision.coursestory.data.response.AllStoriesResponse
import com.submision.coursestory.data.response.ListStoryItem
import com.submision.coursestory.databinding.ActivityMapsBinding
import com.submision.coursestory.view.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels { ViewModelFactory.getInstance(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()

        observeStoriesWithLocation()

        val yogyakarta = LatLng(-7.797068, 110.370529) // Koordinat Yogyakarta
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yogyakarta, 12f))
    }

    private fun observeStoriesWithLocation() {
        mapsViewModel.storiesWithLocation.observe(this, Observer { stories ->
            if (stories != null && stories.isNotEmpty()) {
                Log.d(TAG, "Loaded ${stories.size} stories with location")
                addMarkersToMap(stories)
            } else {
                Log.d(TAG, "No stories with location found.")
            }
        })

        // Trigger fetching stories with location
        mapsViewModel.fetchStoriesWithLocation(1)
    }

    private fun addMarkersToMap(stories: List<ListStoryItem>) {
        val boundsBuilder = LatLngBounds.Builder()

        stories.forEach { story ->
            val latLng = story.lat?.let { story.lon?.let { it1 -> LatLng(it, it1) } }
            latLng?.let {
                MarkerOptions()
                    .position(it)
                    .title(story.name)
                    .snippet(story.description)
            }?.let {
                mMap.addMarker(
                    it
                )
            }
        }

//        stories.forEach { story ->
//            val lat = story.lat
//            val lon = story.lon
//            if (lat == null || lon == null) {
//                Log.e(TAG, "Story ${story.name} is missing lat or lon")
//                return@forEach
//            }
//            mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(lat, lon))
//                    .title(story.name)
//                    .snippet(story.description)
//            )
//            boundsBuilder.include(LatLng(lat, lon))
//        }

        // Zoom the map to show all markers
        val bounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
