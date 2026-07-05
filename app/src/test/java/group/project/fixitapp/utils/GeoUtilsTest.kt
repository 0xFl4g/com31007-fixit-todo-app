package group.project.fixitapp.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class GeoUtilsTest {

    @Test
    fun `zero distance for identical coordinates`() {
        assertEquals(0f, haversineDistanceMetres(53.3811, -1.4701, 53.3811, -1.4701), 0.01f)
    }

    @Test
    fun `one degree of latitude is about 111 km`() {
        // 53.0N to 54.0N along the same meridian
        val distance = haversineDistanceMetres(53.0, -1.47, 54.0, -1.47)
        assertEquals(111_195f, distance, 200f)
    }

    @Test
    fun `longitude degrees shrink with latitude`() {
        // 1 degree of longitude at 53.38N is ~66.4 km, not 111 km
        val distance = haversineDistanceMetres(53.3811, -1.4701, 53.3811, -0.4701)
        assertEquals(66_350f, distance, 300f)
    }

    @Test
    fun `known city pair distance`() {
        // Sheffield (53.3811, -1.4701) to Manchester (53.4808, -2.2426) ~= 52.5 km
        val distance = haversineDistanceMetres(53.3811, -1.4701, 53.4808, -2.2426)
        assertEquals(52_400f, distance, 500f)
    }

    @Test
    fun `short distances are accurate to the metre`() {
        // ~100 m north of the starting point (0.0009 degrees of latitude)
        val distance = haversineDistanceMetres(53.3811, -1.4701, 53.3820, -1.4701)
        assertEquals(100f, distance, 1f)
    }
}
