package com.nozero.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nozero.app.viewmodel.DayCell
import com.nozero.app.viewmodel.HabitDetailUiState
import com.nozero.app.viewmodel.HabitDetailViewModel
import com.nozero.app.viewmodel.MilestoneUiModel
import com.nozero.shared.domain.model.CompletionStatus
import com.nozero.shared.domain.model.ConsistencyMetrics

@Composable
fun HabitDetailScreen(
    viewModel: HabitDetailViewModel,
    habitId: String,
    onBack: () -> Unit,
    onArchived: () -> Unit
) {
    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    val state by viewModel.uiState.collectAsState()

    when (val current = state) {
        is HabitDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        }
        is HabitDetailUiState.Error -> {
            Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(current.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is HabitDetailUiState.Success -> {
            HabitDetailContent(
                state = current,
                onBack = onBack,
                onArchive = {
                    viewModel.archiveHabit(current.habit.id)
                    onArchived()
                }
            )
        }
    }
}

@Composable
private fun HabitDetailContent(
    state: HabitDetailUiState.Success,
    onBack: () -> Unit,
    onArchive: () -> Unit
) {
    var showArchiveDialog by remember { mutableStateOf(false) }

    // Archive confirmation dialog
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Archive \"${state.habit.title}\"?") },
            text = { Text("This will remove it from your Today view. Your history and streaks will be preserved.") },
            confirmButton = {
                TextButton(onClick = {
                    showArchiveDialog = false
                    onArchive()
                }) {
                    Text("Archive", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Back button
        TextButton(onClick = onBack) {
            Text("← Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title & type badge
        val badge = if (state.habit.type == com.nozero.shared.domain.model.HabitType.BAD) "AVOID" else "BUILD"
        Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = if (state.habit.type == com.nozero.shared.domain.model.HabitType.BAD)
                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = state.habit.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.motivationMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Schedule display
        Spacer(modifier = Modifier.height(12.dp))
        val scheduleText = when (state.habit.frequency) {
            is com.nozero.shared.domain.model.HabitFrequency.Daily -> "Every Day"
            is com.nozero.shared.domain.model.HabitFrequency.TimesPerWeek -> {
                val times = (state.habit.frequency as com.nozero.shared.domain.model.HabitFrequency.TimesPerWeek).count
                "${times}× per week"
            }
            is com.nozero.shared.domain.model.HabitFrequency.Scheduled -> {
                val days = (state.habit.frequency as com.nozero.shared.domain.model.HabitFrequency.Scheduled).days
                days.sortedBy { it.ordinal }.joinToString(", ") { it.name.take(3).lowercase().replaceFirstChar { c -> c.uppercase() } }
            }
        }
        val timeText = state.habit.reminderTime?.let { " · ${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: ""
        Text(
            text = "📅 $scheduleText$timeText",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Metrics Cards
        MetricsRow(state.metrics)

        Spacer(modifier = Modifier.height(16.dp))
        
        GraceDaysCard(earnedGraceDays = state.habit.earnedGraceDays)

        Spacer(modifier = Modifier.height(32.dp))

        // Calendar Grid
        SectionLabel("CONSISTENCY GRID")
        Spacer(modifier = Modifier.height(8.dp))
        DayLabels()
        Spacer(modifier = Modifier.height(4.dp))
        CalendarGrid(days = state.calendarDays)

        Spacer(modifier = Modifier.height(32.dp))

        // Identity Path
        SectionLabel("IDENTITY PATH")
        Spacer(modifier = Modifier.height(12.dp))
        state.milestones.forEach { milestone ->
            MilestoneRow(milestone)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Archive button — now shows confirmation dialog
        OutlinedButton(
            onClick = { showArchiveDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Archive Habit")
        }
    }
}

@Composable
private fun MetricsRow(metrics: ConsistencyMetrics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            label = "CURRENT",
            value = "${metrics.currentStreak}d",
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "BEST",
            value = "${metrics.bestStreak}d",
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "RECOVERIES",
            value = "${metrics.recoveryCount}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GraceDaysCard(earnedGraceDays: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Grace Days: $earnedGraceDays/3",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Earned every 14 days of consistency. Saves your streak from accidental misses.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DayLabels() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        days.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(days: List<DayCell>) {
    val rows = days.chunked(7)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    DayCellView(day, modifier = Modifier.weight(1f))
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DayCellView(day: DayCell, modifier: Modifier = Modifier) {
    val color = when {
        day.isFuture -> Color.Transparent
        day.status == CompletionStatus.COMPLETED -> Color(0xFF34D399)
        day.status == CompletionStatus.MISSED || day.status == CompletionStatus.RELAPSED -> Color(0xFFEF4444).copy(alpha = 0.6f)
        day.status == CompletionStatus.EXEMPTED -> Color(0xFF6366F1).copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (day.isToday) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun MilestoneRow(milestone: MilestoneUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (milestone.isAchieved) Color(0xFF34D399).copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (milestone.isAchieved) "✓" else "○",
                color = if (milestone.isAchieved) Color(0xFF34D399)
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (milestone.isAchieved)
                    MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
