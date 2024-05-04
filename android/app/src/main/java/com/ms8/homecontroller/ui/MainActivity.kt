package com.ms8.homecontroller.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.ms8.flashbar.Flashbar
import com.ms8.homecontroller.R
import com.ms8.homecontroller.databinding.ActivityMainBinding
import com.ms8.homecontroller.ui.kittydoor.KittyDoorViewModel
import com.ms8.homecontroller.ui.smartgarage.SmartGarageViewModel

class MainActivity : AppCompatActivity(), HomeControllerActivity {

    private lateinit var binding: ActivityMainBinding

    private var flashbar: Flashbar? = null
    private var drawer: Drawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, _, _ -> flashbar?.dismiss() }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_kitty_door, R.id.navigation_dashboard, R.id.navigation_smart_garage
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        onBackPressedDispatcher.addCallback(onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                Log.i("TESTING", "BEEP BOOP")
                flashbar?.dismiss()
                drawer?.closeDrawer()
            }
    })

        //        This call preloads  the viewModels before paging to them
        val kittyDoorViewModel =
            ViewModelProvider(this)[KittyDoorViewModel::class.java]
        val smartGarageViewModel =
            ViewModelProvider(this)[SmartGarageViewModel::class.java]
    }

    override fun showFlashbar(newFlashbar: Flashbar.Builder) {
        Log.i("##TEST", "Showing flashbar!")
        flashbar?.dismiss()
        val onDismiss = object : Flashbar.OnActionTapListener {
            override fun onActionTapped(bar: Flashbar) {
                bar.dismiss()
                flashbar = null
            }

        }
        val onDismissListener = object :Flashbar.OnBarDismissListener {
            override fun onDismissProgress(bar: Flashbar, progress: Float) {}

            override fun onDismissed(bar: Flashbar, event: Flashbar.DismissEvent) {
                flashbar = null
            }

            override fun onDismissing(bar: Flashbar, isSwiped: Boolean) {}

        }
        newFlashbar.positiveActionTapListener(onDismiss)
        newFlashbar.barDismissListener(onDismissListener)

        flashbar = newFlashbar.build()
        flashbar?.show()
    }

    override fun hideFlashbar() {
        flashbar?.dismiss()
    }

    override fun setOptionsDrawer(newDrawer: Drawer) {
        drawer?.closeDrawer()
        drawer = newDrawer
    }

    override fun openDrawer() {
        drawer?.openDrawer()
    }
}