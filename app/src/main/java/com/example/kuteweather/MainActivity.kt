package com.example.kuteweather

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.kuteweather.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity(), LocationListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initPreferences()
        updateLanguage(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
        supportActionBar?.hide()

//        val locationPermissionRequest = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            when {
//                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                    // Precise location access granted.
//                    Log.d(Constants.DEBUG_LOG, "Access Fine Location granted")
//                }
//                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                    // Only approximate location access granted.
//                    Log.d(Constants.DEBUG_LOG, "Access Coarse Location granted")
//
//                } else -> {
//                // No location access granted.
//                    Log.d(Constants.DEBUG_LOG, "Access Coarse Location denied")
//            }
//            }
//        }

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
//        locationPermissionRequest.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION))

        //doGeolocation()
    }

    private fun initPreferences() {
        viewModel.lang.value = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.key_preference_language), "")!!
        viewModel.isCelsius.value  = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.key_preference_temperature), "").equals(getString(R.string.celsius_unit))!!
        viewModel.isKph.value = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.key_preference_wind), "").equals(getString(R.string.wind_kph))!!
    }

    private fun updateLanguage(ctx: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val lang = prefs.getString(resources.getString(R.string.key_preference_language), "fr")
        updateLanguage(ctx, viewModel.lang.value)
        viewModel.context = setLanguage17(lang, this)!!
    }

    // TODO replace deprecated
    fun updateLanguage(ctx: Context, lang: String?) {
        val cfg = Configuration()
        if (!TextUtils.isEmpty(lang)) cfg.locale = Locale(lang) else cfg.locale =
            Locale.getDefault()
        ctx.resources.updateConfiguration(cfg, null)
    }

    @TargetApi(17)
    fun setLanguage17(lang: String?, c: Context): Context? {
        val overrideConfiguration: Configuration =
            c.resources.configuration
        val locale = Locale(lang)
        Locale.setDefault(locale)
        overrideConfiguration.setLocale(locale)
        //the configuration can be used for other stuff as well
        //Resources resources = context.getResources();//If you want to pass the resources instead of a Context, uncomment this line and put it somewhere useful
        return createConfigurationContext(overrideConfiguration)
    }

    @SuppressLint("MissingPermission")
    private fun doGeolocation() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            1
        )

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,this)
        //locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER,null,application.mainExecutor,            Consumer { Log.d(Constants.DEBUG_LOG, "lat=${it.latitude}, long=${it.longitude}") })
    }

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location) {
        Log.d(Constants.DEBUG_LOG, "geolocalized at: lat=${location.latitude}, long=${location.longitude}")

        // Store in viewmodel
        viewModel.isGeolocalized = true
        viewModel.latitude = location.latitude
        viewModel.longitude = location.longitude

        // Stop updating
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)
    }

//    private fun startLocationUpdates() {
//        val fusedLocationClient
//        fusedLocationClient.requestLocationUpdates(locationRequest,
//            locationCallback,
//            Looper.getMainLooper())
//    }

    fun setLanguageLegacy(lang: String?, c: Context): Context? {
        val res: Resources = c.resources
        // Change locale settings in the app.
        val dm = res.displayMetrics //Utility line
        val conf = res.configuration
        conf.locale =
            Locale(lang) //setLocale requires API 17+ - just like createConfigurationContext
        Locale.setDefault(conf.locale)
        res.updateConfiguration(conf, dm)

        //Using this method you don't need to modify the Context itself. Setting it at the start of the app is enough. As you
        //target both API's though, you want to return the context as you have no clue what is called. Now you can use the Context
        //supplied for both things
        return c
    }

}