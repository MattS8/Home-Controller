package com.ms8.homecontroller.ui.kittydoor

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.ms8.flashbar.Flashbar
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentKittyDoorBinding
import com.ms8.homecontroller.firebase.kittydoor.data.ActionType
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.kittydoor.functions.SendKittyDoorAction
import com.ms8.homecontroller.ui.HomeControllerActivity
import com.ms8.homecontroller.ui.utils.Utils

class KittyDoorFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentKittyDoorBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Status variables
    private var progDrawable: AnimatedVectorDrawableCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKittyDoorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding?.apply {
            btnOpen.setOnClickListener(this@KittyDoorFragment)
            btnClose.setOnClickListener(this@KittyDoorFragment)
            btnCheckLightLevel.setOnClickListener(this@KittyDoorFragment)
            btnEnableAuto.setOnClickListener(this@KittyDoorFragment)

            progDrawable = context?.let { ctx ->
                AnimatedVectorDrawableCompat.create(
                    ctx,
                    R.drawable.av_progress
                )
            }
            progDrawable?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    _binding?.progDoorStatus?.post { progDrawable?.start() }
                }
            })
        }

        // Observer viewModel
        requireActivity().let { act ->
            val kittyDoorViewModel =
                ViewModelProvider(act)[KittyDoorViewModel::class.java]

            kittyDoorViewModel.lightLevel.observe(viewLifecycleOwner, Observer { newLightLevel ->
                updateLightLevelUI(newLightLevel,
                    kittyDoorViewModel.kittyOptions.value?.openLightLevel,
                    kittyDoorViewModel.kittyOptions.value?.closeLightLevel)
            })
            kittyDoorViewModel.status.observe(viewLifecycleOwner, Observer { newStatus ->
                updateStatusUI(newStatus)
            })
            kittyDoorViewModel.hwOverride.observe(viewLifecycleOwner, Observer { newHwOverride ->
                updateHwOverrideUI(newHwOverride)
            })
            kittyDoorViewModel.overrideAuto.observe(viewLifecycleOwner, Observer { newOverrideAuto ->
                updateOverrideAutoUI(newOverrideAuto)
            })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    <!> On Click Functions <!>
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOpen -> openKittyDoor()
            R.id.btnClose -> closeKittyDoor()
            R.id.btnEnableAuto -> enableAuto()
            R.id.btnCheckLightLevel -> SendKittyDoorAction.run(ActionType.READ_LIGHT_LEVEL)
        }
    }

    private fun openKittyDoor() {
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]
        if (kittyDoorViewModel.hwOverride.value == true) {
            showHwOverrideNotification()
        } else {
            SendKittyDoorAction.run(ActionType.OPEN_DOOR)
        }
    }

    private fun closeKittyDoor() {
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]
        if (kittyDoorViewModel.hwOverride.value == true) {
            showHwOverrideNotification()
        } else {
            SendKittyDoorAction.run(ActionType.CLOSE_DOOR)
        }
    }

    private fun enableAuto() {
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]
        if (kittyDoorViewModel.hwOverride.value == true) {
            showHwOverrideNotification()
        } else {
            SendKittyDoorAction.run(ActionType.SET_TO_AUTO)
        }
    }

