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

        return root
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
}