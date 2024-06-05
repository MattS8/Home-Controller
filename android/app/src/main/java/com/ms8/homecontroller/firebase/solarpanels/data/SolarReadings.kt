package com.ms8.homecontroller.firebase.solarpanels.data

data class SolarReadings (
    var heatIndex: List<Float> = ArrayList(),
    var humidity: List<Float> = ArrayList(),
    var temperature: List<Float> = ArrayList(),
    var timestamp: List<Long> = ArrayList()
)