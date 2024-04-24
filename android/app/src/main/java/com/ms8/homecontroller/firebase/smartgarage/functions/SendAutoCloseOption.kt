package com.ms8.homecontroller.firebase.smartgarage.functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ms8.homecontroller.firebase.smartgarage.data.AutoCloseOptions
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.AUTO_CLOSE_OPTIONS
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.CONTROLLER
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.GARAGES
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.HOME_GARAGE
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.SYSTEMS
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SendAutoCloseOption {
    private const val TAG = "SmartGarage_AutoCloseOption"

    /**
     * Changes the auto_close_options endpoint for the garage to
     * reflect the new option changes. The Arduino will read in
     * the new option and update it's 'auto close' functionality
     * accordingly.
     */
    fun run(options: AutoCloseOptions) {
        val database = FirebaseDatabase.getInstance()
        Log.d(TAG, "sendAutoCloseOption - updating options to ${options.enabled}, ${options.timeout}, ${options.warningTimeout}")
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.reference
            .child(SYSTEMS)
            .child(HOME_GARAGE)
            .child(CONTROLLER)
            .child(AUTO_CLOSE_OPTIONS)
            .setValue(
                AutoCloseOptions(
                    options.enabled,
                    options.timeout,
                    options.warningTimeout,
                    options.warningEnabled,
                    uid,
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().time))
            )
    }
}