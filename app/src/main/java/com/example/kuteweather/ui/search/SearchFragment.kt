package com.example.kuteweather.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.kuteweather.BuildConfig
import com.example.kuteweather.MainViewModel
import com.example.kuteweather.R
import com.example.kuteweather.databinding.FragmentSearchBinding
import com.example.kuteweather.db.AppRepository
import com.example.kuteweather.ui.favorites.FavoritesModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var repository : AppRepository? = null
    private val sharedViewModel: MainViewModel by activityViewModels()

    val TAG = "AppDebug"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AppDebug", sharedViewModel.toString())
        val searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.resetData()
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = searchViewModel
        repository = context?.let { AppRepository.getInstance(it) }

        binding.buttonShowWeather.setOnClickListener {
            val parcelable = FavoritesModel(searchViewModel.city.value!!,
                searchViewModel.countryShortName.value!!,
                searchViewModel.latLng.value?.latitude!!,
                searchViewModel.latLng.value?.longitude!!)
            Navigation.findNavController(it).navigate(
                SearchFragmentDirections.actionNavigationSearchToNavigationHome(parcelable)
            )
        }

        binding.imageviewFavoriteIcon.setOnClickListener{
            //TODO do not add if empty
            if(!searchViewModel.city.value.isNullOrEmpty()) {
                val city = FavoritesModel(
                    searchViewModel.city.value!!,
                    searchViewModel.countryShortName.value!!,
                    searchViewModel.latLng.value?.latitude!!,
                    searchViewModel.latLng.value?.longitude!!
                )

                if (searchViewModel.isFavorite.value == true) {
                    searchViewModel.isFavorite.value = false
                    repository?.deleteCity(city)
                } else {
                    searchViewModel.isFavorite.value = true
                    repository?.addData(city)
                }
            }
        }

        searchViewModel.latLng.observe(viewLifecycleOwner) {
            if(it.latitude.equals(0.0) && it.longitude.equals(0.0))
                binding.textviewLatlng.text = ""
            else
                binding.textviewLatlng.text = "${it.latitude},${it.longitude}"
        }

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.US);
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

//         Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                searchViewModel.city.value = place.name
                searchViewModel.latLng.value = place.latLng

                for (component in place.addressComponents.asList()) {
                    if (component.types.contains("country")) {
                        searchViewModel.country.value = component.name
                        searchViewModel.countryShortName.value = "(${component.shortName})"
                    }
                }

                // check if it is stored in local database
                if (repository != null) {
                    CoroutineScope(Dispatchers.Default).launch{
                        searchViewModel.isFavorite.postValue(repository!!.getCity(place.name) != null)
                    }
                }
            }

            override fun onError(status: Status) {
                Log.d(TAG, "An error occurred: $status")
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}