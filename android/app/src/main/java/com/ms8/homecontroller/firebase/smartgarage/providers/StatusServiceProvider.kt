package com.ms8.homecontroller.firebase.smartgarage.providers

import android.util.ArrayMap
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.GARAGES
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.HOME_GARAGE
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.STATUS
import com.ms8.homecontroller.firebase.smartgarage.data.Constants.TYPE
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.smartgarage.functions.SendDebugMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

object StatusServiceProvider {
    private const val TAG = "SmartGarageStatusProvider"

    private val _statusMap = ArrayMap<String, GarageStatus>()

    init {
        _statusMap[GarageStatus.CLOSED.name] = GarageStatus.CLOSED
        _statusMap[GarageStatus.CLOSING.name] = GarageStatus.CLOSING
        _statusMap[GarageStatus.OPEN.name] = GarageStatus.OPEN
        _statusMap[GarageStatus.OPENING.name] = GarageStatus.OPENING
    }

    suspend fun getStatus(): Flow<GarageStatus> {
        val db = FirebaseDatabase.getInstance()
        return callbackFlow {
            val listenerRegistration = db.reference
                .child("status")
                .child("home_garage")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Smart Garage Status Listener - ${error.message}",
                            cause = error.toException())
                    }

                    @Suppress("UNCHECKED_CAST")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            Log.i(TAG, "Snapshot: $snapshot")
                            val snapshotValues = snapshot.value as Map<String, Any?>
                            val newStatus = snapshotValues[TYPE] as String
                            trySend(_statusMap[newStatus]!!)
                        } catch (e: Exception) {
                            //TODO: Track error within the app's UI
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - Status event listener error: ${e.message}")
                        }
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling garage status listener")
                db.reference
                    .child(GARAGES)
                    .child(HOME_GARAGE)
                    .child(STATUS)
                    .removeEventListener(listenerRegistration)
            }
        }
    }
}