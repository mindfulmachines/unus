package unus.db

import java.sql.Timestamp

case class BlockedRow (
                      blocker: String,
                      id1: String,
                      id2: String,
                      ts: Timestamp
                      )
