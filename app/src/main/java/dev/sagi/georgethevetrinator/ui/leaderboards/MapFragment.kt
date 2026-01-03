import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.MyApp
import dev.sagi.georgethevetrinator.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout with the SupportMapFragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Find the map fragment and start it
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        refreshMarkers()
    }

    fun refreshMarkers() {
        // 1. Fetch scores from your manager
        val scores = (requireActivity().application as MyApp).scoreRepository.getTopScores()

        // 2. Clear any existing markers just in case
        googleMap?.clear()

        // 3. Add a marker for every score in the list
        for (score in scores) {
            val position = LatLng(score.latitude, score.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(score.playerName) // Shows name when clicked
                    .snippet("Score: ${score.score}") // Shows score below name
            )
        }

        // 4. Zoom to the #1 rank location initially
        if (scores.isNotEmpty()) {
            val topScorePos = LatLng(scores[0].latitude, scores[0].longitude)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(topScorePos, 5f))
        }
    }

    fun zoomToLocation(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        // animateCamera provides a smooth gliding effect
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}