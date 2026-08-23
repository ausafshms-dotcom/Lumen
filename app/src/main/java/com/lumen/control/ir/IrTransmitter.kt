package com.lumen.control.ir

import android.content.Context
import android.hardware.ConsumerIrManager

/**
 * Thin wrapper around the device's built-in IR blaster.
 * This is the single choke point all future "devices" (fan, AC, TV...) will
 * go through - just call send() with a different address/command pair.
 */
class IrTransmitter(context: Context) {

    private val manager: ConsumerIrManager? =
        context.applicationContext.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    val hasIrBlaster: Boolean
        get() = manager?.hasIrEmitter() == true

    fun send(command: LightCommand) {
        send(ADDRESS_RGB_CONTROLLER, command.command)
    }

    /** Generic escape hatch for future devices / custom codes. */
    fun send(address: Int, command: Int) {
        manager?.transmit(38000, NecCodes.pattern(address, command))
    }
}
