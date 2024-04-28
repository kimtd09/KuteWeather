package com.example.kuteweather.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.kuteweather.MainViewModel
import com.example.kuteweather.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val languageList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_language)!!)
        languageList?.title = "${activity?.getString(R.string.language)}  (${languageList?.value})"

        val temperatureList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_temperature)!!)
        temperatureList?.title = "${activity?.getString(R.string.temperature_title)}  (${temperatureList?.value})"

        val windList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_wind)!!)
        windList?.title = "${activity?.getString(R.string.wind)}  (${windList?.value})"
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        //Log.d("AppDebug", "value set: ${sharedPreferences?.getString(key, "")!!}")

        if(key?.equals(activity?.getString(R.string.key_preference_language)) == true) {
            val languageList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_language)!!)
            languageList?.title = "${activity?.getString(R.string.language)}  (${sharedPreferences?.getString(key, "")})"
        }

        else if(key?.equals(activity?.getString(R.string.key_preference_temperature)) == true) {
            val temperatureList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_temperature)!!)
            temperatureList?.title = "${activity?.getString(R.string.temperature_title)}  (${sharedPreferences?.getString(key, "")})"
            sharedViewModel.isCelsius.value = sharedPreferences?.getString(key, "").equals(getString(R.string.celsius_unit))
        }

        else if(key?.equals(activity?.getString(R.string.key_preference_wind)) == true) {
            val windList = findPreference<ListPreference>(activity?.getString(R.string.key_preference_wind)!!)
            windList?.title = "${activity?.getString(R.string.wind)}  (${sharedPreferences?.getString(key, "")})"
            sharedViewModel.isKph.value = sharedPreferences?.getString(key, "").equals(getString(R.string.wind_kph))
        }

        //TODO try to change lang on demand
//        Log.d("AppDebug", "${key?.equals(activity?.getString(R.string.key_preference_language))}")
//        if (key?.equals(resources.getString(R.string.key_preference_language)) == true) {
//            Log.d("AppDebug","setting new lang")
//            val lang = sharedPreferences?.getString(key, "")

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                (requireActivity() as MainActivity).setLanguage17(lang, requireActivity())
//                requireActivity().recreate()
//            } else {
//                (requireActivity() as MainActivity).setLanguageLegacy(lang, requireActivity())
//                requireActivity().recreate()
//            }
        //}

    }
}