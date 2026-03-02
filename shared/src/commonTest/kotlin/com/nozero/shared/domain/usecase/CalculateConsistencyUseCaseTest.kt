package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.model.CompletionStatus
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateConsistencyUseCaseTest {

    private val useCase = CalculateConsistencyUseCase()

    // -- Streak Tests --

    @Test
    fun `empty history returns zero metrics`() {
        val result = useCase.execute(emptyList())
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.bestStreak)
        assertEquals(0.0, result.consistencyScore)
        assertEquals(0, result.totalCompleted)
        assertEquals(0, result.totalMissed)
    }

    @Test
    fun `three consecutive completions gives streak of 3`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.COMPLETED),
            completion("2026-03-03", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertEquals(3, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    @Test
    fun `miss breaks streak and resets current to zero`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.COMPLETED),
            completion("2026-03-03", CompletionStatus.MISSED),
        )
        val result = useCase.execute(history)
        assertEquals(0, result.currentStreak)
        assertEquals(2, result.bestStreak)
    }

    @Test
    fun `exempted day does NOT break streak`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.EXEMPTED),
            completion("2026-03-03", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertEquals(2, result.currentStreak)
    }

    @Test
    fun `relapse breaks streak the same as miss`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.RELAPSED),
            completion("2026-03-03", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
    }

    // -- Recovery Tests --

    @Test
    fun `recovery bonus applied when completing after a miss`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.MISSED),
            completion("2026-03-03", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertEquals(1, result.recoveryCount)
        assertTrue(result.consistencyScore > 0.0, "Score should be positive after recovery")
    }

    @Test
    fun `consistency score never goes below zero`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.MISSED),
            completion("2026-03-02", CompletionStatus.MISSED),
            completion("2026-03-03", CompletionStatus.MISSED),
            completion("2026-03-04", CompletionStatus.MISSED),
        )
        val result = useCase.execute(history)
        assertEquals(0.0, result.consistencyScore)
    }

    @Test
    fun `isInRecovery is true when last entry is missed`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.MISSED),
        )
        val result = useCase.execute(history)
        assertTrue(result.isInRecovery)
    }

    @Test
    fun `isInRecovery is false when last entry is completed`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.MISSED),
            completion("2026-03-02", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertFalse(result.isInRecovery)
    }

    // -- Edge Cases --

    @Test
    fun `single completed day`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED)
        )
        val result = useCase.execute(history)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
        assertEquals(1, result.totalCompleted)
    }

    @Test
    fun `mixed history preserves best streak`() {
        val history = listOf(
            completion("2026-03-01", CompletionStatus.COMPLETED),
            completion("2026-03-02", CompletionStatus.COMPLETED),
            completion("2026-03-03", CompletionStatus.COMPLETED),
            completion("2026-03-04", CompletionStatus.MISSED),
            completion("2026-03-05", CompletionStatus.COMPLETED),
        )
        val result = useCase.execute(history)
        assertEquals(1, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    // -- Helpers --

    private fun completion(date: String, status: CompletionStatus) = Completion(
        habitId = "test-habit",
        date = LocalDate.parse(date),
        value = 1.0,
        status = status
    )
}
