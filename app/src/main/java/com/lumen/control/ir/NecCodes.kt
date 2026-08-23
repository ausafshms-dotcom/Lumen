package com.lumen.control.ir

/**
 * Encodes NEC-protocol IR frames.
 *
 * All 24 buttons on this remote share the same 16-bit address (0x00F7).
 * Each button is just a different 8-bit command byte, sent alongside its
 * bitwise complement (standard NEC "extended" framing: addrLow, addrHigh,
 * command, ~command).
 *
 * To support a different IR device in the future, just add a new entry to
 * DeviceProfile with its own address, and reuse pattern().
 */
object NecCodes {

    /** Builds the raw on/off microsecond pulse train ConsumerIrManager.transmit() expects. */
    fun pattern(address: Int, command: Int): IntArray {
        val addrLow = address and 0xFF
        val addrHigh = (address shr 8) and 0xFF
        val cmd = command and 0xFF
        val cmdInv = cmd.inv() and 0xFF
        val bytes = intArrayOf(addrLow, addrHigh, cmd, cmdInv)

        val pulses = mutableListOf<Int>()
        // Leader burst
        pulses.add(9000)
        pulses.add(4500)

        for (b in bytes) {
            for (i in 0 until 8) { // LSB first
                val bit = (b shr i) and 1
                pulses.add(562)                     // mark
                pulses.add(if (bit == 1) 1687 else 562) // space
            }
        }
        // Trailing mark to close the frame
        pulses.add(562)

        return pulses.toIntArray()
    }
}

/** The RGB strip controller this app was built for. */
const val ADDRESS_RGB_CONTROLLER = 0x00F7

enum class LightCommand(val label: String, val command: Int) {
    ON("On", 0xC0),
    OFF("Off", 0x40),
    BRIGHTNESS_UP("Brightness +", 0x00),
    BRIGHTNESS_DOWN("Brightness -", 0x80),
    RED("Red", 0x20),
    GREEN("Green", 0xA0),
    BLUE("Blue", 0x60),
    WHITE("White", 0xE0),
    LIGHT_RED("Light Red", 0x10),
    LIGHT_GREEN("Light Green", 0x90),
    LIGHT_BLUE("Light Blue", 0x50),
    FLASH("Flash", 0xD0),
    ORANGE("Orange", 0x30),
    CYAN("Cyan", 0xB0),
    PURPLE("Purple", 0x70),
    STROBE("Strobe", 0xF0),
    LIGHT_ORANGE("Light Orange", 0x08),
    TEAL("Teal", 0x88),
    DARK_PURPLE("Dark Purple", 0x48),
    FADE("Fade", 0xC8),
    YELLOW("Yellow", 0x28),
    LIME_GREEN("Lime Green", 0xA8),
    PINK("Pink", 0x68),
    SMOOTH("Smooth", 0xE8)
}
