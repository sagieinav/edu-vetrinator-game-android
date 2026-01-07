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
import dev.sagi.georgethevetrinator.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(binding.googleMap.id) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        refreshMarkers()
    }

    fun refreshMarkers() {
        // 1. Fetch scores from the repository and clear map (safety layer)
        val scores = (requireActivity().application as MyApp).scoreRepository.getTopScores()
        googleMap?.clear()

        // 2. Add a marker for every score in the list
        for (score in scores) {
            val position = LatLng(score.latitude, score.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(score.playerName)
                    .snippet("Score: ${score.score}")
            )
        }
    }

    fun zoomToLocation(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 4. Nullify binding to prevent memory leaks
        _binding = null
    }
}