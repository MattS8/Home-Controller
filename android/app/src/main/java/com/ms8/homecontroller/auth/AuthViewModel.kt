package com.ms8.homecontroller.auth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val TAG = "AuthViewModel"

    private val _credential = MutableLiveData<Credential?>()
    val credential: LiveData<Credential?> = _credential
    fun getCredentials(request: GetCredentialRequest, activityContext: Context) {
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(activityContext)
                val result = credentialManager.getCredential(request = request, context = activityContext)
                _credential.value = result.credential
            }
            catch (e: GetCredentialException) {
                Log.e(TAG, "Unable to fetch credentials!", e)
                _credential.value = null
            }
        }
    }
}