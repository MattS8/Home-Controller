package com.ms8.homecontroller.firebase.smartgarage.functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ms8.homecontroller.firebase.smartgarage.data.Constants
import com.ms8.homecontroller.firebase.smartgarage.data.DebugMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SendDebugMessage {
    private const val TAG = "SmartGarage_Debug"
    /**
     * Sends a debug message to firebase for remote
     * logging. The message is stored under debug/uid
     * with the identifier being a timestamp in
     * the format yyyy-MM-dd HH:mm:ss.
     *
     * @param message The debug message to store on firebase
     */
    fun run(message: String) {
//        val database = FirebaseDatabase.getInstance()
//        Log.d(TAG, "sendDebugMessage - sending debug message $message ($database)")
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        database.reference
//            .child(Constants.DEBUG)
//            .child(Constants.HOME_GARAGE)
//            .child(uid)
//            .child(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().time))
//            .setValue(DebugMessage(message))

        Log.i(TAG, "Remote firebase debug messages are disabled at this time!")
    }
}