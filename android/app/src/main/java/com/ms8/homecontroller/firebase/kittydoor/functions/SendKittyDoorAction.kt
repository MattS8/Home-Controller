package com.ms8.homecontroller.firebase.kittydoor.functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ms8.homecontroller.firebase.kittydoor.data.ActionType
import com.ms8.homecontroller.firebase.kittydoor.data.Constants
import com.ms8.homecontroller.firebase.kittydoor.data.KittyAction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SendKittyDoorAction {
    private const val TAG = "KittyDoor_SendAction"

    fun run(actionType : ActionType) {
        val database = FirebaseDatabase.getInstance()
        Log.d(TAG, "sendGarageAction - sending action ${actionType.name} ($database)")
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.reference
            .child(Constants.SYSTEMS)
            .child(Constants.KITTY_DOOR)
            .child(Constants.CONTROLLER)
            .child(Constants.ACTION)
            .setValue(
                KittyAction(actionType.name,
                    uid,
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().time))
            )
    }
}