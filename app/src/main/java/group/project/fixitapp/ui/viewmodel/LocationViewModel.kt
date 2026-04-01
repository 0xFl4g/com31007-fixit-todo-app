package group.project.fixitapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(app: Application) : AndroidViewModel(app), LocationListener {
    private var locationManager: LocationManager =
        app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var isTracking by mutableStateOf(false)
    private var _location: Location? = null
    private var location: Location?
        get() = _location
        set(value) {
            _location = value
            latitude = value?.latitude
            longitude = value?.longitude
        }
    var latitude by mutableStateOf<Double?>(null)
    var longitude by mutableStateOf<Double?>(null)

    init {
        // Start fetching location on initialization
        fetchLocation()
    }

    fun fetchLocation() {
        try {
            // Request location updates. Handle permissions appropriately.
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L, // minTime in milliseconds
                0f, // minDistance in meters
                this
            )
            isTracking = true
        } catch (ex: SecurityException) {
            // Handle if permissions are not granted
        }
    }

    override fun onLocationChanged(newLocation: Location) {
        location = newLocation // Now using the property setter
    }

}
