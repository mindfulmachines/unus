package unus.db

import java.sql.Timestamp

case class Model(
                  model: String,
                  ts: Timestamp,
                  features: String,
                  blockers: List[String],
                  batches: List[String],
                  trees: Int,
                  depth: Int,
                  f1: Double,
                  precision: Double,
                  recall: Double
                 )
