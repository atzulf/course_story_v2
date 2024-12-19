package com.submision.coursestory.view.signup

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
import com.submision.coursestory.databinding.ActivitySignupBinding
import com.submision.coursestory.view.login.LoginActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val viewModel by viewModels<SignUpViewModel> {
        com.submision.coursestory.view.ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toLogin()
        setupView()
        setupAction()
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
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            // Validasi input name, email, dan password
            if (name.isEmpty()) {
                binding.edRegisterName.error = getString(R.string.name_error)
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edRegisterEmail.error = getString(R.string.email_error)
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 8) {
                binding.edRegisterPassword.error = getString(R.string.password_error)
                return@setOnClickListener
            }

            binding.progressBar.visibility = android.view.View.VISIBLE

            lifecycleScope.launch {
                viewModel.register(name, email, password)
            }

            lifecycleScope.launch {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = android.view.View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = android.view.View.GONE
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent =
                                        Intent(this@SignupActivity, LoginActivity::class.java)
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
                            binding.progressBar.visibility = android.view.View.GONE
                            AlertDialog.Builder(this@SignupActivity).apply {
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

    private fun toLogin() {
        binding.loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
