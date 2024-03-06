package com.example.weather.ui.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(),OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var binding:FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater,container,false)
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        /*googleMap.setOnMarkerDragListener(object:GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {
                Log.i("TAG", "onMarkerDrag: Marker is being dragged ")
            }

            override fun onMarkerDragEnd(p0: Marker) {
                Log.i("TAG", "onMarkerDragEnd: Marker after dragged ${p0.position.longitude} ->${p0.position.latitude}")
            }

            override fun onMarkerDragStart(p0: Marker) {
                Log.i("TAG", "onMarkerDragStart:start dragging ")
            }

        })*/
        googleMap.setOnMapClickListener {
            latLng->
            val markerOptions=MarkerOptions().position(latLng).title("Drooped Bin").draggable(true)
            googleMap.clear()
            googleMap.addMarker(markerOptions)

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            println("Latitude: ${latLng.latitude}, Longitude: ${latLng.longitude}")
        }
    }

}