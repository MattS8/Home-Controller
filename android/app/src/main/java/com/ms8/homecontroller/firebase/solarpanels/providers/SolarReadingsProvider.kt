package com.ms8.homecontroller.firebase.solarpanels.providers

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ms8.homecontroller.firebase.solarpanels.data.SolarReadings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date

object SolarReadingsProvider {
    private const val TAG = "SolarReadingsProvider"


    private fun getDatabaseReference(): CollectionReference {
        return Firebase.firestore.collection("SolarReadings")
    }
    suspend fun getSolarReadings(startTime: Long, endTime: Long): Flow<List<SolarReadings>?> {
        Log.d("SolarReadingsProv", "Fetching from ${Date(startTime)} to ${Date(endTime)}")
        return callbackFlow {
            val filter = Filter.and(
                Filter.greaterThanOrEqualTo("__name__", startTime.toString()),
                Filter.lessThanOrEqualTo("__name__", endTime.toString()))
            val query = getDatabaseReference().where(filter)
                .orderBy("__name__", Query.Direction.ASCENDING)
                .get()
            val onSuccessListener = query
                .addOnSuccessListener {snapshots ->
                    if (!snapshots.isEmpty) {
                        val list : List<DocumentSnapshot> = snapshots.documents
                        val readingsList = ArrayList<SolarReadings>()
                        list.forEach { doc ->
                            doc.toObject(SolarReadings::class.java)?.let {
                                readingsList.add(it)
                            }
                        }
                        Log.d("SolarReadingsProv", "Received ${readingsList.size} objects!")
                        trySend(readingsList)
                    } else {
                        trySend(ArrayList())
                    }
                }
            val onErrorListener = query.addOnFailureListener {
                Log.e("SolarReadingsProv", "Query Failed: ", it)
            }
            val onCanceledListener = query.addOnCanceledListener {
                Log.w("SolarReadingsProv", "Query Cancelled! ")
            }
            awaitClose {
                Log.d("SolarReadingsProv", "Cancelling solar readings query!")
            }
        }
    }
}