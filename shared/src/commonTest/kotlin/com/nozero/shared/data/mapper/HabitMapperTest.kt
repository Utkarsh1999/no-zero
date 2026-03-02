package com.nozero.shared.data.mapper

import com.nozero.shared.domain.model.*
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitMapperTest {

    @Test
    fun `frequency round-trip for Daily`() {
        val habit = createHabit(frequency = HabitFrequency.Daily)
        val entity = habit.toEntity()
        assertEquals("DAILY", entity.frequencyType)
        assertEquals(null, entity.frequencyValue)
    }

    @Test
    fun `frequency round-trip for TimesPerWeek`() {
        val habit = createHabit(frequency = HabitFrequency.TimesPerWeek(3))
        val entity = habit.toEntity()
        assertEquals("TIMES_PER_WEEK", entity.frequencyType)
        assertEquals("3", entity.frequencyValue)
    }

    @Test
    fun `tracking type round-trip for Count`() {
        val habit = createHabit(trackingType = TrackingType.Count(10000, "steps"))
        val entity = habit.toEntity()
        assertEquals("COUNT", entity.trackingType)
        assertEquals("10000:steps", entity.trackingTarget)
    }

    @Test
    fun `tracking type round-trip for Time`() {
        val habit = createHabit(trackingType = TrackingType.Time(120))
        val entity = habit.toEntity()
        assertEquals("TIME", entity.trackingType)
        assertEquals("120", entity.trackingTarget)
    }

    private fun createHabit(
        frequency: HabitFrequency = HabitFrequency.Daily,
        trackingType: TrackingType = TrackingType.Binary
    ) = Habit(
        id = "test",
        title = "Test",
        type = HabitType.GOOD,
        frequency = frequency,
        trackingType = trackingType,
        createdAt = Clock.System.now()
    )
}
