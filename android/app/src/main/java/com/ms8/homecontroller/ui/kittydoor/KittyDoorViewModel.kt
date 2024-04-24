package com.ms8.homecontroller.ui.kittydoor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KittyDoorViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Kitty Door Fragment"
    }
    val text: LiveData<String> = _text
}