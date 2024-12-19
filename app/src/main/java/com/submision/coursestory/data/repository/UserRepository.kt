package com.submision.coursestory.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.submision.coursestory.data.api.ApiService
import com.submision.coursestory.data.pref.UserPreference
import com.submision.coursestory.data.response.AllStoriesResponse
import com.submision.coursestory.data.response.ErrorResponse
import com.submision.coursestory.data.response.LoginResponse
import com.submision.coursestory.data.response.UploadResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import com.submision.coursestory.data.result.Result
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStories(): AllStoriesResponse {
        val token = userPreference.getSession().first().token
        return apiService.getStories("Bearer $token")
    }

    suspend fun getDetailStory(storyId: String) = try {
        val token = userPreference.getSession().first().token
        apiService.getDetailStory("Bearer $token", storyId)
    } catch (e: HttpException) {
        Log.e("UserRepository", "getDetailStory: ${e.message()}")
        null
    }

    fun uploadStory(file : MultipartBody.Part, description: RequestBody): LiveData<Result<UploadResponse>> = liveData{
        emit(Result.Loading)
        try{
            val token = userPreference.getSession().first().token
            val response = apiService.uploadStory("Bearer $token",file,description)
            emit(Result.Success(response))
        }catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    suspend fun saveSession(user: com.submision.coursestory.data.pref.UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<com.submision.coursestory.data.pref.UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}