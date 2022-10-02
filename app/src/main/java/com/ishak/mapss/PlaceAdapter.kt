package com.ishak.mapss

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ishak.mapkotlin.MapsActivity
import com.ishak.mapkotlin.databinding.RecyclerRowBinding

class PlaceAdapter(val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.Holder>() {
    class Holder(val recyclerRowBinding: RecyclerRowBinding) :
        RecyclerView.ViewHolder(recyclerRowBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var recyclerRowBinding =
            RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(recyclerRowBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.recyclerRowBinding.textView.text = placeList.get(position).place
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("SelectedPlace", placeList.get(position))
            intent.putExtra("info", "old")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}