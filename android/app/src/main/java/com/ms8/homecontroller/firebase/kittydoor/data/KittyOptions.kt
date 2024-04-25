package com.ms8.homecontroller.firebase.kittydoor.data

data class KittyOptions(
    var openLightLevel: Int = 0,
    var closeLightLevel: Int = 0,
    var delayOpening: Boolean = true,
    var delayClosing: Boolean = true,
    var delayOpeningVal: Long = 120,
    var delayClosingVal: Long = 120,
    var o_timestamp: String = "",
    var command: String = "_none_",
    var overrideAuto: Boolean = false
)