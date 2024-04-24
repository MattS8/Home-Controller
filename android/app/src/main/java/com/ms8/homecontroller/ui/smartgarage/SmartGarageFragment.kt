package com.ms8.homecontroller.ui.smartgarage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.FragmentSmartGarageBinding

class SmartGarageFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSmartGarageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var garageProgressDrawable: AnimatedVectorDrawableCompat? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val smartGarageViewModel =
            ViewModelProvider(this).get(SmartGarageViewModel::class.java)

        _binding = FragmentSmartGarageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding?.let {
            it.btnOpen.setOnClickListener(this)
            it.btnClose.setOnClickListener(this)
            //TODO: Set options menu click listener

            garageProgressDrawable =
                context?.let { ctx -> AnimatedVectorDrawableCompat.create(ctx, R.drawable.av_progress) }
            garageProgressDrawable?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    binding.progGarageStatus.post { garageProgressDrawable?.start() }
                }
            })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}