package com.submision.coursestory.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.response.Story
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _detailStory = MutableLiveData<Story?>()
    val detailStory: LiveData<Story?> = _detailStory


    fun getDetailStory(id: String) {
        viewModelScope.launch {
            val response = repository.getDetailStory(id)
            _detailStory.postValue(response?.story)
        }
    }

}