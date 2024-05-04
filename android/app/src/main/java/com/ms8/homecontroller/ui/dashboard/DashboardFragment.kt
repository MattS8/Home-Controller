package com.ms8.homecontroller.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.charts.Cartesian
import com.anychart.charts.Waterfall
import com.anychart.data.Set
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentDashboardBinding
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.ui.kittydoor.KittyDoorViewModel
import com.ms8.homecontroller.ui.smartgarage.SmartGarageViewModel
import com.ms8.homecontroller.ui.utils.Utils

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
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().let { act ->
            val kittyDoorViewModel = ViewModelProvider(act)[KittyDoorViewModel::class.java]

            kittyDoorViewModel.status.observe(viewLifecycleOwner, Observer { newStatus ->
                updateKittyDoorStatusUI(newStatus)
            })
            kittyDoorViewModel.hwOverride.observe(viewLifecycleOwner, Observer { overrideEnabled ->
                updateKittyDoorHwOverrideUI(overrideEnabled)
            })

            val garageDoorViewModel = ViewModelProvider(act)[SmartGarageViewModel::class.java]

            garageDoorViewModel.status.observe(viewLifecycleOwner, Observer { newStatus ->
                updateGarageDoorStatusUI(newStatus)
            })
        }

        setupSolarTempChart()
//        setupSolarHumidityChart()

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

        val data = ArrayList<DataEntry>()
        data.add(CustomDataEntry("4/28", 88, 65))
        data.add(CustomDataEntry("4/29", 82, 67))
        data.add(CustomDataEntry("4/30", 77, 52))
        data.add(CustomDataEntry("5/1", 75, 55))
        data.add(CustomDataEntry("5/2", 80, 65))
        data.add(CustomDataEntry("5/3", 79, 55))
        data.add(CustomDataEntry("5/4", 72, 58))

        val set = Set.instantiate()
        set.data(data)
        val solarTempData = set.mapAs("{ x: 'x', high: 'dayHigh', low: 'dayLow' }")
        val tempColumn = cartesian.rangeColumn(solarTempData)
        tempColumn.name("Temperature Range")
        tempColumn.color("#E4EBF4")
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