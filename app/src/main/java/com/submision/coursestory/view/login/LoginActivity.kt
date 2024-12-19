package com.submision.coursestory.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.submision.coursestory.R
import com.submision.coursestory.data.result.Result
import com.submision.coursestory.databinding.ActivityLoginBinding
import com.submision.coursestory.view.main.MainActivity
import com.submision.coursestory.view.signup.SignupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        com.submision.coursestory.view.ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        toSignUp()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            // Validasi input email dan password
            if (email.isEmpty()) {
                binding.edLoginEmail.error = getString(R.string.email_error)
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 8) {
                binding.edLoginPassword.error = getString(R.string.password_error)
                return@setOnClickListener
            }

            // Tampilkan indikator loading saat proses login
            binding.progressBar.visibility = android.view.View.VISIBLE

            // Memulai proses login
            lifecycleScope.launch {
                viewModel.login(email, password)
            }

            // Mengamati perubahan status loginState
            lifecycleScope.launch {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is Result.Loading -> {
                            // Menampilkan progress bar saat login sedang berlangsung
                            binding.progressBar.visibility = android.view.View.VISIBLE
                        }
                        is Result.Success -> {
                            // Sembunyikan progress bar dan tampilkan dialog sukses
                            binding.progressBar.visibility = android.view.View.GONE
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                        is Result.Error -> {
                            // Sembunyikan progress bar dan tampilkan dialog error
                            binding.progressBar.visibility = android.view.View.GONE
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Oops!")
                                setPositiveButton("Coba Lagi") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toSignUp() {
        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

}
