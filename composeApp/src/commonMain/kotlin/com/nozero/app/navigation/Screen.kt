package com.nozero.app.navigation

/**
 * Navigation destinations for the NoZero app.
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object TemplateSelection : Screen("template_selection")
    data object CreateHabit : Screen("create_habit")
    data object Today : Screen("today")
    data object AddHabit : Screen("add_habit")
    data object AddHabitCustom : Screen("add_habit_custom")
    data object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: String) = "habit_detail/$habitId"
    }
    data object Settings : Screen("settings")
}
