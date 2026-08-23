package com.lumen.control.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.control.ir.IrTransmitter
import com.lumen.control.ui.theme.AccentStart
import com.lumen.control.ui.theme.Background
import com.lumen.control.ui.theme.SurfaceElevated
import com.lumen.control.ui.theme.TextSecondary

/**
 * Placeholder for future devices (fan, AC, TV...). Each future device just
 * needs its own IR address + command map, reusing IrTransmitter.send().
 */
@Composable
fun DevicesScreen() {
    EmptyState(
        title = "Devices",
        subtitle = "Add more IR-controlled devices here in the future — fans, AC units, TVs. This app isn't limited to lights."
    )
}

/**
 * Settings screen doubles as the extensibility hook: a raw NEC command
 * sender for testing new codes without changing any code.
 */
@Composable
fun SettingsScreen(irTransmitter: IrTransmitter) {
    var addressHex by remember { mutableStateOf("00F7") }
    var commandHex by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Advanced: send a raw NEC code", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Useful for testing a new remote before wiring it into the app.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = addressHex,
                    onValueChange = { addressHex = it },
                    label = { Text("Address (hex)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = commandHex,
                    onValueChange = { commandHex = it },
                    label = { Text("Command (hex)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        val addr = addressHex.toIntOrNull(16)
                        val cmd = commandHex.toIntOrNull(16)
                        if (addr != null && cmd != null) {
                            irTransmitter.send(addr, cmd)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentStart),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Transmit", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            if (irTransmitter.hasIrBlaster) "IR blaster detected on this device." else "No IR blaster detected.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(40.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
