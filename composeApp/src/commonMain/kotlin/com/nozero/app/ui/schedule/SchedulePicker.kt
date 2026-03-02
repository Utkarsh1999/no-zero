package com.nozero.app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek

/**
 * 7-day weekday pill selector.
 */
@Composable
fun WeekdayPicker(
    selectedDays: Set<DayOfWeek>,
    onDaysChanged: (Set<DayOfWeek>) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { (dayOfWeek, label) ->
            val isSelected = dayOfWeek in selectedDays
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        val newDays = if (isSelected) {
                            selectedDays - dayOfWeek
                        } else {
                            selectedDays + dayOfWeek
                        }
                        onDaysChanged(newDays)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Simple time picker with hour and minute inputs.
 * Stores time as hour (0-23) and minute (0-59).
 */
@Composable
fun SimpleTimePicker(
    hour: Int,
    minute: Int,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var hourText by remember { mutableStateOf(hour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(minute.toString().padStart(2, '0')) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Hour input
        OutlinedTextField(
            value = hourText,
            onValueChange = { text ->
                val filtered = text.filter { it.isDigit() }.take(2)
                hourText = filtered
                val h = filtered.toIntOrNull() ?: 0
                if (h in 0..23) {
                    onTimeChanged(h, minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0)
                }
            },
            modifier = Modifier.width(72.dp),
            label = { Text("HH") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Minute input
        OutlinedTextField(
            value = minuteText,
            onValueChange = { text ->
                val filtered = text.filter { it.isDigit() }.take(2)
                minuteText = filtered
                val m = filtered.toIntOrNull() ?: 0
                if (m in 0..59) {
                    onTimeChanged(hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0, m)
                }
            },
            modifier = Modifier.width(72.dp),
            label = { Text("MM") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
