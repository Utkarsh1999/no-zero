package com.nozero.app.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nozero.app.ui.logging.CountLoggingSheet
import com.nozero.app.ui.logging.TimeLoggingSheet
import com.nozero.app.ui.tutorial.TutorialOverlay
import com.nozero.app.ui.tutorial.tutorialSteps
import com.nozero.app.viewmodel.HabitItemUiModel
import com.nozero.app.viewmodel.TodayUiState
import com.nozero.app.viewmodel.TodayViewModel
import com.nozero.shared.domain.model.HabitType
import com.nozero.shared.domain.model.TrackingType

@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onHabitClick: (String) -> Unit = {},
    onAddHabit: () -> Unit = {},
    showTutorial: Boolean = false,
    onTutorialComplete: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Tutorial step state
    var tutorialStep by remember { mutableIntStateOf(0) }

    // Bottom sheet state
    var sheetHabit by remember { mutableStateOf<HabitItemUiModel?>(null) }

    // Show bottom sheet for Time/Count
    sheetHabit?.let { habit ->
        when (val tracking = habit.trackingType) {
            is TrackingType.Time -> {
                TimeLoggingSheet(
                    habitTitle = habit.title,
                    targetMinutes = tracking.targetMinutes,
                    currentValue = habit.loggedValue,
                    onLog = { value ->
                        viewModel.onLogValue(habit.id, value)
                        sheetHabit = null
                    },
                    onDismiss = { sheetHabit = null }
                )
            }
            is TrackingType.Count -> {
                CountLoggingSheet(
                    habitTitle = habit.title,
                    target = tracking.target,
                    unit = tracking.unit,
                    currentValue = habit.loggedValue,
                    onLog = { value ->
                        viewModel.onLogValue(habit.id, value)
                        sheetHabit = null
                    },
                    onDismiss = { sheetHabit = null }
                )
            }
            else -> { sheetHabit = null }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                if (state is TodayUiState.Success && (state as TodayUiState.Success).habits.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = onAddHabit,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Text("+", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (val current = state) {
                    is TodayUiState.Loading -> LoadingView()
                    is TodayUiState.Error -> ErrorView(current.message)
                    is TodayUiState.Success -> {
                        if (current.habits.isEmpty()) {
                            EmptyStateView(onAddHabit = onAddHabit)
                        } else {
                            TodayContent(
                                state = current,
                                onBinaryToggle = viewModel::onToggleCompletion,
                                onTimedTap = { habit -> sheetHabit = habit },
                                onCountTap = { habit -> sheetHabit = habit },
                                onAvoidanceLog = { habitId, stayed ->
                                    viewModel.onLogAvoidance(habitId, stayed)
                                },
                                onHabitClick = onHabitClick
                            )
                        }
                    }
                }
            }
        }

        // Tutorial overlay — shown on top of everything
        if (showTutorial && state is TodayUiState.Success &&
            (state as TodayUiState.Success).habits.isNotEmpty()
        ) {
            TutorialOverlay(
                currentStep = tutorialStep,
                onNext = {
                    if (tutorialStep < tutorialSteps.lastIndex) {
                        tutorialStep++
                    } else {
                        onTutorialComplete()
                    }
                },
                onSkip = { onTutorialComplete() }
            )
        }
    }
}

