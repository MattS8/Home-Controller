package com.ms8.homecontroller.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ms8.homecontroller.R
import com.ms8.homecontroller.auth.AuthViewModel
import com.ms8.homecontroller.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val TAG = "SplashActivity"

    // Status variables
    private var progDrawable: AnimatedVectorDrawableCompat? = null
    private val progCallback = object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            binding.progressBar.post { progDrawable?.start() }
        }
    }

    // Sign-in variables
    var testing = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProgressBar()

        googleSignIn(true)

        ViewModelProvider(this)[AuthViewModel::class.java]
            .credential.observe(this) { newCredential ->
                when (newCredential) {
                    null -> {
                        Log.w(TAG, "Null credential passed!")
                    }
                    is PublicKeyCredential -> {
                        TODO("Support public keys on Firebase")
                    }
                    is PasswordCredential -> {
                        TODO("Support ID and password on Firebase")
                    }
                    is CustomCredential -> {
                        if (newCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                            val idTokenCredential = GoogleIdTokenCredential
                                .createFrom(newCredential.data)
                            val firebaseCredential = GoogleAuthProvider.getCredential(idTokenCredential.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    when {
                                        task.isSuccessful -> {
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                        else -> {
                                            Log.e(TAG, "Unable to authenticate user with FirebaseAuth: ${task.exception}")
                                        }
                                    }
                                }
                        } else {
                            Log.e(TAG, "Received unknown credential type: ${newCredential.type}")
                        }
                    }
                    else -> {
                        Log.e(TAG, "Received unknown credential type: ${newCredential.type}")
                    }
                }
            }
    }

    private fun setupProgressBar() {
        progDrawable = AnimatedVectorDrawableCompat.create(
            this,
            R.drawable.av_progress
        )
        binding.progressBar.setImageDrawable(progDrawable)
    }

    private fun googleSignIn(filterByAuthorizedAccounts: Boolean) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setAutoSelectEnabled(true)
            .setServerClientId(getString(R.string.default_web_id))
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        ViewModelProvider(this)[AuthViewModel::class.java]
            .getCredentials(request,this)
    }

    override fun onResume() {
        super.onResume()
        Log.i("##TEST", "onResume")
        progDrawable?.registerAnimationCallback(progCallback)
        progDrawable?.start()
    }

    override fun onPause() {
        super.onPause()
        Log.i("##TEST", "onPause")
        progDrawable?.unregisterAnimationCallback(progCallback)
    }
}