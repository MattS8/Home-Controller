package com.ms8.homecontroller.ui.smartgarage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ms8.homecontroller.firebase.smartgarage.data.Constants
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.smartgarage.providers.AutoCloseOptionsProvider
import com.ms8.homecontroller.firebase.smartgarage.providers.StatusServiceProvider
import kotlinx.coroutines.launch

class SmartGarageViewModel : ViewModel() {
    private var _status = MutableLiveData<GarageStatus>()
    val status: LiveData<GarageStatus> = _status

    private var _prevStatus = MutableLiveData<GarageStatus>()
    val prevStatus: LiveData<GarageStatus> = _status

    private var _autoCloseEnabled = MutableLiveData<Boolean>()
    val autoCloseEnabled: LiveData<Boolean> = _autoCloseEnabled

    private var _autoCloseWarningEnabled = MutableLiveData<Boolean>()
    val autoCloseWarningEnabled: LiveData<Boolean> = _autoCloseWarningEnabled

    private var _autoCloseTimeout = MutableLiveData<Number>()
    val autoCloseTimeout: LiveData<Number> = _autoCloseTimeout

    private var _autoCloseWarningTimeout = MutableLiveData<Number>()
    val autoCloseWarningTimeout: LiveData<Number> = _autoCloseWarningTimeout

    init {
        viewModelScope.launch {
            StatusServiceProvider.getStatus().collect { newStatus ->
                Log.i(Constants.FLOW_TAG, "status coroutine running...")
                _prevStatus.value = status.value
                _status.value = newStatus
            }
        }

        viewModelScope.launch {
            AutoCloseOptionsProvider.getEnabled().collect { newVal ->
                Log.i(Constants.FLOW_TAG, "auto_close_opt coroutine running...")
                _autoCloseEnabled.value = newVal
            }
        }

        viewModelScope.launch {
            AutoCloseOptionsProvider.getWarningEnabled().collect { newVal ->
                Log.i(Constants.FLOW_TAG, "auto_close_opt coroutine running...")
                _autoCloseWarningEnabled.value = newVal
            }
        }

        viewModelScope.launch {
            AutoCloseOptionsProvider.getTimeout().collect { newVal ->
                Log.i(Constants.FLOW_TAG, "auto_close_opt coroutine running...")
                _autoCloseTimeout.value = newVal
            }
        }

        viewModelScope.launch {
            AutoCloseOptionsProvider.getWarningTimeout().collect { newVal ->
                Log.i(Constants.FLOW_TAG, "auto_close_opt coroutine running...")
                _autoCloseWarningTimeout.value = newVal
            }
        }
    }
}