@Composable
private fun TodayContent(
    state: TodayUiState.Success,
    onBinaryToggle: (String, Boolean) -> Unit,
    onTimedTap: (HabitItemUiModel) -> Unit,
    onCountTap: (HabitItemUiModel) -> Unit,
    onAvoidanceLog: (String, Boolean) -> Unit,
    onHabitClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        item {
            Text(
                text = state.dateLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Text(
                text = state.dailyMotivation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }

        items(state.habits, key = { it.id }) { habit ->
            when (habit.trackingType) {
                is TrackingType.Binary -> BinaryHabitCard(
                    habit = habit,
                    onToggle = { onBinaryToggle(habit.id, habit.isCompletedToday) },
                    onCardClick = { onHabitClick(habit.id) }
                )
                is TrackingType.Time -> TimedHabitCard(
                    habit = habit,
                    target = (habit.trackingType as TrackingType.Time).targetMinutes,
                    onLogTap = { onTimedTap(habit) },
                    onCardClick = { onHabitClick(habit.id) }
                )
                is TrackingType.Count -> CountHabitCard(
                    habit = habit,
                    target = (habit.trackingType as TrackingType.Count).target,
                    unit = (habit.trackingType as TrackingType.Count).unit,
                    onLogTap = { onCountTap(habit) },
                    onCardClick = { onHabitClick(habit.id) }
                )
                is TrackingType.Avoidance -> AvoidanceHabitCard(
                    habit = habit,
                    onClean = { onAvoidanceLog(habit.id, true) },
                    onSlipped = { onAvoidanceLog(habit.id, false) },
                    onCardClick = { onHabitClick(habit.id) }
                )
            }
        }
    }
}

// ── Binary Card ──────────────────────────────────────────
@Composable
private fun BinaryHabitCard(
    habit: HabitItemUiModel,
    onToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    val checkColor by animateColorAsState(
        targetValue = if (habit.isCompletedToday) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.outline,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "checkColor"
    )

    HabitCardShell(habit = habit, onCardClick = onCardClick) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(checkColor.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (habit.isCompletedToday) "✓" else "○",
                style = MaterialTheme.typography.titleLarge,
                color = checkColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Timed Card ───────────────────────────────────────────
@Composable
private fun TimedHabitCard(
    habit: HabitItemUiModel,
    target: Int,
    onLogTap: () -> Unit,
    onCardClick: () -> Unit
) {
    HabitCardShell(
        habit = habit,
        onCardClick = onCardClick,
        subtitleOverride = if (habit.loggedValue > 0)
            "${habit.loggedValue.toInt()} / $target min"
        else null
    ) {
        Surface(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onLogTap() },
            shape = RoundedCornerShape(12.dp),
            color = if (habit.isCompletedToday) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ) {
            Text(
                text = if (habit.loggedValue > 0) "${habit.loggedValue.toInt()}m" else "Log",
                style = MaterialTheme.typography.labelLarge,
                color = if (habit.isCompletedToday) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

// ── Count Card ───────────────────────────────────────────
@Composable
private fun CountHabitCard(
    habit: HabitItemUiModel,
    target: Int,
    unit: String,
    onLogTap: () -> Unit,
    onCardClick: () -> Unit
) {
    HabitCardShell(
        habit = habit,
        onCardClick = onCardClick,
        subtitleOverride = if (habit.loggedValue > 0)
            "${formatNumber(habit.loggedValue.toInt())} / ${formatNumber(target)} $unit"
        else null
    ) {
        Surface(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onLogTap() },
            shape = RoundedCornerShape(12.dp),
            color = if (habit.isCompletedToday) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ) {
            Text(
                text = if (habit.loggedValue > 0) formatNumber(habit.loggedValue.toInt()) else "Log",
                style = MaterialTheme.typography.labelLarge,
                color = if (habit.isCompletedToday) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

// ── Avoidance Card ───────────────────────────────────────
@Composable
private fun AvoidanceHabitCard(
    habit: HabitItemUiModel,
    onClean: () -> Unit,
    onSlipped: () -> Unit,
    onCardClick: () -> Unit
) {
    HabitCardShell(habit = habit, onCardClick = onCardClick) {
        if (habit.isCompletedToday && habit.loggedValue == 1.0) {
            // Already marked clean
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Clean ✓",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        } else if (habit.isCompletedToday && habit.loggedValue == 0.0) {
            // Relapsed
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Slipped",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        } else {
            // Not logged yet — show two buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClean() },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Clean",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
                Surface(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSlipped() },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Slipped",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

// ── Shared Card Shell ────────────────────────────────────
@Composable
private fun HabitCardShell(
    habit: HabitItemUiModel,
    onCardClick: () -> Unit,
    subtitleOverride: String? = null,
    trailingContent: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val badge = if (habit.type == HabitType.BAD) "AVOID" else "BUILD"
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (habit.type == HabitType.BAD)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                val subtitle = subtitleOverride ?: if (habit.currentStreak > 0)
                    "${habit.currentStreak} day streak"
                else habit.motivationMessage
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailingContent()
        }
    }
}

// ── Empty / Loading / Error ──────────────────────────────
@Composable
private fun EmptyStateView(onAddHabit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎯",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = MaterialTheme.typography.headlineLarge.fontSize * 2
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No habits yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start building your identity.\nEvery small action compounds.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddHabit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Add Your First Habit", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
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
