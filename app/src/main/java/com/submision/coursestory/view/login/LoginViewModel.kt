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

    private val _loginState = MutableStateFlow<Result<String>>(Result.Loading)
    val loginState: StateFlow<Result<String>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            try {
                val loginResponse = repository.login(email, password)

                val loginResult = loginResponse.loginResult
                if (loginResult != null && !loginResult.token.isNullOrEmpty()) {

                    val user = UserModel(
                        email = email,
                        token = loginResult.token,
                        isLogin = true
                    )
                    saveSession(user)
                    _loginState.value = Result.Success("Login successful")
                } else {
                    _loginState.value = Result.Error("Login failed: Missing token")
                }
            } catch (e: Exception) {
                _loginState.value = Result.Error(e.message ?: "Login failed")
            }
        }
    }

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}