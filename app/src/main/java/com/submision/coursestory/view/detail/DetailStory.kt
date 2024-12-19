package com.submision.coursestory.view.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.submision.coursestory.data.response.Story
import com.submision.coursestory.databinding.ActivityDetailStoryBinding
import com.submision.coursestory.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan ID dari Intent
        val storyId = intent.getStringExtra("story_id")

        if (storyId != null) {
            Log.d("DetailActivity", "Story ID: $storyId")
            viewModel.getDetailStory(storyId)
            fetchStoryDetail()
        }else {
            Log.e("DetailActivity", "Story ID is null")
        }

        // Menyiapkan action untuk tombol kembali jika ada
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchStoryDetail() {
        viewModel.detailStory.observe(this) { detailStory ->
            detailStory?.let { populateDetail(it) }
        }

    }

    private fun populateDetail(story: Story) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .into(ivItemPhoto)
        }
    }
}
