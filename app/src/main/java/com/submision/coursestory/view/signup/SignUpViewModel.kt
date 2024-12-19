package com.submision.coursestory.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submision.coursestory.data.result.Result
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    private val signupState = MutableStateFlow<Result<RegisterResponse>>(Result.Loading)
    val registerState: StateFlow<Result<RegisterResponse>> = signupState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            signupState.value = Result.Loading
            try {
                val response = repository.register(name, email, password)  // Mengembalikan RegisterResponse
                signupState.value = Result.Success(response)  // Sesuaikan dengan tipe yang benar
            } catch (e: Exception) {
                signupState.value = Result.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
