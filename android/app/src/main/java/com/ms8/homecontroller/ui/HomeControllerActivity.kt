package com.ms8.homecontroller.ui

import com.mikepenz.materialdrawer.Drawer
import com.ms8.flashbar.Flashbar

interface HomeControllerActivity {
    fun showFlashbar(newFlashbar: Flashbar.Builder)

    fun hideFlashbar()

    fun setOptionsDrawer(newDrawer: Drawer)

    fun openDrawer()
}