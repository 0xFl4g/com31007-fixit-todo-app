package group.project.fixitapp.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METRES = 6_371_000.0

/**
 * Great-circle distance between two coordinates using the haversine formula.
 */
fun haversineDistanceMetres(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    return (2 * EARTH_RADIUS_METRES * asin(sqrt(a))).toFloat()
}
