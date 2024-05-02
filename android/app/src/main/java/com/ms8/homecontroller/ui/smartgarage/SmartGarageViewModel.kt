package com.ms8.homecontroller.ui.smartgarage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ms8.homecontroller.firebase.smartgarage.data.Constants
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.smartgarage.providers.StatusServiceProvider
import kotlinx.coroutines.launch

class SmartGarageViewModel : ViewModel() {
    private var _status = MutableLiveData<GarageStatus>()
    val status: LiveData<GarageStatus> = _status

    private var _prevStatus = MutableLiveData<GarageStatus>()
    val prevStatus: LiveData<GarageStatus> = _status

    init {
        viewModelScope.launch {
            StatusServiceProvider.getStatus().collect { newStatus ->
                Log.i(Constants.FLOW_TAG, "status coroutine running...")
                _prevStatus.value = status.value
                _status.value = newStatus
            }
        }
    }
}