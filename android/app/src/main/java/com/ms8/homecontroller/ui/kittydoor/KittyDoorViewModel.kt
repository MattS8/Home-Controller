package com.ms8.homecontroller.ui.kittydoor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms8.homecontroller.firebase.kittydoor.data.Constants
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.kittydoor.data.KittyOptions
import com.ms8.homecontroller.firebase.kittydoor.providers.HardwareOverrideProvider
import com.ms8.homecontroller.firebase.kittydoor.providers.LightSensorServiceProvider
import com.ms8.homecontroller.firebase.kittydoor.providers.DoorStateServiceProvider
import com.ms8.homecontroller.firebase.kittydoor.providers.OverrideAutoProvider
import kotlinx.coroutines.launch

class KittyDoorViewModel : ViewModel() {
    private var _status = MutableLiveData<DoorStatus>()
    val status: LiveData<DoorStatus> = _status

    private var _prevStatus = MutableLiveData<DoorStatus>()
    val prevStatus: LiveData<DoorStatus> = _prevStatus

    private var _lightLevel = MutableLiveData<Int>()
    val lightLevel: LiveData<Int> = _lightLevel

    private var _kittyOptions = MutableLiveData<KittyOptions>()
    val kittyOptions: LiveData<KittyOptions> = _kittyOptions

    private var _hwOverride = MutableLiveData<Boolean>()
    val hwOverride: LiveData<Boolean> = _hwOverride

    private var _overrideAuto = MutableLiveData<Boolean>()
    val overrideAuto: LiveData<Boolean> = _overrideAuto

    init {
        viewModelScope.launch {
            DoorStateServiceProvider.getStatus().collect { newStatus ->
                Log.i(Constants.FLOW_TAG, "status coroutine running...")
                _prevStatus.value = status.value
                _status.value = newStatus
            }
        }
        viewModelScope.launch {
            LightSensorServiceProvider.getLightValue().collect { newLightLevel ->
                Log.i(Constants.FLOW_TAG, "lightLevel coroutine running...")
                _lightLevel.value = newLightLevel
            }
        }
        viewModelScope.launch {
            HardwareOverrideProvider.getHwOverride().collect {newHwOverride ->
                Log.i(Constants.FLOW_TAG, "hwOverride coroutine running...")
                _hwOverride.value = newHwOverride
            }
        }
        viewModelScope.launch {
            OverrideAutoProvider.getOverrideAuto().collect {newOverrideAuto ->
                Log.i(Constants.FLOW_TAG, "overrideAuto coroutine running...")
                _overrideAuto.value = newOverrideAuto
            }
        }
    }
}