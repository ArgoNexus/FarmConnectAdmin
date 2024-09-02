package com.example.farmconnectadmin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmconnectadmin.databinding.ItemViewImageSelectionBinding

class AdapterSelectedImage(private val imageUris: ArrayList<Uri>) : RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {

    // ViewHolder class
    class SelectedImageViewHolder(val binding: ItemViewImageSelectionBinding) : RecyclerView.ViewHolder(binding.root)

    // Inflate the item layout and create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val binding = ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedImageViewHolder(binding)
    }

    // Return the size of the image URLs list
    override fun getItemCount(): Int {
        return imageUris.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val image = imageUris[position]
        holder.binding.apply {
            // Ensure that your binding has an ImageView and set its URI
            ivImage.setImageURI(image) // Replace `ivImage` with the actual ID from your layout
        }

        // Handle item removal
        holder.binding.closeButton.setOnClickListener {
            if (position < imageUris.size) {
                imageUris.removeAt(position)
                notifyItemRemoved(position)
                // Optionally, notifyItemRangeChanged if you want to update the remaining items
            }
        }
    }
}
