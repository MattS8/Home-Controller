package com.ms8.homecontroller.firebase.kittydoor.providers

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ms8.homecontroller.firebase.kittydoor.data.Constants
import com.ms8.homecontroller.firebase.smartgarage.functions.SendDebugMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object HardwareOverrideProvider {
    private const val TAG = "KittyDoor_HW_Provider"

    private fun getDatabaseReference() : DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child(Constants.STATUS)
            .child(Constants.HW_OVERRIDE)
    }

    suspend fun getHwOverride(): Flow<Int> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val newMode = snapshot.child(Constants.TYPE).value as Number
                            trySend(newMode.toInt())
                        } catch (e: Exception) {
                            //TODO: Track error within the app's UI
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - Status event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled HW Override Listener - ${error.message}",
                            cause = error.toException())
                    }

                })
        }
    }
}