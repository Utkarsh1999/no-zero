package com.nozero.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class OnboardingPage(
    val title: String,
    val body: String,
    val emoji: String
)

private val pages = listOf(
    OnboardingPage(
        title = "No Zero Days",
        body = "Track consistency, not perfection.\nEvery small action counts.",
        emoji = "🔥"
    ),
    OnboardingPage(
        title = "Built for Recovery",
        body = "Missed a day? That's okay.\nWe reward your comeback, not punish your slip.",
        emoji = "💪"
    ),
    OnboardingPage(
        title = "Your Identity Shift",
        body = "You're not just tracking habits.\nYou're becoming someone new.",
        emoji = "🧠"
    )
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Emoji
        AnimatedContent(
            targetState = pages[currentPage].emoji,
            transitionSpec = {
                fadeIn() + slideInVertically { it / 2 } togetherWith
                        fadeOut() + slideOutVertically { -it / 2 }
            },
            label = "emoji"
        ) { emoji ->
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize * 2
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Title
        AnimatedContent(
            targetState = pages[currentPage].title,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "title"
        ) { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body
        AnimatedContent(
            targetState = pages[currentPage].body,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "body"
        ) { body ->
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Page Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                        .background(
                            color = if (index == currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        // CTA Button
        val isLast = currentPage == pages.lastIndex
        Button(
            onClick = {
                if (isLast) onComplete() else currentPage++
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = if (isLast) "Get Started" else "Next",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Skip
        if (!isLast) {
            TextButton(
                onClick = onComplete,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
