package com.nozero.shared.`data`.local.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.nozero.shared.`data`.local.NoZeroDatabase
import com.nozero.shared.`data`.local.NoZeroDatabaseQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<NoZeroDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = NoZeroDatabaseImpl.Schema

internal fun KClass<NoZeroDatabase>.newInstance(driver: SqlDriver): NoZeroDatabase =
    NoZeroDatabaseImpl(driver)

private class NoZeroDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), NoZeroDatabase {
  override val noZeroDatabaseQueries: NoZeroDatabaseQueries = NoZeroDatabaseQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 2

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE HabitEntity (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    description TEXT,
          |    type TEXT NOT NULL,
          |    frequencyType TEXT NOT NULL,
          |    frequencyValue TEXT,
          |    trackingType TEXT NOT NULL,
          |    trackingTarget TEXT,
          |    reinforcementStyle TEXT NOT NULL DEFAULT 'NEUTRAL',
          |    reminderTime TEXT,
          |    isArchived INTEGER NOT NULL DEFAULT 0,
          |    createdAt INTEGER NOT NULL,
          |    isDeleted INTEGER NOT NULL DEFAULT 0,
          |    earnedGraceDays INTEGER NOT NULL DEFAULT 0,
          |    allowBackdateLogging INTEGER NOT NULL DEFAULT 1
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CompletionEntity (
          |    habitId TEXT NOT NULL,
          |    date TEXT NOT NULL,
          |    value REAL NOT NULL DEFAULT 1.0,
          |    status TEXT NOT NULL,
          |    PRIMARY KEY (habitId, date),
          |    FOREIGN KEY (habitId) REFERENCES HabitEntity(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    private fun migrateInternal(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
    ): QueryResult.Value<Unit> {
      if (oldVersion <= 1 && newVersion > 1) {
        driver.execute(null,
            "ALTER TABLE HabitEntity ADD COLUMN earnedGraceDays INTEGER NOT NULL DEFAULT 0", 0)
      }
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> {
      var lastVersion = oldVersion

      callbacks.filter { it.afterVersion in oldVersion until newVersion }
      .sortedBy { it.afterVersion }
      .forEach { callback ->
        migrateInternal(driver, oldVersion = lastVersion, newVersion = callback.afterVersion + 1)
        callback.block(driver)
        lastVersion = callback.afterVersion + 1
      }

      if (lastVersion < newVersion) {
        migrateInternal(driver, lastVersion, newVersion)
      }
      return QueryResult.Unit
    }
  }
}
