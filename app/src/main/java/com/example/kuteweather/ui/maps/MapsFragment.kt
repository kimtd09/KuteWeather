package com.example.kuteweather.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.kuteweather.R
import com.example.kuteweather.databinding.FragmentMapsBinding
import com.example.kuteweather.db.AppRepository
import com.example.kuteweather.ui.favorites.FavoritesModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding : FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapsViewModel
    private lateinit var googleMap: GoogleMap
    private var repository : AppRepository? = null
    private var favoritesSet : MutableLiveData<ArrayList<FavoritesModel>> = MutableLiveData()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        favoritesSet.observe(viewLifecycleOwner) {
            it.forEach{
                val lieu = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(lieu).title(it.city).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                /*
                float   HUE_AZURE
                float   HUE_BLUE
                float   HUE_CYAN
                float   HUE_GREEN
                float   HUE_MAGENTA
                float   HUE_ORANGE
                float   HUE_RED
                float   HUE_ROSE
                float   HUE_VIOLET
                float   HUE_YELLOW
                 */
            }
        }

        googleMap.setOnMapClickListener {
            googleMap.clear()
            addFavoritesMarkers()
            googleMap.addMarker(MarkerOptions().position(it))
            viewModel.coordinates.value = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = viewModel

        binding.button.setOnClickListener {
            //Toast.makeText(context, "coordinates=${viewModel.coordinates.value}",Toast.LENGTH_SHORT).show()
            Navigation.findNavController(binding.root).navigate(MapsFragmentDirections.actionNavigationMapsToNavigationHome(viewModel.getParcelable()))
        }

        repository = context?.let { AppRepository.getInstance(it) }

        repository?.observable?.observe(viewLifecycleOwner) {
            favoritesSet.value = ArrayList(it)
        }

        return binding.root
    }

    private fun addFavoritesMarkers() {
        favoritesSet.value?.forEach {
            val lieu = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(MarkerOptions().position(lieu).title(it.city).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}