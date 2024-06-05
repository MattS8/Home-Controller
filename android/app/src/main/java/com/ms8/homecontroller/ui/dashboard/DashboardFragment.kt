package com.ms8.homecontroller.ui.dashboard

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.ScaleTypes
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.anychart.scales.DateTime
import com.anychart.scales.ScatterBase
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentDashboardBinding
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.solarpanels.data.SolarReadings
import com.ms8.homecontroller.ui.kittydoor.KittyDoorViewModel
import com.ms8.homecontroller.ui.smartgarage.SmartGarageViewModel
import com.ms8.homecontroller.ui.utils.Utils
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().let { act ->
            val kittyDoorViewModel = ViewModelProvider(act)[KittyDoorViewModel::class.java]

            kittyDoorViewModel.status.observe(viewLifecycleOwner) { newStatus ->
                updateKittyDoorStatusUI(newStatus)
            }
            kittyDoorViewModel.hwOverride.observe(viewLifecycleOwner) { overrideEnabled ->
                updateKittyDoorHwOverrideUI(overrideEnabled)
            }

            val garageDoorViewModel = ViewModelProvider(act)[SmartGarageViewModel::class.java]

            garageDoorViewModel.status.observe(viewLifecycleOwner) { newStatus ->
                updateGarageDoorStatusUI(newStatus)
            }

            val dashboardViewModel =
                ViewModelProvider(this)[DashboardViewModel::class.java]
            dashboardViewModel.readings.observe(viewLifecycleOwner) { newReadings ->
                updateSolarReadingsChartUI(newReadings)
            }
        }

        return root
    }

//    private fun setupSolarHumidityChart() {
//        val anyChartView: AnyChartView = binding.chartSolarHumid
//        anyChartView.setProgressBar(binding.progressBar2)
//
//        val cartesian: Cartesian = AnyChart.cartesian()
//        cartesian.title("Humidity Readings (%)")
//        cartesian.background().fill("#242424")
//
//        val data = ArrayList<DataEntry>()
//        data.add(CustomDataEntry("4/28", 37, 34))
//        data.add(CustomDataEntry("4/29", 42, 35))
//        data.add(CustomDataEntry("4/30", 76, 66))
//        data.add(CustomDataEntry("5/1", 90, 75))
//        data.add(CustomDataEntry("5/2", 76, 87))
//        data.add(CustomDataEntry("5/3", 55, 79))
//        data.add(CustomDataEntry("5/4", 45, 34))
//
//        val set = Set.instantiate()
//        set.data(data)
//        val solarHumidityData = set.mapAs("{ x: 'x', high: 'dayHigh', low: 'dayLow' }")
//        val humidColumn = cartesian.rangeColumn(solarHumidityData)
//        humidColumn.name("Humidity Range")
//        humidColumn.color("#E4EBF4")
//
//        cartesian.xAxis(true)
//        cartesian.yAxis(true)
//        cartesian.yScale()
//            .minimum(0)
//            .maximum(100)
//
//        cartesian.legend(true)
//        val labels = cartesian.labels()
//        labels.enabled(true)
//        labels.fontColor("#E4EBF4")
//
//        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})")
//
//        anyChartView.setChart(cartesian)
//    }

    private fun setupSolarTempChart() {
        val anyChartView : AnyChartView = binding.chartSolarPanels
        anyChartView.setProgressBar(binding.progressBar)

        val cartesian: Cartesian = AnyChart.cartesian()

        cartesian.title("Temperature Readings (°F)")
        cartesian.background().fill("#242424")

//        val data = ArrayList<DataEntry>()
//        data.add(CustomDataEntry("4/28", 88, 65))
//        data.add(CustomDataEntry("4/29", 82, 67))
//        data.add(CustomDataEntry("4/30", 77, 52))
//        data.add(CustomDataEntry("5/1", 75, 55))
//        data.add(CustomDataEntry("5/2", 80, 65))
//        data.add(CustomDataEntry("5/3", 79, 55))
//        data.add(CustomDataEntry("5/4", 72, 58))

//        val set = Set.instantiate()
//        set.data(data)
//        val solarTempData = set.mapAs("{ x: 'x', high: 'dayHigh', low: 'dayLow' }")
//        val tempColumn = cartesian.rangeColumn(solarTempData)
//        tempColumn.name("Temperature Range")
//        tempColumn.color("#E4EBF4")
        cartesian.xAxis(true)
        cartesian.yAxis(true)
        cartesian.yScale()
            .minimum(0)
            .maximum(120)

        cartesian.legend(true)
        val labels = cartesian.labels()
        labels.enabled(true)
        labels.fontColor("#E4EBF4")

        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})")

        anyChartView.setChart(cartesian)
    }

    private fun setupHeatIndexChart(newReadings: List<SolarReadings>) {
        val anyChartView : AnyChartView = binding.chartSolarPanels
        anyChartView.setProgressBar(binding.progressBar)

        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)
        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            .yStroke("red", 0, "5 2", "round", "round")

        cartesian.title("Temperature Readings (°F)")
        cartesian.background().fill("#242424")

        val data = ArrayList<DataEntry>()
        var minDate = newReadings[0].timestamp[0]
        var maxDate = newReadings[0].timestamp[0]
        newReadings.forEach { readingGroup ->
            readingGroup.heatIndex.forEachIndexed { index, heatReading ->
                val xFormat = formatTimestamp(readingGroup.timestamp[index])
                data.add(HeatIndexDataEntry(xFormat, heatReading))
                maxDate = readingGroup.timestamp[index]
//                Log.d("SolarTempReadings", "adding $heatReading @ $xFormat")
            }
        }

        val set = Set.instantiate()
        set.data(data)
        val solarTempMapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series1 = cartesian.line(solarTempMapping)
        series1.name("Heat Index")
        series1.color("#E4EBF4")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

