package com.ms8.homecontroller.firebase.smartgarage.functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ms8.homecontroller.firebase.smartgarage.data.ActionType
import com.ms8.homecontroller.firebase.smartgarage.data.Constants
import com.ms8.homecontroller.firebase.smartgarage.data.GarageAction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SendGarageAction {
    private const val TAG = "SmartGarage_SendAction"

    /**
     * Changes the action endpoint for the garage to the
     * specified action type. The Arduino will  read this
     * action and act accordingly.
     *
     * @param actionType The type of action to send to the garage
     */
    fun run(actionType : ActionType) {
        val database = FirebaseDatabase.getInstance()
        Log.d(TAG, "sendGarageAction - sending action ${actionType.name} ($database)")
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.reference
            .child(Constants.GARAGES)
            .child(Constants.HOME_GARAGE)
            .child(Constants.CONTROLLER)
            .child(Constants.ACTION)
            .setValue(
                GarageAction(actionType.name,
                    uid,
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().time))
            )
    }
}