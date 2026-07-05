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
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.utils.GEO_NOTIFICATION_ID_OFFSET
import group.project.fixitapp.utils.NOTIFICATION_CHANNEL_ID
import group.project.fixitapp.utils.createNotification
import group.project.fixitapp.utils.haversineDistanceMetres
import group.project.fixitapp.utils.showNotification
import kotlinx.coroutines.launch

private const val GEO_NOTIFY_RADIUS_METRES = 100f

class LocationViewModel(app: Application) : AndroidViewModel(app), LocationListener {
    private var locationManager: LocationManager =
        app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val taskDao = AppDatabase.getDatabase(app).taskDAO()
    private val settingDao = AppDatabase.getDatabase(app).settingDAO()

    // Tasks already notified about this session, so we don't re-notify on every update
    private val geoNotifiedTaskIds = mutableSetOf<Int>()

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
        notifyNearbyTasks(newLocation)
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.removeUpdates(this)
        isTracking = false
    }

    private fun notifyNearbyTasks(currentLocation: Location) {
        viewModelScope.launch {
            if (settingDao.getSettingByName("notifyGeoLocatedTasks")?.isActive == false) return@launch

            for (task in taskDao.getIncompleteTasksWithLocation()) {
                val taskId = task.id ?: continue
                if (taskId in geoNotifiedTaskIds) continue

                val distance = haversineDistanceMetres(
                    currentLocation.latitude, currentLocation.longitude,
                    task.latitude!!, task.longitude!!
                )
                if (distance <= GEO_NOTIFY_RADIUS_METRES) {
                    geoNotifiedTaskIds += taskId
                    val notification = createNotification(
                        getApplication(),
                        NOTIFICATION_CHANNEL_ID,
                        task.title,
                        "You are near this task's location"
                    )
                    showNotification(
                        getApplication(),
                        GEO_NOTIFICATION_ID_OFFSET + taskId,
                        notification
                    )
                }
            }
        }
    }
}
