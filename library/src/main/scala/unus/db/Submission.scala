package unus.db

import java.sql.Timestamp

case class Submission (
                      submission: String,
                      model: String,
                      blockers: List[String],
                      ts: Timestamp,
                      f1: Option[Double],
                      precision: Option[Double],
                      recall: Option[Double]
                      )
