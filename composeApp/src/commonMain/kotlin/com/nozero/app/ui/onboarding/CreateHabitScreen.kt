package com.nozero.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nozero.app.ui.schedule.SimpleTimePicker
import com.nozero.app.ui.schedule.WeekdayPicker
import com.nozero.shared.domain.model.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Composable
fun CreateHabitScreen(
    onHabitCreated: (
        title: String,
        description: String?,
        type: HabitType,
        frequency: HabitFrequency,
        trackingType: TrackingType,
        reinforcementStyle: ReinforcementStyle,
        reminderTime: LocalTime?,
        allowBackdateLogging: Boolean
    ) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf(HabitType.GOOD) }
    var selectedTracking by remember { mutableStateOf<TrackingType>(TrackingType.Binary) }
    var selectedStyle by remember { mutableStateOf(ReinforcementStyle.NEUTRAL) }
    var allowBackdateLogging by remember { mutableStateOf(true) }

    // Schedule state
    var frequencyMode by remember { mutableStateOf("daily") } // daily, specific, times
    var selectedDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var timesPerWeek by remember { mutableIntStateOf(3) }

    // Time slot state
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    // Target inputs for Time/Count
    var targetMinutes by remember { mutableStateOf("30") }
    var targetCount by remember { mutableStateOf("1") }
    var targetUnit by remember { mutableStateOf("times") }

    val selectedFrequency: HabitFrequency = when (frequencyMode) {
        "specific" -> HabitFrequency.Scheduled(selectedDays)
        "times" -> HabitFrequency.TimesPerWeek(timesPerWeek)
        else -> HabitFrequency.Daily
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Create Habit",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ── Habit Name ───────────────────────────────────
        SectionLabel("HABIT NAME")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; titleError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., Morning Run") },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Description ──────────────────────────────────
        SectionLabel("DESCRIPTION (OPTIONAL)")
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("What does this habit mean to you?") },
            singleLine = false,
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Type ─────────────────────────────────────────
        SectionLabel("TYPE")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChipOption("BUILD", selectedType == HabitType.GOOD, onClick = { selectedType = HabitType.GOOD }, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.tertiary)
            ChipOption("AVOID", selectedType == HabitType.BAD, onClick = { selectedType = HabitType.BAD }, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Schedule ─────────────────────────────────────
        SectionLabel("SCHEDULE")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipOption("Every Day", frequencyMode == "daily", onClick = { frequencyMode = "daily" }, modifier = Modifier.fillMaxWidth())
            ChipOption("Specific Days", frequencyMode == "specific", onClick = { frequencyMode = "specific" }, modifier = Modifier.fillMaxWidth())
            ChipOption("X Times per Week", frequencyMode == "times", onClick = { frequencyMode = "times" }, modifier = Modifier.fillMaxWidth())
        }

        // Day picker (visible when "Specific Days" selected)
        if (frequencyMode == "specific") {
            Spacer(modifier = Modifier.height(12.dp))
            WeekdayPicker(
                selectedDays = selectedDays,
                onDaysChanged = { selectedDays = it }
            )
        }

        // Times per week stepper
        if (frequencyMode == "times") {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { if (timesPerWeek > 1) timesPerWeek-- }
                ) {
                    Text("−", modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), style = MaterialTheme.typography.titleLarge)
                }
                Text(
                    text = "$timesPerWeek×",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { if (timesPerWeek < 7) timesPerWeek++ }
                ) {
                    Text("+", modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Preferred Time (optional) ────────────────────
        SectionLabel("PREFERRED TIME (OPTIONAL)")
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(
                checked = showTimePicker,
                onCheckedChange = { showTimePicker = it }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (showTimePicker) "Set a preferred time" else "No time set",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (showTimePicker) {
            Spacer(modifier = Modifier.height(8.dp))
            SimpleTimePicker(
                hour = selectedHour,
                minute = selectedMinute,
                onTimeChanged = { h, m -> selectedHour = h; selectedMinute = m }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Tracking ─────────────────────────────────────
        if (selectedType == HabitType.GOOD) {
            SectionLabel("TRACKING")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipOption("Yes / No", selectedTracking is TrackingType.Binary, onClick = { selectedTracking = TrackingType.Binary }, modifier = Modifier.fillMaxWidth())
                ChipOption("Timed (Minutes)", selectedTracking is TrackingType.Time, onClick = { selectedTracking = TrackingType.Time(targetMinutes.toIntOrNull() ?: 30) }, modifier = Modifier.fillMaxWidth())
                ChipOption("Count", selectedTracking is TrackingType.Count, onClick = { selectedTracking = TrackingType.Count(targetCount.toIntOrNull() ?: 1, targetUnit) }, modifier = Modifier.fillMaxWidth())
            }

            // Target input for Time
            if (selectedTracking is TrackingType.Time) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = targetMinutes,
                    onValueChange = { targetMinutes = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Target Minutes") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
            }

            // Target input for Count
            if (selectedTracking is TrackingType.Count) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = targetCount,
                        onValueChange = { targetCount = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.weight(1f),
                        label = { Text("Target") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    OutlinedTextField(
                        value = targetUnit,
                        onValueChange = { targetUnit = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Unit") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            selectedTracking = TrackingType.Avoidance
        }

        // ── Motivation Tone ──────────────────────────────
        SectionLabel("MOTIVATION TONE")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipOption("Soft", selectedStyle == ReinforcementStyle.SOFT, onClick = { selectedStyle = ReinforcementStyle.SOFT }, modifier = Modifier.weight(1f))
            ChipOption("Neutral", selectedStyle == ReinforcementStyle.NEUTRAL, onClick = { selectedStyle = ReinforcementStyle.NEUTRAL }, modifier = Modifier.weight(1f))
            ChipOption("Bold", selectedStyle == ReinforcementStyle.AGGRESSIVE, onClick = { selectedStyle = ReinforcementStyle.AGGRESSIVE }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Accountability ───────────────────────────────
        SectionLabel("ACCOUNTABILITY")
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(
                checked = allowBackdateLogging,
                onCheckedChange = { allowBackdateLogging = it }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Allow Backdated Logging",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "If disabled, you cannot check off missed days.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ── Create Button ────────────────────────────────
        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = "Habit name is required"
                } else if (frequencyMode == "specific" && selectedDays.isEmpty()) {
                    titleError = "Select at least one day"
                } else {
                    // Resolve final tracking type with target values
                    val finalTracking = when (selectedTracking) {
                        is TrackingType.Time -> TrackingType.Time(targetMinutes.toIntOrNull() ?: 30)
                        is TrackingType.Count -> TrackingType.Count(targetCount.toIntOrNull() ?: 1, targetUnit.ifBlank { "times" })
                        else -> selectedTracking
                    }
                    val reminderTime = if (showTimePicker) LocalTime(selectedHour, selectedMinute) else null
                    onHabitCreated(
                        title.trim(),
                        description.trim().ifBlank { null },
                        selectedType,
                        selectedFrequency,
                        finalTracking,
                        selectedStyle,
                        reminderTime,
                        allowBackdateLogging
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Text("Create Habit", style = MaterialTheme.typography.titleMedium)
        }

        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
)

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ChipOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp)
        )
    }
}
