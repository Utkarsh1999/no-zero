package com.nozero.shared.`data`.local

import kotlin.Long
import kotlin.String

public data class HabitEntity(
  public val id: String,
  public val title: String,
  public val description: String?,
  public val type: String,
  public val frequencyType: String,
  public val frequencyValue: String?,
  public val trackingType: String,
  public val trackingTarget: String?,
  public val reinforcementStyle: String,
  public val reminderTime: String?,
  public val isArchived: Long,
  public val createdAt: Long,
  public val isDeleted: Long,
  public val earnedGraceDays: Long,
  public val allowBackdateLogging: Long,
)
