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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object LightSensorServiceProvider {
    private const val TAG = "KittyDoorLightProvider"

    private fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child(Constants.STATUS)
            .child(Constants.LIGHT_LEVEL)
    }

    suspend fun getLightValue(): Flow<Int> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            Log.i(TAG, "Snapshot: $snapshot")
                            val newLevel = snapshot.child(Constants.LIGHT_LEVEL_VAL).value as Number
                            trySend(newLevel.toInt())
                        } catch (e: Exception) {
                            //TODO: Track error within the app's UI
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - Status event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Kitty Door Light Level Listener - ${error.message}",
                            cause = error.toException())
                    }

                })
            awaitClose {
                Log.d(TAG, "Cancelling kitty door light level listener")
                getDatabaseReference()
                    .removeEventListener(listenerRegistration)
            }
        }
    }
}