package com.submision.coursestory.view.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.submision.coursestory.R
import com.submision.coursestory.data.utils.getImageUri
import com.submision.coursestory.data.utils.reduceFileImage
import com.submision.coursestory.data.utils.uriToFile
import com.submision.coursestory.databinding.ActivityUploadBinding
import com.submision.coursestory.view.ViewModelFactory
import com.submision.coursestory.view.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.submision.coursestory.data.result.Result
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivPreview.setImageURI(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    fun showImage(){
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }
    private fun showLoading(loading: Boolean) {
        val animation = if (loading) {
            AlphaAnimation(0f, 1f).apply {
                duration = 300
            }
        } else {
            AlphaAnimation(1f, 0f).apply {
                duration = 300
            }
        }
        binding.progressBar.startAnimation(animation)
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadStory() {
        if (!binding.edAddDescription.text.isNullOrBlank() && currentImageUri != null) {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val desc = binding.edAddDescription.text.toString()

                showLoading(true)

                val requestBody = desc.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

                val multipartBody =
                    MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

                viewModel.uploadStory(multipartBody, requestBody)
                    .observe(this) { response ->
                        when (response) {
                            is Result.Error -> showToast(
                                "Gagal Mengupload Foto Karena ${response.exception}"
                            )

                            Result.Loading -> showLoading(
                                true
                            )

                            is Result.Success -> {
                                showToast("Berhasil Mengupload Data")
                                runBlocking {
                                    delay(500)
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
            }
        } else {
            showToast("Anda Belum Mengisi Foto atau deskripsi")
        }
    }
}