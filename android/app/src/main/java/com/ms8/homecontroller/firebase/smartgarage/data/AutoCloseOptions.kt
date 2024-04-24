package com.ms8.homecontroller.firebase.smartgarage.data

data class AutoCloseOptions (
    var enabled : Boolean = false,
    var timeout : Long = 0,
    var warningTimeout : Long = 0,
    var warningEnabled: Boolean = false,
    var uid : String = "",
    var o_timestamp : String = ""
)
