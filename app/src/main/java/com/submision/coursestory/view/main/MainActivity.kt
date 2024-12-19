package com.submision.coursestory.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.submision.coursestory.data.pref.UserPreference
import com.submision.coursestory.data.pref.dataStore
import com.submision.coursestory.databinding.ActivityMainBinding
import com.submision.coursestory.view.welcome.WelcomeActivity
import com.submision.coursestory.data.response.ListStoryItem
import com.submision.coursestory.view.detail.DetailStoryActivity
import com.submision.coursestory.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private val userPreference by lazy { UserPreference.getInstance(dataStore) }
    private val viewModel by viewModels<MainViewModel> {
        com.submision.coursestory.view.ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView dan Adapter dengan onItemClick
        setupRecyclerView()

        // Observasi sesi pengguna
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        // Observasi data cerita
        observeStories()

        // Mengatur tampilan dan aksi tombol logout
        setupView()
        setupAction()

        kliktoupload()

        // Memuat cerita dari ViewModel
        viewModel.fetchStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { story -> onStoryClick(story) }
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeStories() {
        viewModel.stories.observe(this) { stories ->
            if (stories.isNotEmpty()) {
                storyAdapter.submitList(stories)
            } else {
                Toast.makeText(this, "No stories available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onStoryClick(story: ListStoryItem) {
        // Handle the click event, for example, navigate to a detail page
        Toast.makeText(this, "Clicked on: ${story.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra("story_id", story.id)  // Mengirimkan object story
        startActivity(intent)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kliktoupload() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

}
