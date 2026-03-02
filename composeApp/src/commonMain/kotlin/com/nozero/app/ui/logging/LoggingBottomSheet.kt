package com.nozero.app.ui.logging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for logging time-based habits.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeLoggingSheet(
    habitTitle: String,
    targetMinutes: Int,
    currentValue: Double,
    onLog: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf(if (currentValue > 0) currentValue.toInt().toString() else "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = habitTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: $targetMinutes minutes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Input
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Minutes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick-pick buttons
            Text(
                text = "QUICK LOG",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 15, 30, 60, 120).forEach { mins ->
                    FilterChip(
                        selected = inputText == mins.toString(),
                        onClick = { inputText = mins.toString() },
                        label = { Text("${mins}m") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    val value = inputText.toDoubleOrNull() ?: 0.0
                    if (value > 0) onLog(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = (inputText.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Log Progress", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Bottom sheet for logging count-based habits.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountLoggingSheet(
    habitTitle: String,
    target: Int,
    unit: String,
    currentValue: Double,
    onLog: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf(if (currentValue > 0) currentValue.toInt().toString() else "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = habitTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: ${formatNumber(target)} $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Input
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(unit.replaceFirstChar { it.uppercase() }) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick pick (percentages of target)
            Text(
                text = "QUICK LOG",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.25, 0.5, 0.75, 1.0).forEach { fraction ->
                    val quickVal = (target * fraction).toInt()
                    FilterChip(
                        selected = inputText == quickVal.toString(),
                        onClick = { inputText = quickVal.toString() },
                        label = { Text(formatNumber(quickVal)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val value = inputText.toDoubleOrNull() ?: 0.0
                    if (value > 0) onLog(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = (inputText.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Log Progress", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatNumber(n: Int): String {
    return if (n >= 1000) {
        val k = n / 1000.0
        if (k == k.toInt().toDouble()) "${k.toInt()}K" else "${"%.1f".format(k)}K"
    } else {
        n.toString()
    }
}
