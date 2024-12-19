package com.submision.coursestory.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.result.Result
import com.submision.coursestory.data.pref.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    // MutableStateFlow for handling login state
    private val _loginState = MutableStateFlow<Result<String>>(Result.Loading)
    val loginState: StateFlow<Result<String>> = _loginState

    /**
     * Function to handle login
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading // Emit loading state
            try {
                val loginResponse = repository.login(email, password)

                // Extract loginResult from LoginResponse
                val loginResult = loginResponse.loginResult
                if (loginResult != null && !loginResult.token.isNullOrEmpty()) {
                    // Save session with UserModel
                    val user = UserModel(
                        email = email,
                        token = loginResult.token,
                        isLogin = true
                    )
                    saveSession(user) // Save user session
                    _loginState.value = Result.Success("Login successful") // Emit success state
                } else {
                    _loginState.value = Result.Error("Login failed: Missing token")
                }
            } catch (e: Exception) {
                _loginState.value = Result.Error(e.message ?: "Login failed") // Emit error state
            }
        }
    }


    /**
     * Save user session to DataStore
     */
    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}