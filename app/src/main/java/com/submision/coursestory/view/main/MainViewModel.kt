package com.submision.coursestory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    fun getSession(): LiveData<com.submision.coursestory.data.pref.UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val response = repository.getStories()
                if (response.listStory != null) {
                    _stories.postValue(response.listStory.filterNotNull())
                } else {
                    _stories.postValue(emptyList())
                }
            } catch (e: Exception) {
                _stories.postValue(emptyList())
            }
        }
    }
}
