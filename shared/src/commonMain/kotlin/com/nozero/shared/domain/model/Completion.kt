package com.nozero.shared.domain.model

import kotlinx.datetime.LocalDate

/**
 * Represents a single day's log for a habit.
 */
data class Completion(
    val habitId: String,
    val date: LocalDate,
    val value: Double = 1.0,
    val status: CompletionStatus
)

enum class CompletionStatus {
    COMPLETED,
    MISSED,
    EXEMPTED,
    RELAPSED
}
