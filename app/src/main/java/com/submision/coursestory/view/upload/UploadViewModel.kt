package com.submision.coursestory.view.upload

import androidx.lifecycle.ViewModel
import com.submision.coursestory.data.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: UserRepository): ViewModel() {
    fun uploadStory(file: MultipartBody.Part, description: RequestBody) =
        repository.uploadStory(file, description)
}