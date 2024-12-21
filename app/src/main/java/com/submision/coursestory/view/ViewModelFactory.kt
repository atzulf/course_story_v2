package com.submision.coursestory.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.submision.coursestory.data.repository.UserRepository
import com.submision.coursestory.di.Injection
import com.submision.coursestory.view.detail.DetailStoryViewModel
import com.submision.coursestory.view.login.LoginViewModel
import com.submision.coursestory.view.main.MainViewModel
import com.submision.coursestory.view.maps.MapsViewModel
import com.submision.coursestory.view.signup.SignUpViewModel
import com.submision.coursestory.view.upload.UploadViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                DetailStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (ViewModelFactory.Companion.INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    ViewModelFactory.Companion.INSTANCE =
                        ViewModelFactory(
                            Injection.provideRepository(context)
                        )
                }
            }
            return ViewModelFactory.Companion.INSTANCE as ViewModelFactory
        }
    }
}