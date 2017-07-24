package unus.db

import java.sql.Timestamp

case class Label(
                  batch: String,
                  id1: String,
                  id2: String,
                  valid: Boolean,
                  ts: Timestamp
                )
