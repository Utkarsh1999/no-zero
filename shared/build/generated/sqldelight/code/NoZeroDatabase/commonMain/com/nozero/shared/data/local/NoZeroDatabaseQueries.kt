package com.nozero.shared.`data`.local

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Long
import kotlin.String

public class NoZeroDatabaseQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getActiveHabits(mapper: (
    id: String,
    title: String,
    description: String?,
    type: String,
    frequencyType: String,
    frequencyValue: String?,
    trackingType: String,
    trackingTarget: String?,
    reinforcementStyle: String,
    reminderTime: String?,
    isArchived: Long,
    createdAt: Long,
    isDeleted: Long,
    earnedGraceDays: Long,
  ) -> T): Query<T> = Query(-1_874_777_529, arrayOf("HabitEntity"), driver, "NoZeroDatabase.sq",
      "getActiveHabits",
      "SELECT HabitEntity.id, HabitEntity.title, HabitEntity.description, HabitEntity.type, HabitEntity.frequencyType, HabitEntity.frequencyValue, HabitEntity.trackingType, HabitEntity.trackingTarget, HabitEntity.reinforcementStyle, HabitEntity.reminderTime, HabitEntity.isArchived, HabitEntity.createdAt, HabitEntity.isDeleted, HabitEntity.earnedGraceDays FROM HabitEntity WHERE isArchived = 0 AND isDeleted = 0 ORDER BY createdAt DESC") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5),
      cursor.getString(6)!!,
      cursor.getString(7),
      cursor.getString(8)!!,
      cursor.getString(9),
      cursor.getLong(10)!!,
      cursor.getLong(11)!!,
      cursor.getLong(12)!!,
      cursor.getLong(13)!!
    )
  }

  public fun getActiveHabits(): Query<HabitEntity> = getActiveHabits { id, title, description, type,
      frequencyType, frequencyValue, trackingType, trackingTarget, reinforcementStyle, reminderTime,
      isArchived, createdAt, isDeleted, earnedGraceDays ->
    HabitEntity(
      id,
      title,
      description,
      type,
      frequencyType,
      frequencyValue,
      trackingType,
      trackingTarget,
      reinforcementStyle,
      reminderTime,
      isArchived,
      createdAt,
      isDeleted,
      earnedGraceDays
    )
  }

  public fun <T : Any> getHabitById(id: String, mapper: (
    id: String,
    title: String,
    description: String?,
    type: String,
    frequencyType: String,
    frequencyValue: String?,
    trackingType: String,
    trackingTarget: String?,
    reinforcementStyle: String,
    reminderTime: String?,
    isArchived: Long,
    createdAt: Long,
    isDeleted: Long,
    earnedGraceDays: Long,
  ) -> T): Query<T> = GetHabitByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5),
      cursor.getString(6)!!,
      cursor.getString(7),
      cursor.getString(8)!!,
      cursor.getString(9),
      cursor.getLong(10)!!,
      cursor.getLong(11)!!,
      cursor.getLong(12)!!,
      cursor.getLong(13)!!
    )
  }

  public fun getHabitById(id: String): Query<HabitEntity> = getHabitById(id) { id_, title,
      description, type, frequencyType, frequencyValue, trackingType, trackingTarget,
      reinforcementStyle, reminderTime, isArchived, createdAt, isDeleted, earnedGraceDays ->
    HabitEntity(
      id_,
      title,
      description,
      type,
      frequencyType,
      frequencyValue,
      trackingType,
      trackingTarget,
      reinforcementStyle,
      reminderTime,
      isArchived,
      createdAt,
      isDeleted,
      earnedGraceDays
    )
  }

  public fun <T : Any> getCompletionsForHabit(habitId: String, mapper: (
    habitId: String,
    date: String,
    value_: Double,
    status: String,
  ) -> T): Query<T> = GetCompletionsForHabitQuery(habitId) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getDouble(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun getCompletionsForHabit(habitId: String): Query<CompletionEntity> =
      getCompletionsForHabit(habitId) { habitId_, date, value_, status ->
    CompletionEntity(
      habitId_,
      date,
      value_,
      status
    )
  }

  public fun <T : Any> getCompletionsForDate(date: String, mapper: (
    habitId: String,
    date: String,
    value_: Double,
    status: String,
  ) -> T): Query<T> = GetCompletionsForDateQuery(date) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getDouble(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun getCompletionsForDate(date: String): Query<CompletionEntity> =
      getCompletionsForDate(date) { habitId, date_, value_, status ->
    CompletionEntity(
      habitId,
      date_,
      value_,
      status
    )
  }

  public fun <T : Any> getCompletionRange(
    habitId: String,
    date: String,
    date_: String,
    mapper: (
      habitId: String,
      date: String,
      value_: Double,
      status: String,
    ) -> T,
  ): Query<T> = GetCompletionRangeQuery(habitId, date, date_) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getDouble(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun getCompletionRange(
    habitId: String,
    date: String,
    date_: String,
  ): Query<CompletionEntity> = getCompletionRange(habitId, date, date_) { habitId_, date__, value_,
      status ->
    CompletionEntity(
      habitId_,
      date__,
      value_,
      status
    )
  }

  public fun insertHabit(
    id: String,
    title: String,
    description: String?,
    type: String,
    frequencyType: String,
    frequencyValue: String?,
    trackingType: String,
    trackingTarget: String?,
    reinforcementStyle: String,
    reminderTime: String?,
    isArchived: Long,
    createdAt: Long,
    earnedGraceDays: Long,
  ) {
    driver.execute(-1_802_966_041, """
        |INSERT OR REPLACE INTO HabitEntity(id, title, description, type, frequencyType, frequencyValue, trackingType, trackingTarget, reinforcementStyle, reminderTime, isArchived, createdAt, isDeleted, earnedGraceDays)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?)
        """.trimMargin(), 13) {
          bindString(0, id)
          bindString(1, title)
          bindString(2, description)
          bindString(3, type)
          bindString(4, frequencyType)
          bindString(5, frequencyValue)
          bindString(6, trackingType)
          bindString(7, trackingTarget)
          bindString(8, reinforcementStyle)
          bindString(9, reminderTime)
          bindLong(10, isArchived)
          bindLong(11, createdAt)
          bindLong(12, earnedGraceDays)
        }
    notifyQueries(-1_802_966_041) { emit ->
      emit("HabitEntity")
    }
  }

  public fun updateHabit(
    title: String,
    description: String?,
    frequencyType: String,
    frequencyValue: String?,
    trackingType: String,
    trackingTarget: String?,
    reinforcementStyle: String,
    reminderTime: String?,
    earnedGraceDays: Long,
    id: String,
  ) {
    driver.execute(2_021_703_639, """
        |UPDATE HabitEntity SET title = ?, description = ?, frequencyType = ?, frequencyValue = ?, trackingType = ?, trackingTarget = ?, reinforcementStyle = ?, reminderTime = ?, earnedGraceDays = ?
        |WHERE id = ?
        """.trimMargin(), 10) {
          bindString(0, title)
          bindString(1, description)
          bindString(2, frequencyType)
          bindString(3, frequencyValue)
          bindString(4, trackingType)
          bindString(5, trackingTarget)
          bindString(6, reinforcementStyle)
          bindString(7, reminderTime)
          bindLong(8, earnedGraceDays)
          bindString(9, id)
        }
    notifyQueries(2_021_703_639) { emit ->
      emit("HabitEntity")
    }
  }

  public fun archiveHabit(id: String) {
    driver.execute(2_110_651_814, """UPDATE HabitEntity SET isArchived = 1 WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(2_110_651_814) { emit ->
      emit("HabitEntity")
    }
  }

  public fun deleteHabit(id: String) {
    driver.execute(1_473_327_157, """UPDATE HabitEntity SET isDeleted = 1 WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(1_473_327_157) { emit ->
      emit("HabitEntity")
    }
  }

  public fun insertCompletion(
    habitId: String,
    date: String,
    value_: Double,
    status: String,
  ) {
    driver.execute(-991_450_807,
        """INSERT OR REPLACE INTO CompletionEntity(habitId, date, value, status) VALUES (?, ?, ?, ?)""",
        4) {
          bindString(0, habitId)
          bindString(1, date)
          bindDouble(2, value_)
          bindString(3, status)
        }
    notifyQueries(-991_450_807) { emit ->
      emit("CompletionEntity")
    }
  }

  public fun deleteCompletion(habitId: String, date: String) {
    driver.execute(1_564_730_811, """DELETE FROM CompletionEntity WHERE habitId = ? AND date = ?""",
        2) {
          bindString(0, habitId)
          bindString(1, date)
        }
    notifyQueries(1_564_730_811) { emit ->
      emit("CompletionEntity")
    }
  }

  private inner class GetHabitByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("HabitEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("HabitEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_960_381_572,
        """SELECT HabitEntity.id, HabitEntity.title, HabitEntity.description, HabitEntity.type, HabitEntity.frequencyType, HabitEntity.frequencyValue, HabitEntity.trackingType, HabitEntity.trackingTarget, HabitEntity.reinforcementStyle, HabitEntity.reminderTime, HabitEntity.isArchived, HabitEntity.createdAt, HabitEntity.isDeleted, HabitEntity.earnedGraceDays FROM HabitEntity WHERE id = ? AND isDeleted = 0""",
        mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "NoZeroDatabase.sq:getHabitById"
  }

  private inner class GetCompletionsForHabitQuery<out T : Any>(
    public val habitId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CompletionEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CompletionEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_277_548_288,
        """SELECT CompletionEntity.habitId, CompletionEntity.date, CompletionEntity.value, CompletionEntity.status FROM CompletionEntity WHERE habitId = ? ORDER BY date DESC""",
        mapper, 1) {
      bindString(0, habitId)
    }

    override fun toString(): String = "NoZeroDatabase.sq:getCompletionsForHabit"
  }

  private inner class GetCompletionsForDateQuery<out T : Any>(
    public val date: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CompletionEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CompletionEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(733_829_282,
        """SELECT CompletionEntity.habitId, CompletionEntity.date, CompletionEntity.value, CompletionEntity.status FROM CompletionEntity WHERE date = ?""",
        mapper, 1) {
      bindString(0, date)
    }

    override fun toString(): String = "NoZeroDatabase.sq:getCompletionsForDate"
  }

  private inner class GetCompletionRangeQuery<out T : Any>(
    public val habitId: String,
    public val date: String,
    public val date_: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CompletionEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CompletionEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_444_244_129,
        """SELECT CompletionEntity.habitId, CompletionEntity.date, CompletionEntity.value, CompletionEntity.status FROM CompletionEntity WHERE habitId = ? AND date >= ? AND date <= ? ORDER BY date ASC""",
        mapper, 3) {
      bindString(0, habitId)
      bindString(1, date)
      bindString(2, date_)
    }

    override fun toString(): String = "NoZeroDatabase.sq:getCompletionRange"
  }
}
