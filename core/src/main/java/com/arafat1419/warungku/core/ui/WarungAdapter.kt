package com.arafat1419.warungku.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arafat1419.warungku.assets.R
import com.arafat1419.warungku.core.databinding.ListWarungBinding
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.bumptech.glide.Glide

class WarungAdapter : RecyclerView.Adapter<WarungAdapter.ViewHolder>() {
    private val listWarung = ArrayList<WarungDomain>()

    var onItemClicked: ((WarungDomain) -> Unit)? = null

    fun setWarung(newWarung: List<WarungDomain>?) {
        if (newWarung == null) return

        listWarung.clear()
        listWarung.addAll(newWarung)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ListWarungBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listWarung[position])
    }

    override fun getItemCount(): Int = listWarung.size

    inner class ViewHolder(private val binding: ListWarungBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(warungDomain: WarungDomain) {
            with(binding) {

                txtName.text = warungDomain.name
                txtAddress.text = warungDomain.address

                val coordinateFormat = itemView.resources.getString(R.string.coordinate_format)
                txtCoordinate.text = String.format(
                    coordinateFormat,
                    warungDomain.lat?.toString()?.take(4),
                    warungDomain.long?.toString()?.take(4)
                )

                Glide.with(itemView.context)
                    .load(warungDomain.photoUrl)
                    .into(imgWarung)

                itemView.setOnClickListener {
                    onItemClicked?.invoke(warungDomain)
                }
            }
        }
    }
}