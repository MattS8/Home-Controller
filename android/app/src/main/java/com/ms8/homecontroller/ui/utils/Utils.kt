package com.ms8.homecontroller.ui.utils

import android.util.Log
import androidx.collection.ArrayMap
import com.ms8.homecontroller.R
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus

object Utils {
    private val kdStatusColors = ArrayMap<DoorStatus, Int>()
    private val gdStatusColors = ArrayMap<GarageStatus, Int>()

    init {
        kdStatusColors[DoorStatus.CLOSED] = R.color.close
        kdStatusColors[DoorStatus.CLOSING] = R.color.closing
        kdStatusColors[DoorStatus.OPEN] = R.color.open
        kdStatusColors[DoorStatus.OPENING] = R.color.opening

        gdStatusColors[GarageStatus.CLOSED] = R.color.close
        gdStatusColors[GarageStatus.CLOSING] = R.color.closing
        gdStatusColors[GarageStatus.OPEN] = R.color.open
        gdStatusColors[GarageStatus.OPENING] = R.color.opening
    }

    fun getStatusColor(status: DoorStatus): Int {
        kdStatusColors[status]?.let {
            return it
        }

        Log.e("UiUtils", "Unable to get status color from ${status.name}!")
        return R.color.white
    }

    fun getStatusColor(status: GarageStatus): Int {
        gdStatusColors[status]?.let {
            return it
        }

        Log.e("UiUtils", "Unable to get status color from ${status.name}!")
        return R.color.white
    }

    fun getGarageTimeoutProgress(timeout: Long): Int = (timeout / (15 * 60 * 1000)).toInt()

    fun getGarageWarningTimeout(progress: Int) : Long  = (15 * progress * 60 * 1000).toLong()
    fun getGarageTimeout(progress: Int): Long = (15 * (progress + 1) * 60 * 1000).toLong()
}