package com.submision.coursestory.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.submision.coursestory.data.response.ListStoryItem
import com.submision.coursestory.databinding.ItemStoryBinding

class StoryAdapter(private val onItemClick: (ListStoryItem) -> Unit) : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem?) {
            story?.let {
                binding.tvItemName.text = it.name
                Glide.with(binding.root.context)
                    .load(it.photoUrl)
                    .into(binding.ivItemPhoto)

                // Menangani klik item
                binding.root.setOnClickListener {
                    story?.let { onItemClick(it) }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
