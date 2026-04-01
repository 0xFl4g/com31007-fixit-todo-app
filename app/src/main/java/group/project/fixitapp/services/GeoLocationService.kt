package group.project.fixitapp.services

import android.location.Location
import android.location.LocationListener
import group.project.fixitapp.ui.viewmodel.LocationViewModel

object GeoLocationService : LocationListener {
    var locationViewModel: LocationViewModel? = null

    override fun onLocationChanged(newLocation: Location) {
        locationViewModel?.onLocationChanged(newLocation)
    }

    fun updateLatestLocation(latestLocation: Location) {
        locationViewModel?.onLocationChanged(latestLocation)
    }

}