//    <!> UI Updaters <!>
    private fun updateOverrideAutoUI(overrideAutoEnabled: Boolean) {
        _binding?.let { b ->
            context?.let {ctx ->
                val states = arrayOf(intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_pressed))
                val colors = if(!overrideAutoEnabled)
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoEnabled),
                        ContextCompat.getColor(ctx, R.color.autoDisabled),
                        ContextCompat.getColor(ctx, R.color.autoChecked),
                        ContextCompat.getColor(ctx, R.color.autoPressed)
                    ) else
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoDisabled),
                        ContextCompat.getColor(ctx, R.color.autoDisabled),
                        ContextCompat.getColor(ctx, R.color.autoDisabled),
                        ContextCompat.getColor(ctx, R.color.autoDisabledPressed)
                    )
                val textColors = if (!overrideAutoEnabled)
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoText),
                        ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                        ContextCompat.getColor(ctx, R.color.autoText),
                        ContextCompat.getColor(ctx, R.color.autoTextPressed)
                    ) else
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                        ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                        ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                        ContextCompat.getColor(ctx, R.color.autoTextDisabledPressed)
                    )

                val openColors = if (overrideAutoEnabled)
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.open),
                        ContextCompat.getColor(ctx, R.color.open),
                        ContextCompat.getColor(ctx, R.color.open),
                        ContextCompat.getColor(ctx, R.color.opening)
                    ) else
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoColorOpenDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorOpenDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorOpenDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorOpenDisabledPressed)
                    )
                val closeColors = if (overrideAutoEnabled)
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.close),
                        ContextCompat.getColor(ctx, R.color.close),
                        ContextCompat.getColor(ctx, R.color.close),
                        ContextCompat.getColor(ctx, R.color.closing)
                    ) else
                    intArrayOf(
                        ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                        ContextCompat.getColor(ctx, R.color.autoColorCloseDisabledPressed)
                    )

                b.btnOpen.backgroundTintList = ColorStateList(states, openColors)
                b.btnClose.backgroundTintList = ColorStateList(states, closeColors)
                b.btnEnableAuto.backgroundTintList = ColorStateList(states, colors)
                b.btnEnableAuto.setTextColor(ColorStateList(states,textColors))
            }
        }
    }

    private fun updateHwOverrideUI(hwOverrideEnabled: Boolean) {
        _binding?.let { b ->
            if (hwOverrideEnabled) {
                b.tvHardwareOverrideVal.text = getString(R.string.enabled)
                b.tvHardwareOverrideVal.setTextColor(ContextCompat.getColor(
                    b.tvHardwareOverrideVal.context,
                    R.color.hw_enabled))
            } else {
                b.tvHardwareOverrideVal.text = getString(R.string.disabled)
                b.tvHardwareOverrideVal.setTextColor(ContextCompat.getColor(
                    b.tvHardwareOverrideVal.context,
                    R.color.hw_disabled))
            }
        }
    }
    private fun updateLightLevelUI(newLightLevel: Int,
                                   openLightLevel: Int?,
                                   closeLightLevel: Int?) {
        context?.let { ctx ->
            binding.tvLightLevel.text = newLightLevel.toString()

            binding.tvLightLevel.setTextColor(ContextCompat.getColor(ctx, when {
                newLightLevel >= (openLightLevel ?: 1024) -> R.color.lightOpen
                newLightLevel <= (closeLightLevel ?: 0) -> R.color.lightClose
                else -> R.color.white
            }))
        }
    }

    private fun updateStatusUI(newStatus: DoorStatus) {
        context?.let { ctx ->
            // Set status text
            binding.tvStatus.text = newStatus.name
            val statusColor = ContextCompat.getColor(ctx, Utils.getStatusColor(newStatus))
            binding.tvStatus.setTextColor(statusColor)

            // Show/hide animated progress drawable
            val showProgressView = newStatus == DoorStatus.OPENING || newStatus == DoorStatus.CLOSING
            binding.progDoorStatus.apply {
                if (showProgressView) {
                    progDrawable?.let {
                        it.setTint(statusColor)
                        setImageDrawable(it)
                        it.start()
                    }
                }
                animate()
                    .alpha(if (showProgressView) 1f else 0f)
                    .setDuration(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    //    <!> UI Helpers <!>

    private fun showHwOverrideNotification() {
        activity?.let { act ->
            if (act is HomeControllerActivity) {
                act.showFlashbar(Flashbar.Builder(act)
                    .icon(R.drawable.ic_warning_yellow_24dp)
                    .iconColorFilter(ContextCompat.getColor(act, R.color.warningYellow))
                    .backgroundColor(ContextCompat.getColor(act, com.ms8.flashbar.R.color.slate_black))
                    .showIcon()
                    .title(R.string.hw_override_enabled)
                    .message(R.string.hw_override_enabled_msg)
                    .positiveActionText(android.R.string.ok)
                    .titleAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Headline5)
                    .messageAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
                    .positiveActionTextAppearance(R.style.Theme_HomeController_TextAppearance_FlashbarWarning_Positive)
                    .negativeActionTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1))
            } else {
                Log.e("KDF", "Parent activity does not implement FlashbarActivity!")
            }
        }
    }
}