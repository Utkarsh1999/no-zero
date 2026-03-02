package com.nozero.shared.data.mapper

import com.nozero.shared.data.local.CompletionEntity
import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.model.CompletionStatus
import kotlinx.datetime.LocalDate

fun CompletionEntity.toDomain(): Completion {
    return Completion(
        habitId = habitId,
        date = LocalDate.parse(date),
        value = value_,
        status = CompletionStatus.valueOf(status)
    )
}
