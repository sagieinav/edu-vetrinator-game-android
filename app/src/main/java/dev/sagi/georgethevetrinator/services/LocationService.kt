package dev.sagi.georgethevetrinator.services

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import android.location.Location

class LocationService(context: Context) {
    private val appContext = context.applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onResult: (lat: Double, lon: Double) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            onResult(lat, lon)
        }.addOnFailureListener {
            onResult(0.0, 0.0)
        }
    }
}