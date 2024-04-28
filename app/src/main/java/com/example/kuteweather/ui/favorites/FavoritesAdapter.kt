package com.example.kuteweather.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.kuteweather.R
import com.example.kuteweather.databinding.FavoritesRowBinding
import com.example.kuteweather.tools.WeatherIcons
import kotlinx.coroutines.*
import kotlin.random.Random

class FavoritesAdapter(private val listener : View.OnClickListener, private val list: List<FavoritesModel>, private val viewModel: FavoritesViewModel) : RecyclerView.Adapter<FavoritesAdapter.CustomViewHolder>() {

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding =  FavoritesRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.favorites_row, parent, false)
        return CustomViewHolder(view)
    }

    /**
     * we store index in tag of city textview
     */
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.binding.cityData = list[position]
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(listener)
        //holder.binding.index = position

        // we create a mutable for each row
        viewModel.listOfTemperatures.add(MutableLiveData(""))
        viewModel.listOfIcons.add(MutableLiveData(Integer(R.drawable.transparent_image)))
        viewModel.listOfProgressBarStatus.add(MutableLiveData(false))

        // binding each row to their mutable
        holder.binding.temp = viewModel.listOfTemperatures[position].value!!
        holder.binding.icon = viewModel.listOfIcons[position].value!!.toInt()
        holder.binding.isVisible = viewModel.listOfProgressBarStatus[position].value
    }

    override fun getItemCount(): Int {
        return list.size
    }


}