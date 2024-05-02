package com.ms8.homecontroller.ui

import com.ms8.flashbar.Flashbar

interface FlashbarActivity {
    fun showFlashbar(newFlashbar: Flashbar.Builder)

    fun hideFlashbar()
}