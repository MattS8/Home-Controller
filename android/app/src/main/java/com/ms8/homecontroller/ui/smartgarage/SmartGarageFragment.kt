package com.ms8.homecontroller.ui.smartgarage

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentSmartGarageBinding
import com.ms8.homecontroller.firebase.smartgarage.data.ActionType
import com.ms8.homecontroller.firebase.smartgarage.data.GarageStatus
import com.ms8.homecontroller.firebase.smartgarage.functions.SendGarageAction

class SmartGarageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSmartGarageBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Status variables
    private var garageProgressDrawable: AnimatedVectorDrawableCompat? = null
    private val garageStatusColors = ArrayMap<GarageStatus, Int>()

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
            //TODO: Set options menu click listener

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

        smartGarageViewModel.status.observe(viewLifecycleOwner, Observer { newStatus ->
            updateStatusUI(newStatus)
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOpen -> SendGarageAction.run(ActionType.OPEN)
            R.id.btnClose -> SendGarageAction.run(ActionType.CLOSE)
            //TODO: Handle opening the side drawer
        }
    }

    private fun getStatusColor(status: GarageStatus): Int {
        garageStatusColors[status]?.let {
            return it
        }

        Log.e("SmartGarage", "Unable to get status color from ${status.name}!")
        return R.color.white
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