package com.nozero.shared.`data`.local

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.nozero.shared.`data`.local.shared.newInstance
import com.nozero.shared.`data`.local.shared.schema
import kotlin.Unit

public interface NoZeroDatabase : Transacter {
  public val noZeroDatabaseQueries: NoZeroDatabaseQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = NoZeroDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): NoZeroDatabase =
        NoZeroDatabase::class.newInstance(driver)
  }
}
