package com.ms8.homecontroller.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms8.homecontroller.firebase.solarpanels.data.SolarReadings
import com.ms8.homecontroller.firebase.solarpanels.providers.SolarReadingsProvider
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DashboardViewModel : ViewModel() {

    private val _readings = MutableLiveData<List<SolarReadings>>()
    val readings: LiveData<List<SolarReadings>> = _readings
    val solarReadingsDateRange: DateRange = DateRange()

    init {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        solarReadingsDateRange.setStartingDate(year, month, day)
        solarReadingsDateRange.setEndingDate(year, month, day)
        val startingTime = solarReadingsDateRange.getStartingDate().time - (86400000L * 40)               // Get 2 days worth of data
        val endingTime = solarReadingsDateRange.getEndingDate().time
        viewModelScope.launch {
            SolarReadingsProvider.getSolarReadings(startingTime, endingTime).collect { newReadings ->

                if (newReadings != null) {
                    _readings.value = newReadings!!
                } else {
                    _readings.value = ArrayList()
                }
            }
        }
    }

    inner class DateRange {

        fun setStartingDate(year: Int, month: Int, day: Int) {
            startingYear = year
            startingMonth = month
            startingDay = day
        }

        fun setEndingDate(year: Int, month: Int, day: Int) {
            endingYear = year
            endingMonth = month
            endingDay = day
        }

        fun getStartingDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.set(startingYear, startingMonth, startingDay)
            return calendar.time
        }

        fun getEndingDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.set(endingYear, endingMonth, endingDay)
            return calendar.time
        }

        private var startingYear: Int = 0
        private var startingMonth: Int = 0
        private var startingDay: Int = 0
        private var endingYear: Int = 0
        private var endingMonth: Int = 0
        private var endingDay: Int = 0
    }
}