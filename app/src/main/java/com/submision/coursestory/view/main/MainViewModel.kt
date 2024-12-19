package com.submision.coursestory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    // LiveData untuk daftar cerita
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    // Mendapatkan sesi pengguna
    fun getSession(): LiveData<com.submision.coursestory.data.pref.UserModel> {
        return repository.getSession().asLiveData()
    }

    // Fungsi logout
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    // Mendapatkan daftar cerita dari repository
    fun fetchStories() {
        viewModelScope.launch {
            try {
                val response = repository.getStories()
                if (response.listStory != null) {
                    _stories.postValue(response.listStory.filterNotNull()) // Hanya menyimpan item yang tidak null
                } else {
                    _stories.postValue(emptyList()) // Jika kosong, kirim list kosong
                }
            } catch (e: Exception) {
                // Tangani error dengan mengirim list kosong atau log error
                _stories.postValue(emptyList())
            }
        }
    }
}
