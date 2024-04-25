package com.ms8.homecontroller.ui.kittydoor

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.andrognito.flashbar.Flashbar
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentKittyDoorBinding
import com.ms8.homecontroller.firebase.kittydoor.data.ActionType
import com.ms8.homecontroller.firebase.kittydoor.data.Constants
import com.ms8.homecontroller.firebase.kittydoor.data.DoorStatus
import com.ms8.homecontroller.firebase.kittydoor.functions.SendKittyDoorAction

class KittyDoorFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentKittyDoorBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Status variables
    private var progDrawable: AnimatedVectorDrawableCompat? = null
    private val statusColors = ArrayMap<DoorStatus, Int>()

    // Flashbar notification
    private var flashbar: Flashbar? = null

    init {
        statusColors[DoorStatus.CLOSED] = R.color.close
        statusColors[DoorStatus.CLOSING] = R.color.closing
        statusColors[DoorStatus.OPEN] = R.color.open
        statusColors[DoorStatus.OPENING] = R.color.opening
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]

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
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    <!> On Click Functions <!>

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOpen -> OpenKittyDoor()
            R.id.btnClose -> SendKittyDoorAction.run(ActionType.CLOSE_DOOR)
            R.id.btnEnableAuto -> SendKittyDoorAction.run(ActionType.SET_TO_AUTO)
            R.id.btnCheckLightLevel -> SendKittyDoorAction.run(ActionType.READ_LIGHT_LEVEL)
        }
    }

    private fun OpenKittyDoor() {
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]
        val hwOverride = kittyDoorViewModel.hwOverride.value

        when (hwOverride) {
            Constants.HW_OVERRIDE_ENABLED -> {
                showHwOverrideNotification()
            }
            Constants.HW_OVERRIDE_DISABLED -> {
                SendKittyDoorAction.run(ActionType.OPEN_DOOR)
            }
            else -> {
                Log.e("KittyDoor", "Unknown hw Override state ($hwOverride)")
            }
        }
    }

//    <!> UI Updaters <!>
    private fun updateHwOverrideUI(newHwOverride: Int) {
        _binding?.let { b ->
            context?.let {ctx ->
                val states = arrayOf(intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_pressed))

                when (newHwOverride) {
                    Constants.HW_OVERRIDE_ENABLED -> {
                        val colors = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.autoEnabled),
                            ContextCompat.getColor(ctx, R.color.autoDisabled),
                            ContextCompat.getColor(ctx, R.color.autoChecked),
                            ContextCompat.getColor(ctx, R.color.autoPressed)
                        )
                        val textColors = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.autoText),
                            ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                            ContextCompat.getColor(ctx, R.color.autoText),
                            ContextCompat.getColor(ctx, R.color.autoTextPressed)
                        )
                        val closeColors = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.close),
                            ContextCompat.getColor(ctx, R.color.close),
                            ContextCompat.getColor(ctx, R.color.close),
                            ContextCompat.getColor(ctx, R.color.closing)
                        )

                    }
                    Constants.HW_OVERRIDE_DISABLED -> {
                        val color = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.autoDisabled),
                            ContextCompat.getColor(ctx, R.color.autoDisabled),
                            ContextCompat.getColor(ctx, R.color.autoDisabled),
                            ContextCompat.getColor(ctx, R.color.autoDisabledPressed)
                        )
                        val textColors = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                            ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                            ContextCompat.getColor(ctx, R.color.autoTextDisabled),
                            ContextCompat.getColor(ctx, R.color.autoTextDisabledPressed)
                        )
                        val closeColors = intArrayOf(
                            ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                            ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                            ContextCompat.getColor(ctx, R.color.autoColorCloseDisabled),
                            ContextCompat.getColor(ctx, R.color.autoColorCloseDisabledPressed)
                        )
                    }
                    else -> {
                        Log.w("KittyDoor", "Unable to update UI with hwOverride code $newHwOverride")
                    }
                }
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
            val statusColor = ContextCompat.getColor(ctx, getStatusColor(newStatus))
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
        activity?.let { activity ->
            flashbar = Flashbar.Builder(activity)
                .icon(R.drawable.ic_warning_yellow_24dp)
                .iconColorFilter(ContextCompat.getColor(activity, R.color.warningYellow))
                .showIcon()
                .title(R.string.hw_override_enabled)
                .message(R.string.hw_override_enabled_msg)
                .positiveActionText(android.R.string.ok)
                .positiveActionTapListener(object : Flashbar.OnActionTapListener {
                    override fun onActionTapped(bar: Flashbar) {
                        flashbar?.dismiss()
                    }
                })
                .barDismissListener(object : Flashbar.OnBarDismissListener {
                    override fun onDismissProgress(bar: Flashbar, progress: Float) {}

                    override fun onDismissed(bar: Flashbar, event: Flashbar.DismissEvent) {
                        flashbar = null
                    }

                    override fun onDismissing(bar: Flashbar, isSwiped: Boolean) {}
                })
                .backgroundColor(ContextCompat.getColor(activity, R.color.background))
                .titleAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Headline5)
                .messageAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
                .positiveActionTextAppearance(R.style.Theme_HomeController_TextAppearance_FlashbarWarning_Positive)
                .negativeActionTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1)
                .build()
            flashbar?.show()
        }
    }

    private fun getStatusColor(status: DoorStatus): Int {
        statusColors[status]?.let {
            return it
        }

        Log.e("KittyDoor", "Unable to get status color from ${status.name}!")
        return R.color.white
    }
}