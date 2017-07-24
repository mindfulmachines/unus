package unus.db

import java.sql.Timestamp

case class Blocker (
                   blocker: String,
                   rows: Long,
                   ts: Timestamp
                   )
