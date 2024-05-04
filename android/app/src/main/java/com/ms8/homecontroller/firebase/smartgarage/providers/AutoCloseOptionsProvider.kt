package com.ms8.homecontroller.firebase.smartgarage.providers



import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.ms8.homecontroller.firebase.smartgarage.functions.SendDebugMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object AutoCloseOptionsProvider {
    private const val TAG = "AutoCloseOptProvider"

    private fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("garages")
            .child("home_garage")
            .child("controller")
            .child("defaults")
            .child("auto_close_options")
    }


    suspend fun getEnabled(): Flow<Boolean> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .child("enabled")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val newVal = snapshot.value as Boolean
                            trySend(newVal)
                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - auto_close options event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Smart Garage Options Listener - ${error.message}",
                            cause = error.toException())
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling auto_close opt listener")
                getDatabaseReference()
                    .removeEventListener(listenerRegistration)
            }
        }
    }

    suspend fun getTimeout(): Flow<Number> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .child("timeout")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val newVal = snapshot.value as Number
                            trySend(newVal)
                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - auto_close options event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Smart Garage Options Listener - ${error.message}",
                            cause = error.toException())
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling auto_close opt listener")
                getDatabaseReference()
                    .removeEventListener(listenerRegistration)
            }
        }
    }

    suspend fun getWarningEnabled(): Flow<Boolean> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .child("warningEnabled")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val newVal = snapshot.value as Boolean
                            trySend(newVal)
                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - auto_close options event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Smart Garage Options Listener - ${error.message}",
                            cause = error.toException())
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling auto_close opt listener")
                getDatabaseReference()
                    .removeEventListener(listenerRegistration)
            }
        }
    }

    suspend fun getWarningTimeout(): Flow<Number> {
        return callbackFlow {
            val listenerRegistration = getDatabaseReference()
                .child("warningTimeout")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val newVal = snapshot.value as Number
                            trySend(newVal)
                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            SendDebugMessage.run("$TAG - auto_close options event listener error: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel(message = "Cancelled Smart Garage Options Listener - ${error.message}",
                            cause = error.toException())
                    }
                })
            awaitClose {
                Log.d(TAG, "Cancelling auto_close opt listener")
                getDatabaseReference()
                    .removeEventListener(listenerRegistration)
            }
        }
    }
}