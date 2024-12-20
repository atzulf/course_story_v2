package com.submision.coursestory.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.response.ListStoryItem
import kotlinx.coroutines.launch
import com.submision.coursestory.data.result.Result


class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> = _storiesWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchStoriesWithLocation(location: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getStoriesWithLocation(1)
                result.observeForever { response ->
                    _isLoading.value = false
                    when (response) {

                        is Result.Error -> {
                            // Log error or show a message
                            _isLoading.value = false
                        }
                        Result.Loading -> {
                            _isLoading.value = true
                        }
                       is Result.Success -> {
                       _storiesWithLocation.value = response.data.listStory?.filterNotNull()
                       }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Log error or show a message
            }
        }
    }
}


