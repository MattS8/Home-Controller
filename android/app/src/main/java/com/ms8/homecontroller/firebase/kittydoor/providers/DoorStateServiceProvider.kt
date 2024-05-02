package com.ms8.homecontroller.firebase.kittydoor.providers

import android.util.ArrayMap
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ms8.homecontroller.firebase.kittydoor.data.Constants
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.smartgarage.functions.SendDebugMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object DoorStateServiceProvider {
    private const val TAG = "KittyDoorStatusProvider"

    private val _statusMap = ArrayMap<String, DoorStatus>()

    init {
        _statusMap[DoorStatus.CLOSED.name] = DoorStatus.CLOSED
        _statusMap[DoorStatus.CLOSING.name] = DoorStatus.CLOSING
        _statusMap[DoorStatus.OPEN.name] = DoorStatus.OPEN
        _statusMap[DoorStatus.OPENING.name] = DoorStatus.OPENING
    }

    private fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child(Constants.SYSTEMS)
            .child(Constants.KITTY_DOOR)
            .child(Constants.STATUS)
            .child(Constants.DOOR_STATE)
    }

    suspend fun getStatus(): Flow<DoorStatus> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            Log.i(TAG, "Snapshot: $snapshot")
                            val newStatus = snapshot.child(Constants.STATUS).value as String
                            trySend(_statusMap[newStatus]!!)
                        } catch (e: Exception) {
                            //TODO: Track error within the app's UI
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - Status event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Kitty Door Status Listener - ${error.message}",
                            cause = error.toException())
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling kitty door status listener")
                getDatabaseReference().removeEventListener(listenerRegistration)
            }
        }
    }
}