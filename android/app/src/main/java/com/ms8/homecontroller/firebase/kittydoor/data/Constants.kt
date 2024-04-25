package com.ms8.homecontroller.firebase.kittydoor.data

object Constants {
    const val DOOR_STATE = "door_state"
    const val SYSTEMS = "systems"
    const val TYPE = "type"
    const val LIGHT_LEVEL_VAL = "level"
    const val STATUS = "status"
    const val KITTY_DOOR = "kitty_door"
    const val HW_OVERRIDE = "kitty_door_hw_override"
    const val LIGHT_LEVEL = "kitty_door_light_level"
    const val OPT_CLOSE_LIGHT_LVL = "closeLightLevel"
    const val COMMAND = "command"
    const val OPT_DELAY_CLOSING = "delayClosing"
    const val OPT_DELAY_CLOSING_VAL = "delayClosingVal"
    const val OPT_DELAY_OPENING = "delayOpening"
    const val OPT_DELAY_OPENING_VAL = "delayOpeningVal"
    const val OPT_OPEN_LIGHT_LVL = "openLightLevel"
    const val OVERRIDE_AUTO = "overrideAuto"
    const val TIMESTAMP = "timestamp"

    const val CMD_OPEN = "openKittyDoor"
    const val CMD_CLOSE = "closeKittyDoor"
    const val CMD_LIGHT_LVL = "readLightLevel"
    const val CMD_NON = "_none_"

    const val HW_OVERRIDE_DISABLED = 0
    const val HW_OVERRIDE_ENABLED = 1

    const val FLOW_TAG = "FLOW_KittyDoor"
}