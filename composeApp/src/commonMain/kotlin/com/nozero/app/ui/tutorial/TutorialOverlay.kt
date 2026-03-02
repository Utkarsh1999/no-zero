package com.nozero.app.ui.tutorial

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A single step in the guided tutorial.
 */
data class TutorialStep(
    val title: String,
    val message: String,
    val spotlightPosition: SpotlightPosition,
    val tooltipAlignment: TooltipAlignment = TooltipAlignment.BOTTOM
)

enum class SpotlightPosition {
    FIRST_CARD,
    CARD_ACTION,
    CARD_BODY,
    FAB,
    NONE
}

enum class TooltipAlignment {
    TOP, BOTTOM, CENTER
}

/**
 * The tutorial step sequence.
 */
val tutorialSteps = listOf(
    TutorialStep(
        title = "Your Habit Card",
        message = "This is your habit card. It shows your current streak and today's status.",
        spotlightPosition = SpotlightPosition.FIRST_CARD,
        tooltipAlignment = TooltipAlignment.BOTTOM
    ),
    TutorialStep(
        title = "Log Your Progress",
        message = "Tap the action button to log your habit. Different types have different logging methods — tap, enter minutes, or count.",
        spotlightPosition = SpotlightPosition.CARD_ACTION,
        tooltipAlignment = TooltipAlignment.BOTTOM
    ),
    TutorialStep(
        title = "View Your History",
        message = "Tap the card to see your full history, consistency grid, and identity milestones.",
        spotlightPosition = SpotlightPosition.CARD_BODY,
        tooltipAlignment = TooltipAlignment.BOTTOM
    ),
    TutorialStep(
        title = "Add More Habits",
        message = "Need more habits? Tap the + button to add from templates or create your own.",
        spotlightPosition = SpotlightPosition.FAB,
        tooltipAlignment = TooltipAlignment.TOP
    ),
    TutorialStep(
        title = "You're Ready! 🔥",
        message = "Remember: no zero days.\nEvery small action builds your identity.",
        spotlightPosition = SpotlightPosition.NONE,
        tooltipAlignment = TooltipAlignment.CENTER
    )
)

/**
 * Full-screen tutorial overlay with spotlight cutout and tooltip.
 */
@Composable
fun TutorialOverlay(
    currentStep: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val step = tutorialSteps[currentStep]
    val isLast = currentStep == tutorialSteps.lastIndex

    // Pulsing animation for spotlight
    val pulseAlpha by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.55f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* consume taps on overlay */ }
    ) {
        // Semi-transparent backdrop
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.99f } // Required for BlendMode.Clear
        ) {
            // Dark overlay
            drawRect(Color.Black.copy(alpha = pulseAlpha))

            // Spotlight cutout
            when (step.spotlightPosition) {
                SpotlightPosition.FIRST_CARD -> drawSpotlightRect(
                    center = Offset(size.width / 2, size.height * 0.38f),
                    spotSize = Size(size.width - 48.dp.toPx(), 120.dp.toPx())
                )
                SpotlightPosition.CARD_ACTION -> drawSpotlightRect(
                    center = Offset(size.width - 60.dp.toPx(), size.height * 0.38f),
                    spotSize = Size(80.dp.toPx(), 60.dp.toPx())
                )
                SpotlightPosition.CARD_BODY -> drawSpotlightRect(
                    center = Offset(size.width * 0.38f, size.height * 0.38f),
                    spotSize = Size(size.width * 0.65f, 100.dp.toPx())
                )
                SpotlightPosition.FAB -> drawSpotlightCircle(
                    center = Offset(size.width - 52.dp.toPx(), size.height - 80.dp.toPx()),
                    radius = 36.dp.toPx()
                )
                SpotlightPosition.NONE -> { /* No cutout */ }
            }
        }

        // Tooltip
        val tooltipModifier = when (step.tooltipAlignment) {
            TooltipAlignment.TOP -> Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
            TooltipAlignment.BOTTOM -> Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
            TooltipAlignment.CENTER -> Modifier.align(Alignment.Center)
        }

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(200))
            },
            modifier = tooltipModifier.padding(horizontal = 32.dp),
            label = "tooltip"
        ) { _ ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = step.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Step indicator dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        tutorialSteps.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == currentStep) 20.dp else 6.dp, 6.dp)
                                    .background(
                                        color = if (index == currentStep)
                                            Color(0xFF818CF8)
                                        else
                                            Color(0xFF818CF8).copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }

                    // Buttons
                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isLast) "Let's Go!" else "Next",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (!isLast) {
                        TextButton(onClick = onSkip) {
                            Text(
                                text = "Skip Tutorial",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSpotlightRect(center: Offset, spotSize: Size) {
    drawRoundRect(
        color = Color.Black,
        topLeft = Offset(center.x - spotSize.width / 2, center.y - spotSize.height / 2),
        size = spotSize,
        cornerRadius = CornerRadius(16.dp.toPx()),
        blendMode = BlendMode.Clear
    )
}

private fun DrawScope.drawSpotlightCircle(center: Offset, radius: Float) {
    drawCircle(
        color = Color.Black,
        center = center,
        radius = radius,
        blendMode = BlendMode.Clear
    )
}
