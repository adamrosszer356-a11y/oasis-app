// ui/screens/DevicesScreen.kt
package com.example.oasisapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.oasisapp.network.AddDeviceRequest
import com.example.oasisapp.network.RetrofitClient
import com.example.oasisapp.network.WaterPlantRequest
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<Device?>(null) }

    // TODO: A felhasználó ID-t dinamikusan kellene kezelni
    val userId = 1

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            devices = RetrofitClient.api.getDevices(userId = userId.toString())
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Hiba a kapcsolódáskor: ${e.message}"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    if (showAddDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, plantName ->
                scope.launch {
                    try {
                        val response = RetrofitClient.api.addDevice(
                            AddDeviceRequest(
                                user_id = userId,
                                name = name,
                                plant_name = plantName
                            )
                        )
                        if (response.success && response.device != null) {
                            devices = devices + response.device
                            Toast.makeText(context, "Eszköz hozzáadva!", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                        } else {
                            Toast.makeText(context, "Hiba: ${response.message}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Hiba: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    if (selectedDevice != null) {
        DeviceControlDialog(
            device = selectedDevice!!,
            onDismiss = { selectedDevice = null },
            onWater = { deviceId, amount ->
                scope.launch {
                    try {
                        val response = RetrofitClient.api.waterPlant(
                            WaterPlantRequest(device_id = deviceId, amount = amount)
                        )
                        if (response.success) {
                            Toast.makeText(context, "Öntözés elindítva!", Toast.LENGTH_SHORT).show()
                            // Itt frissíthetjük az eszköz állapotát, ha szükséges
                            selectedDevice = null 
                        } else {
                            Toast.makeText(context, "Hiba: ${response.message}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Hálózati hiba: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
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
                onClick = { showAddDialog = true }
            ) {
                Text("+ Eszköz")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nem sikerült betölteni az adatokat.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { 
                         scope.launch {
                             try {
                                 isLoading = true
                                 devices = RetrofitClient.api.getDevices(userId = userId.toString())
                                 errorMessage = null
                             } catch(e: Exception) {
                                 errorMessage = e.message
                             } finally {
                                 isLoading = false
                             }
                         }
                    }) {
                        Text("Újrapróbálás")
                    }
                }
            }
        } else if (devices.isEmpty()) {
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
                DeviceCard(
                    device = device,
                    onClick = { selectedDevice = device }
                )
            }
        }
    }
}

@Composable
fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var plantName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Új eszköz hozzáadása") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Eszköz neve") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = plantName,
                    onValueChange = { plantName = it },
                    label = { Text("Növény fajtája") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && plantName.isNotBlank()) {
                        onAdd(name, plantName)
                    }
                }
            ) {
                Text("Hozzáadás")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun DeviceControlDialog(
    device: Device,
    onDismiss: () -> Unit,
    onWater: (String, Int) -> Unit
) {
    var waterAmount by remember { mutableStateOf(100f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(device.name) },
        text = {
            Column {
                Text("Növény: ${device.plantName}")
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Öntözés mennyisége: ${waterAmount.toInt()} ml")
                Slider(
                    value = waterAmount,
                    onValueChange = { waterAmount = it },
                    valueRange = 50f..500f,
                    steps = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onWater(device.id, waterAmount.toInt()) }
            ) {
                Text("Öntözés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bezárás")
            }
        }
    )
}

@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
