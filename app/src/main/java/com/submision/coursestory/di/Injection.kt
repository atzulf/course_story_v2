package com.submision.coursestory.di

import android.content.Context
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.data.api.ApiConfig
import com.submision.coursestory.data.pref.UserPreference
import com.submision.coursestory.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}