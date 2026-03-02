package com.nozero.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nozero.app.navigation.Screen
import com.nozero.app.ui.dashboard.TodayScreen
import com.nozero.app.ui.detail.HabitDetailScreen
import com.nozero.app.ui.onboarding.CreateHabitScreen
import com.nozero.app.ui.onboarding.OnboardingScreen
import com.nozero.app.ui.onboarding.TemplateSelectionScreen
import com.nozero.app.ui.theme.NoZeroTheme
import com.nozero.app.viewmodel.HabitDetailViewModel
import com.nozero.app.viewmodel.OnboardingViewModel
import com.nozero.app.viewmodel.TodayViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    NoZeroTheme {
        val navController = rememberNavController()
        val onboardingViewModel: OnboardingViewModel = koinInject()
        val todayViewModel: TodayViewModel = koinInject()
        val habitDetailViewModel: HabitDetailViewModel = koinInject()

        val startDestination = remember {
            if (onboardingViewModel.isOnboardingCompleted()) {
                Screen.Today.route
            } else {
                Screen.Onboarding.route
            }
        }

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // ── Onboarding Flow ──────────────────────────────
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.TemplateSelection.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.TemplateSelection.route) {
                TemplateSelectionScreen(
                    onTemplatesSelected = { templatesWithTimes ->
                        onboardingViewModel.createHabitsFromTemplates(templatesWithTimes)
                        onboardingViewModel.completeOnboarding()
                        navController.navigate(Screen.Today.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onCreateCustom = {
                        navController.navigate(Screen.CreateHabit.route)
                    }
                )
            }

            composable(Screen.CreateHabit.route) {
                CreateHabitScreen(
                    onHabitCreated = { title, description, type, frequency, tracking, style, reminderTime, allowBackdateLogging ->
                        onboardingViewModel.createCustomHabit(title, description, type, frequency, tracking, style, reminderTime, allowBackdateLogging)
                        onboardingViewModel.completeOnboarding()
                        navController.navigate(Screen.Today.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ── Main App Flow ────────────────────────────────
            composable(Screen.Today.route) {
                val preferences: com.nozero.shared.data.local.AppPreferences = koinInject()
                var showTutorial by remember {
                    mutableStateOf(
                        !preferences.getBoolean(
                            com.nozero.shared.data.local.PreferenceKeys.TUTORIAL_COMPLETED,
                            false
                        )
                    )
                }

                TodayScreen(
                    viewModel = todayViewModel,
                    onHabitClick = { habitId ->
                        navController.navigate(Screen.HabitDetail.createRoute(habitId))
                    },
                    onAddHabit = {
                        navController.navigate(Screen.AddHabit.route)
                    },
                    showTutorial = showTutorial,
                    onTutorialComplete = {
                        preferences.putBoolean(
                            com.nozero.shared.data.local.PreferenceKeys.TUTORIAL_COMPLETED,
                            true
                        )
                        showTutorial = false
                    }
                )
            }

            // Standalone Add Habit — template selection (reuses same screen)
            composable(Screen.AddHabit.route) {
                TemplateSelectionScreen(
                    onTemplatesSelected = { templatesWithTimes ->
                        onboardingViewModel.createHabitsFromTemplates(templatesWithTimes)
                        navController.popBackStack()
                    },
                    onCreateCustom = {
                        navController.navigate(Screen.AddHabitCustom.route)
                    }
                )
            }

            // Standalone Add Habit — custom form (reuses same screen)
            composable(Screen.AddHabitCustom.route) {
                CreateHabitScreen(
                    onHabitCreated = { title, description, type, frequency, tracking, style, reminderTime, allowBackdateLogging ->
                        onboardingViewModel.createCustomHabit(title, description, type, frequency, tracking, style, reminderTime, allowBackdateLogging)
                        // Pop back to Today (remove both add screens from stack)
                        navController.popBackStack(Screen.Today.route, inclusive = false)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.HabitDetail.route,
                arguments = listOf(navArgument("habitId") { type = NavType.StringType })
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
                HabitDetailScreen(
                    viewModel = habitDetailViewModel,
                    habitId = habitId,
                    onBack = { navController.popBackStack() },
                    onArchived = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
