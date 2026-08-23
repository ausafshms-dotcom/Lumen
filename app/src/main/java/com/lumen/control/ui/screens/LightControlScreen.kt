package com.lumen.control.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.control.ir.IrTransmitter
import com.lumen.control.ir.LightCommand
import com.lumen.control.ui.theme.AccentEnd
import com.lumen.control.ui.theme.AccentStart
import com.lumen.control.ui.theme.Background
import com.lumen.control.ui.theme.Danger
import com.lumen.control.ui.theme.Surface as SurfaceColor
import com.lumen.control.ui.theme.SurfaceElevated
import com.lumen.control.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.roundToInt

private val modeButtons = listOf(
    LightCommand.FLASH,
    LightCommand.STROBE,
    LightCommand.FADE,
    LightCommand.SMOOTH
)

@Composable
fun LightControlScreen(irTransmitter: IrTransmitter) {
    val haptics = LocalHapticFeedback.current
    var selectedColor by remember { mutableStateOf<LightCommand?>(null) }
    var isOn by remember { mutableStateOf(true) }
    var brightness by remember { mutableFloatStateOf(70f) }
    var committedBrightness by remember { mutableFloatStateOf(70f) }
    var activeMode by remember { mutableStateOf<LightCommand?>(null) }

    fun fire(cmd: LightCommand) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        irTransmitter.send(cmd)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Room Lights", style = MaterialTheme.typography.headlineMedium)
                Text(
                    if (irTransmitter.hasIrBlaster) "Ready" else "No IR blaster found on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (irTransmitter.hasIrBlaster) AccentEnd else Danger
                )
            }

            PowerButton(
                isOn = isOn,
                onToggle = {
                    isOn = !isOn
                    fire(if (isOn) LightCommand.ON else LightCommand.OFF)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        ColorRing(
            selected = selectedColor,
            onColorSelected = {
                selectedColor = it
                activeMode = null
                fire(it)
            },
            onWhiteSelected = {
                selectedColor = LightCommand.WHITE
                activeMode = null
                fire(LightCommand.WHITE)
            },
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Brightness", style = MaterialTheme.typography.titleMedium)
                    Text("${brightness.roundToInt()}%", color = TextSecondary)
                }
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    onValueChangeFinished = {
                        val delta = brightness - committedBrightness
                        val steps = (abs(delta) / 6f).roundToInt().coerceIn(0, 15)
                        val direction = if (delta > 0) LightCommand.BRIGHTNESS_UP else LightCommand.BRIGHTNESS_DOWN
                        repeat(steps) { irTransmitter.send(direction) }
                        committedBrightness = brightness
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentStart,
                        activeTrackColor = AccentStart,
                        inactiveTrackColor = Color(0xFF2A2A38)
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Modes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
            modifier = Modifier.heightIn(max = 150.dp)
        ) {
            items(modeButtons) { mode ->
                ModeButton(
                    label = mode.label,
                    isActive = activeMode == mode,
                    onClick = {
                        activeMode = mode
                        selectedColor = null
                        fire(mode)
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PowerButton(isOn: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (isOn) AccentStart else SurfaceColor)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.PowerSettingsNew,
            contentDescription = "Power",
            tint = if (isOn) Color.Black else TextSecondary
        )
    }
}

@Composable
private fun ModeButton(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) AccentStart.copy(alpha = 0.22f) else SurfaceElevated)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) AccentEnd else Color.White
        )
    }
}
