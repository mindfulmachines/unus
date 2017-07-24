package unus.db

import java.sql.Timestamp

case class LabeledRow(
                      blocker: String,
                      id1: String,
                      id2: String,
                      batch: String,
                      valid: Boolean,
                      ts: Timestamp
                      )
