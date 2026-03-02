package com.nozero.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nozero.app.ui.schedule.SimpleTimePicker
import com.nozero.shared.domain.model.HabitTemplate
import com.nozero.shared.domain.model.HabitTemplates
import com.nozero.shared.domain.model.HabitType
import kotlinx.datetime.LocalTime

@Composable
fun TemplateSelectionScreen(
    onTemplatesSelected: (List<Pair<HabitTemplate, LocalTime?>>) -> Unit,
    onCreateCustom: () -> Unit
) {
    val selectedTemplates = remember { mutableStateMapOf<HabitTemplate, LocalTime?>() }
    var showGood by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Header
        Text(
            text = "Choose Your Habits",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick templates to get started, or create your own.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tab Row: BUILD / AVOID
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabButton(
                text = "BUILD",
                isSelected = showGood,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { showGood = true },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "AVOID",
                isSelected = !showGood,
                color = MaterialTheme.colorScheme.error,
                onClick = { showGood = false },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Template List
        val templates = if (showGood) HabitTemplates.goodHabits else HabitTemplates.badHabits
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(templates) { template ->
                val isSelected = selectedTemplates.containsKey(template)
                val selectedTime = selectedTemplates[template]
                TemplateCard(
                    template = template,
                    isSelected = isSelected,
                    selectedTime = selectedTime,
                    onToggle = {
                        if (isSelected) {
                            selectedTemplates.remove(template)
                        } else {
                            selectedTemplates[template] = template.suggestedReminderTime
                        }
                    },
                    onTimeChanged = { newTime ->
                        selectedTemplates[template] = newTime
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Button(
            onClick = { onTemplatesSelected(selectedTemplates.toList()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedTemplates.isNotEmpty(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Add ${selectedTemplates.size} Habit${if (selectedTemplates.size != 1) "s" else ""}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        TextButton(
            onClick = onCreateCustom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Create Custom Habit",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun TemplateCard(
    template: HabitTemplate,
    isSelected: Boolean,
    selectedTime: LocalTime?,
    onToggle: () -> Unit,
    onTimeChanged: (LocalTime?) -> Unit
) {
    val borderColor = if (isSelected)
        if (template.type == HabitType.GOOD) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.outline

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) borderColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true) else null
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) borderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Text(
                            text = "✓",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            
            // Expanded reminder section
            if (isSelected) {
                Divider(color = borderColor.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (selectedTime != null) onTimeChanged(null)
                            else onTimeChanged(LocalTime(9, 0))
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set a daily reminder",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = selectedTime != null,
                            onCheckedChange = { checked ->
                                if (checked) onTimeChanged(LocalTime(9, 0))
                                else onTimeChanged(null)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                    
                    if (selectedTime != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SimpleTimePicker(
                            hour = selectedTime.hour,
                            minute = selectedTime.minute,
                            onTimeChanged = { h, m -> onTimeChanged(LocalTime(h, m)) }
                        )
                    }
                }
            }
        }
    }
}