//        val tempColumn = cartesian.rangeColumn(solarTempData)
//        tempColumn.name("Temperature Range")
//        tempColumn.color("#E4EBF4")

//        val xAxis = cartesian.xAxis(true).xAxis(0)
//        cartesian.yAxis(true)
        cartesian.yScale()
            .minimum(0)
            .maximum(120)

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)
//        val labels = cartesian.labels()
//        labels.enabled(true)
//        labels.fontColor("#E4EBF4")

        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})")
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        // Set up the x-axis scale
//        xAxis.labels().padding(5.0, 5.0, 5.0, 5.0)
//        xAxis.ticks(true).staggerMaxLines(5)
//        xAxis.scale(ScaleTypes.DATE_TIME)
//        xAxis.labels().format("{%value}{scale}")
        val xAxis = cartesian.xAxis(0)
//        xAxis.drawFirstLabel(false)
//        xAxis.drawLastLabel(false)

        val dateTimeScale = DateTime.instantiate()
        dateTimeScale.maxTicksCount(5)
        dateTimeScale.minimum(minDate)
        dateTimeScale.maximum(maxDate)
        xAxis.scale(dateTimeScale)
        anyChartView.setChart(cartesian)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    private fun updateSolarReadingsChartUI(newReadings: List<SolarReadings>?) {
        if (!newReadings.isNullOrEmpty()) {
            setupHeatIndexChart(newReadings)
        } else {
            Log.d("SolarReadingsChartUI", "No readings received, not doing anything!")
        }
    }

    private fun updateKittyDoorHwOverrideUI(overrideEnabled: Boolean) {
        if (overrideEnabled) {
            binding.tvKittyDoorHwOverride.text = getString(R.string.enabled)
            binding.tvKittyDoorHwOverride.setTextColor(ContextCompat.getColor(
                binding.tvKittyDoorHwOverride.context,
                R.color.hw_enabled))
        } else {
            binding.tvKittyDoorHwOverride.text = getString(R.string.disabled)
            binding.tvKittyDoorHwOverride.setTextColor(ContextCompat.getColor(
                binding.tvKittyDoorHwOverride.context,
                R.color.hw_disabled))
        }
    }

    private fun updateGarageDoorStatusUI(newStatus: GarageStatus) {
        context?.let { ctx ->
            binding.tvGarageDoorStatus.text = newStatus.name
            val statusColor = ContextCompat.getColor(ctx, Utils.getStatusColor(newStatus))
            binding.tvGarageDoorStatus.setTextColor(statusColor)
        }
    }

    private fun updateKittyDoorStatusUI(newStatus: DoorStatus) {
        context?.let { ctx ->
            binding.tvKittyDoorStatus.text = newStatus.name
            val statusColor = ContextCompat.getColor(ctx, Utils.getStatusColor(newStatus))
            binding.tvKittyDoorStatus.setTextColor(statusColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class HeatIndexDataEntry(val x: String, val heatIndex: Number): ValueDataEntry(x, heatIndex) {

    }

    private class CustomDataEntry(
        val x: String,
        val dayHigh: Number,
        val dayLow: Number,
    ) : DataEntry() {
        init {
            setValue("x", x)
            setValue("dayHigh", dayHigh)
            setValue("dayLow", dayLow)
        }
    }
}