package com.nozero.shared.motivation

import com.nozero.shared.domain.model.ConsistencyMetrics
import com.nozero.shared.domain.model.HabitType
import com.nozero.shared.domain.model.ReinforcementStyle
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class MotivationEngineTest {

    private val engine = MotivationEngine()

    @Test
    fun `recovery message returned when in recovery state`() {
        val metrics = metrics(isInRecovery = true)
        val msg = engine.getDailyMessage("Gym", HabitType.GOOD, metrics, ReinforcementStyle.SOFT)
        assertContains(msg.lowercase(), "miss", message = "Recovery message should mention missing")
    }

    @Test
    fun `milestone message at 7 day streak`() {
        val metrics = metrics(currentStreak = 7)
        val msg = engine.getDailyMessage("Study", HabitType.GOOD, metrics, ReinforcementStyle.NEUTRAL)
        assertContains(msg.lowercase(), "week")
    }

    @Test
    fun `milestone message at 30 day streak`() {
        val metrics = metrics(currentStreak = 30)
        val msg = engine.getDailyMessage("Meditate", HabitType.GOOD, metrics, ReinforcementStyle.AGGRESSIVE)
        assertContains(msg.lowercase(), "foundation")
    }

    @Test
    fun `bad habit identity message contains habit title`() {
        val metrics = metrics(currentStreak = 2)
        val msg = engine.getDailyMessage("Smoking", HabitType.BAD, metrics, ReinforcementStyle.SOFT)
        assertContains(msg, "Smoking")
    }

    @Test
    fun `aggressive style returns stronger language`() {
        val metrics = metrics(currentStreak = 2)
        val msg = engine.getDailyMessage("Gym", HabitType.GOOD, metrics, ReinforcementStyle.AGGRESSIVE)
        assertTrue(msg.contains("excuse", ignoreCase = true) || msg.contains("identity", ignoreCase = true))
    }

    private fun metrics(
        currentStreak: Int = 0,
        isInRecovery: Boolean = false
    ) = ConsistencyMetrics(
        currentStreak = currentStreak,
        bestStreak = currentStreak,
        consistencyScore = 0.5,
        totalCompleted = currentStreak,
        totalMissed = 0,
        recoveryCount = 0,
        isInRecovery = isInRecovery
    )
}
