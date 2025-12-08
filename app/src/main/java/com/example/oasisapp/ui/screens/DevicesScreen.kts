// ui/screens/DevicesScreen.kt
package com.example.oasisapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class Device(
    val id: String,
    val name: String,
    val plantName: String,
    val status: String,
    val moisture: Int,
    val light: Int,
    val temp: Double,
    val battery: Int
)

@Composable
fun DevicesScreen(paddingValues: PaddingValues) {
    var devices by remember {
        mutableStateOf(
            listOf(
                Device(
                    id = "oasis-001",
                    name = "Living Room Oasis",
                    plantName = "Monstera Deliciosa",
                    status = "online",
                    moisture = 65,
                    light = 42,
                    temp = 23.5,
                    battery = 78
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Eszközeid",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedButton(
                onClick = {
                    // demo: új eszköz hozzáadása
                    devices = devices + Device(
                        id = "oasis-${devices.size + 1}",
                        name = "New Oasis ${devices.size + 1}",
                        plantName = "Pothos",
                        status = "online",
                        moisture = 45,
                        light = 78,
                        temp = 22.1,
                        battery = 92
                    )
                }
            ) {
                Text("+ Eszköz")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (devices.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Még nincs csatlakoztatott Oasis eszköz.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Kezdd az első okos cseréped párosításával!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            devices.forEach { device ->
                Spacer(modifier = Modifier.height(10.dp))
                DeviceCard(device = device)
            }
        }
    }
}

@Composable
private fun DeviceCard(device: Device) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: open plant detail */ },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(device.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        device.plantName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                val statusColor = when (device.status) {
                    "online" -> Color(0xFF66BB6A)
                    "offline" -> Color(0xFFE53935)
                    else -> Color(0xFFFFB300)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = device.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SensorItem(label = "Nedvesség", value = "${device.moisture}%")
                SensorItem(label = "Fény", value = "${device.light}%")
                SensorItem(label = "Hőmérséklet", value = "${device.temp}°C")
                SensorItem(label = "Akkumulátor", value = "${device.battery}%")
            }
        }
    }
}

@Composable
private fun SensorItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
