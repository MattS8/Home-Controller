package com.ms8.homecontroller.ui.smartgarage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.addCallback
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentSmartGarageBinding
import com.ms8.homecontroller.databinding.OptionsSgAutoCloseBinding
import com.ms8.homecontroller.firebase.smartgarage.data.ActionType
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.smartgarage.functions.SendGarageAction
import com.ms8.homecontroller.ui.HomeControllerActivity
import com.ms8.homecontroller.ui.utils.Utils

class SmartGarageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSmartGarageBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var optionsBinding: OptionsSgAutoCloseBinding? = null

    // Status variables
    private var garageProgressDrawable: AnimatedVectorDrawableCompat? = null
    private val garageStatusColors = ArrayMap<GarageStatus, Int>()
    private var drawer: Drawer? = null

    init {
        garageStatusColors[GarageStatus.CLOSED] = R.color.close
        garageStatusColors[GarageStatus.CLOSING] = R.color.closing
        garageStatusColors[GarageStatus.OPEN] = R.color.open
        garageStatusColors[GarageStatus.OPENING] = R.color.opening
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val smartGarageViewModel =
            ViewModelProvider(this)[SmartGarageViewModel::class.java]

        _binding = FragmentSmartGarageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding?.let {
            it.btnOpen.setOnClickListener(this)
            it.btnClose.setOnClickListener(this)
            it.btnOptions.setOnClickListener(this)

            garageProgressDrawable =
                context?.let { ctx ->
                    AnimatedVectorDrawableCompat.create(
                        ctx,
                        R.drawable.av_progress
                    )
                }
            garageProgressDrawable?.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    _binding?.progGarageStatus?.post { garageProgressDrawable?.start() }
                }
            })
        }
        smartGarageViewModel.status.observe(viewLifecycleOwner) { newStatus ->
            updateStatusUI(newStatus)
        }
        setupOptions(inflater)

        return root
    }

    private fun setupOptions(inflater: LayoutInflater) {
        val smartGarageViewModel =
            ViewModelProvider(this)[SmartGarageViewModel::class.java]
        optionsBinding = OptionsSgAutoCloseBinding.inflate(inflater, null, false)
        smartGarageViewModel.autoCloseEnabled.observe(viewLifecycleOwner) { newEnabled ->
            optionsBinding?.swAutoClose?.isChecked = newEnabled
            updateOptionsSaveButton()
            updateWarningAfterUI()
            optionsBinding?.let { b ->
                b.swAutoClose.isChecked = newEnabled
                updateAutoCloseUI(newEnabled)
            }
        }
        smartGarageViewModel.autoCloseWarningEnabled.observe(viewLifecycleOwner) { newVal ->
            optionsBinding?.swWarnBeforeClosing?.isChecked = newVal
            updateOptionsSaveButton()
            updateWarningAfterUI()
        }
        smartGarageViewModel.autoCloseTimeout.observe(viewLifecycleOwner) { newVal ->
            val newProgress = Utils.getGarageTimeoutProgress(newVal.toLong())
            optionsBinding?.sbCloseAfter?.progress = newProgress
            optionsBinding?.let { b ->
                b.sbCloseAfter.progress = newProgress
                b.tvCloseAfterValue.text = getTimeTextFromProgress(newProgress)
            }
            updateOptionsSaveButton()
        }
        smartGarageViewModel.autoCloseWarningTimeout.observe(viewLifecycleOwner) { newVal ->
            val newProgress = Utils.getGarageTimeoutProgress(newVal.toLong())
            optionsBinding?.let { b ->
                b.sbWarningAfter.progress = newProgress
                b.tvWarningAfterValue.text = getTimeTextFromProgress(newProgress)
            }

            updateOptionsSaveButton()
        }
        optionsBinding!!.swAutoClose.setOnCheckedChangeListener { _, newChecked ->
            updateWarningAfterUI()
            updateOptionsSaveButton()
            updateAutoCloseUI(newChecked)
        }
        optionsBinding!!.swWarnBeforeClosing.setOnCheckedChangeListener { _, _ ->
            updateWarningAfterUI()
            updateOptionsSaveButton()
        }
        val seekListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateWarningAfterUI()
                updateOptionsSaveButton()
                optionsBinding?.let { b ->
                    b.tvCloseAfterValue.text = getTimeTextFromProgress(b.sbCloseAfter.progress)
                    b.tvWarningAfterValue.text = getTimeTextFromProgress(b.sbWarningAfter.progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        optionsBinding!!.sbWarningAfter.setOnSeekBarChangeListener(seekListener)
        optionsBinding!!.sbCloseAfter.setOnSeekBarChangeListener(seekListener)

        // Setup drawer
        requireActivity().let { act ->
            drawer = DrawerBuilder()
                .withActivity(act)
                .withDrawerGravity(Gravity.END)
                .withFullscreen(true)
                .withCloseOnClick(false)
                .withTranslucentStatusBar(false)
                .withCustomView(optionsBinding!!.root)
                .build()
            drawer!!.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            drawer!!.closeDrawer()

            act.onBackPressedDispatcher.addCallback(this) {
                drawer?.closeDrawer()
            }

            if (act is HomeControllerActivity) {
                act.setOptionsDrawer(drawer!!)
            }
        }
    }

    private fun updateAutoCloseUI(newEnabled: Boolean) {
        optionsBinding?.let { b ->
            b.tvCloseAfter.isEnabled = newEnabled
            b.tvCloseAfterValue.isEnabled = newEnabled
            b.sbCloseAfter.isEnabled = newEnabled
        }
    }

    private fun updateWarningAfterUI() {
        optionsBinding?.let { b ->
            val warningAfterEnabled = b.swAutoClose.isChecked && b.swWarnBeforeClosing.isChecked

            b.tvWarningAfter.isEnabled = warningAfterEnabled
            b.tvWarningAfterValue.isEnabled = warningAfterEnabled
            b.sbWarningAfter.isEnabled = warningAfterEnabled
            b.swWarnBeforeClosing.isEnabled = b.swAutoClose.isChecked
        }
    }

    private fun updateOptionsSaveButton() {
        optionsBinding?.let { b ->
            val viewModel = ViewModelProvider(this)[SmartGarageViewModel::class.java]
            val progressToTimeout = Utils.getGarageTimeout(b.sbCloseAfter.progress)
            val progressToWarningTimeout = Utils.getGarageWarningTimeout(b.sbWarningAfter.progress)
            val newSettingsToSave = viewModel.autoCloseEnabled.value != b.swAutoClose.isChecked
                    || viewModel.autoCloseTimeout.value?.toLong() != progressToTimeout
                    || viewModel.autoCloseWarningTimeout.value?.toLong() != progressToWarningTimeout
                    || viewModel.autoCloseWarningEnabled.value != b.swWarnBeforeClosing.isChecked

            b.btnSaveSettings.isEnabled = newSettingsToSave

//            Log.i("##TEST", "closeAfter: $progToTimeout vs ${viewModel.autoCloseTimeout.value?.toLong()}" +
//                    "\nsendWarningAfter: $progToWarningTimeout vs ${viewModel.autoCloseWarningTimeout.value?.toLong()}" +
//                    "\nautoCloseEnabled: ${b.swAutoClose.isChecked} vs ${viewModel.autoCloseEnabled.value}" +
//                    "\nautoCloseWarningEnabled: ${b.swWarnBeforeClosing.isChecked} vs ${viewModel.autoCloseWarningEnabled.value}" +
//                    "\nnewSettingsToSave: $newSettingsToSave")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOpen -> SendGarageAction.run(ActionType.OPEN)
            R.id.btnClose -> SendGarageAction.run(ActionType.CLOSE)
            R.id.btnOptions -> openOptionsView()
            R.id.btnSaveSettings -> SaveSettings()
        }
    }

    private fun SaveSettings() {

    }

    private fun openOptionsView() {
        requireActivity().let { act ->
            if (act is HomeControllerActivity) {
                act.openDrawer()
            }
        }
    }

    private fun getStatusColor(status: GarageStatus): Int {
        garageStatusColors[status]?.let {
            return it
        }

        Log.e("SmartGarage", "Unable to get status color from ${status.name}!")
        return R.color.white
    }
    private fun getTimeTextFromProgress(progress: Int): String {
        return "${progress * 15} min"
    }
    private fun updateStatusUI(newStatus: GarageStatus) {
        context?.let { ctx ->
            // Set status text
            binding.tvStatus.text = newStatus.name
            val statusColor = ContextCompat.getColor(ctx, getStatusColor(newStatus))
            binding.tvStatus.setTextColor(statusColor)

            // Show/hide animated progress drawable
            val showProgressView =
                newStatus == GarageStatus.OPENING || newStatus == GarageStatus.CLOSING
            binding.progGarageStatus.apply {
                if (showProgressView) {
                    garageProgressDrawable?.setTint(statusColor)
                    setImageDrawable(garageProgressDrawable)
                    garageProgressDrawable?.start()
                }
                animate()
                    .alpha(if (showProgressView) 1f else 0f)
                    .setDuration(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }

    }
}