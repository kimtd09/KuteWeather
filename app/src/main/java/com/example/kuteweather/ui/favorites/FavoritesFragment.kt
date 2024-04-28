package com.example.kuteweather.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.kuteweather.MainViewModel
import com.example.kuteweather.R
import com.example.kuteweather.api.weather.ContentProvider
import com.example.kuteweather.databinding.FragmentFavoritesBinding
import com.example.kuteweather.db.AppRepository
import com.example.kuteweather.model.WeatherGson1
import com.example.kuteweather.tools.WeatherIcons
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.synchronized
import kotlin.math.roundToInt
import kotlin.random.Random

class FavoritesFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoritesAdapter
    private lateinit var viewModel: FavoritesViewModel
    private var repository: AppRepository? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val threadList = mutableListOf<Job>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        viewModel.selectedIndex.value = -1
        binding.data = viewModel

        repository = context?.let { AppRepository.getInstance(it) }
        //repository?.loadData()
        //repository?.loadData2()

        //repository?.liveData?.observe(viewLifecycleOwner) {
        viewModel.liveData?.observe(viewLifecycleOwner) {
            //Log.d("AppDebug", "observed: $it")
            adapter = FavoritesAdapter(this, it, viewModel)
//            viewModel.requestAPI()
            binding.recyclerView.adapter = adapter
        }

        // Show city weather on click
        binding.favoritesWeatherIcon.setOnClickListener {
            val parcelable =
                viewModel.selectedIndex.value?.let { it1 -> viewModel.liveData?.value?.get(it1) }
            Navigation.findNavController(it).navigate(
                FavoritesFragmentDirections.actionNavigationFavoritesToNavigationHome(parcelable!!)
            )
        }


        // delete a city of favorites
        binding.favoritesDeleteIcon.setOnClickListener {
            val city =
                viewModel.selectedIndex.value?.let { it1 -> viewModel.liveData?.value?.get(it1) }
            context?.let { it1 ->
                if (city != null) {
                    AppRepository.getInstance(it1)?.deleteCity(city)
                    viewModel.selectedIndex.value = -1

                    //TODO getall to refresh list
                }
            }
        }

        return binding.root
    }

    @OptIn(InternalCoroutinesApi::class)
    fun loadTemp2() {
        //Thread.sleep(1000)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)  // give time to adapter to be created from list
        }.invokeOnCompletion {
            val max = viewModel.listOfTemperatures.size - 1

            for (i in 0..max) {
                viewModel.listOfProgressBarStatus[i].postValue(true)
                adapter.notifyItemChanged(i)
                CoroutineScope(Dispatchers.Main).launch {
                    delay(Random.nextLong(2000, 5000))
                    viewModel.listOfTemperatures[i].postValue(Random.nextInt(50).toString() + "°C")
                    viewModel.listOfIcons[i].postValue(
                        Integer(
                            WeatherIcons.getDayMap()?.values?.elementAt(
                                Random.nextInt(WeatherIcons.getNightMap()?.size!!)
                            )!!
                        )
                    )
//                    viewModel.listOfIcons[i].setImageResource(WeatherIcons.getDayMap()?.values?.elementAt(
//                        Random.nextInt(WeatherIcons.getDayMap()?.size?.minus(1)!!))!!)
                }.invokeOnCompletion {
                    viewModel.listOfProgressBarStatus[i].postValue(false)
                    CoroutineScope(Dispatchers.Main).launch {
                        synchronized(adapter) {
                            adapter.notifyItemChanged(i)
                        }
                        delay(2000)
                    }
                }
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun loadTempWithOneThread() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            delay(1000)  // give time to adapter to be created from list
        }
            threadList.add(job)
            job.invokeOnCompletion {
            val max = viewModel.listOfTemperatures.size - 1
            for (i in 0..max) {
                viewModel.listOfProgressBarStatus[i].postValue(true)
                adapter.notifyItemChanged(i)
                CoroutineScope(Dispatchers.Main).launch {
                    //delay(Random.nextLong(2000, 5000))
                    viewModel.listOfTemperatures[i].postValue(Random.nextInt(50).toString() + "°C")
                    viewModel.listOfIcons[i].postValue(
                        Integer(
                            WeatherIcons.getDayMap()?.values?.elementAt(
                                Random.nextInt(WeatherIcons.getNightMap()?.size!!)
                            )!!
                        )
                    )
//                    viewModel.listOfIcons[i].setImageResource(WeatherIcons.getDayMap()?.values?.elementAt(
//                        Random.nextInt(WeatherIcons.getDayMap()?.size?.minus(1)!!))!!)
                }.invokeOnCompletion {
                    viewModel.listOfProgressBarStatus[i].postValue(false)
                    CoroutineScope(Dispatchers.Main).launch {
                        synchronized(adapter) {
                            adapter.notifyItemChanged(i)
                        }
                        delay(1000)
                    }
                }
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun loadData() {
        if (viewModel.liveData?.value.isNullOrEmpty()) return
        val job = CoroutineScope(Dispatchers.Main).launch {
            delay(2000)  // give time to adapter to be created from list
        }

        threadList.add(job)

        job.invokeOnCompletion {
            val max = viewModel.liveData?.value?.size?.minus(1)

            for (i in 0..max!!) {
                val cityName = viewModel.liveData?.value?.get(i)?.city

                viewModel.listOfProgressBarStatus[i].postValue(true)
                adapter.notifyItemChanged(i)

                val job = CoroutineScope(Dispatchers.Main).launch {
                    val result = ContentProvider.request(cityName!!)
                    //Log.d("AppDebug", "result=$result")
                    if (result != null) {
                        val root = WeatherGson1(result).root
                        var tempInt: Int = 0
                        var unit: String = ""

                        if(isAdded) {  // It avoids exception when we leave fragment to another
                            if (sharedViewModel.isCelsius.value == true) {
                                unit = getString(R.string.celsius_unit)
                                tempInt = root.current.temp_c.toDouble().roundToInt()
                            } else {
                                unit = getString(R.string.fahrenheit_unit)
                                tempInt = root.current.temp_f.toDouble().roundToInt()
                            }
                        }

                        viewModel.listOfTemperatures[i].postValue("$tempInt$unit")

                        if (root.current.is_day == 1) {
                            viewModel.listOfIcons[i].postValue(
                                Integer(
                                    WeatherIcons.getDayMap()?.get(root.current.condition.code)!!
                                )
                            )
                        } else {
                            viewModel.listOfIcons[i].postValue(
                                Integer(
                                    WeatherIcons.getNightMap()?.get(root.current.condition.code)!!
                                )
                            )
                        }
                    }

                }

                //threadList.add(job)

                job.invokeOnCompletion {
                    viewModel.listOfProgressBarStatus[i].postValue(false)
                    CoroutineScope(Dispatchers.Main).launch {
                        synchronized(adapter) {
                            adapter.notifyItemChanged(i)
                        }
                        delay(2000)
                    }
                }

//                viewModel.listOfProgressBarStatus[i].postValue(true)
//                adapter.notifyItemChanged(i)
//                CoroutineScope(Dispatchers.Main).launch {
//                    delay(Random.nextLong(2000, 5000))
//                    viewModel.listOfTemperatures[i].postValue(Random.nextInt(50).toString() + "°C")
//                    viewModel.listOfIcons[i].setImageResource(WeatherIcons.getDayMap()?.values?.elementAt(
//                        Random.nextInt(WeatherIcons.getDayMap()?.size?.minus(1)!!))!!)
//                }.invokeOnCompletion {
//                    viewModel.listOfProgressBarStatus[i].postValue(false)
//                    synchronized(adapter) {
//                        adapter.notifyItemChanged(i)
//                    }
//                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //Log.d("AppDebug", "canceling jobs")
        //threadList.forEach { it.cancel() }

    }

    override fun onResume() {
        super.onResume()
        //loadTemp1()
        //loadTemp2()
        loadData()
    }

    fun loadTemp1() {
        CoroutineScope(Dispatchers.Main).launch {
            val max = viewModel.listOfTemperatures.size - 1
            for (i in 0..max) {
                viewModel.listOfTemperatures[i].postValue(Random.nextInt(50).toString() + "°C")
            }
        }
//                .invokeOnCompletion {
//                Thread.sleep(500)
//                adapter.notifyDataSetChanged()
//            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        if (v != null) {
            Log.d("AppDebug", "${v?.tag}: ${viewModel.liveData?.value?.get(v.tag as Int)?.city}")
            //v.setBackgroundColor(resources.getColor(R.color.mint_leaf))
            val indexFromTag = v.tag as Int

            if (viewModel.selectedIndex.value == indexFromTag) {
                viewModel.selectedIndex.value = -1
            } else {
                viewModel.selectedIndex.value = indexFromTag
            }

            val max = binding.recyclerView.size - 1
            for (i in 0..max) {
                // check favorites_row.xml
                // TODO optimize code
                val row = binding.recyclerView.getChildAt(i) as LinearLayout
                row.setBackgroundColor(resources.getColor(R.color.white))
                val ll = (row.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
                val cityText = ll.getChildAt(1) as TextView
                val countryText = ll.getChildAt(2) as TextView
                val tempText = (row.getChildAt(0) as LinearLayout).getChildAt(1) as TextView
                cityText.setTextColor(resources.getColor(android.R.color.secondary_text_light))
                countryText.setTextColor(resources.getColor(android.R.color.secondary_text_light))
                tempText.setTextColor(resources.getColor(android.R.color.secondary_text_light))
            }

            if (viewModel.selectedIndex.value != -1) {
                binding.recyclerView.getChildAt(v.tag as Int)
                    .setBackgroundColor(resources.getColor(R.color.selected_row))
                (binding.recyclerView.getChildViewHolder(v) as FavoritesAdapter.CustomViewHolder).binding.textFavoritesCity.setTextColor(
                    resources.getColor(
                        R.color.white
                    )
                )
                (binding.recyclerView.getChildViewHolder(v) as FavoritesAdapter.CustomViewHolder).binding.textFavoritesCountry.setTextColor(
                    resources.getColor(
                        R.color.white
                    )
                )
                (binding.recyclerView.getChildViewHolder(v) as FavoritesAdapter.CustomViewHolder).binding.textFavoritesTemp.setTextColor(
                    resources.getColor(
                        R.color.white
                    )
                )
            }

        }
    }
}