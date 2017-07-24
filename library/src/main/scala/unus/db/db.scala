package unus

import io.getquill.{PostgresEscape, PostgresJdbcContext}

package object db {
  type DbContext = PostgresJdbcContext[PostgresEscape]
}
