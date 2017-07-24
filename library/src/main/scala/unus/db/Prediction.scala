package unus.db

import java.sql.Timestamp

case class Prediction(
                       model: String,
                       id1: String,
                       id2: String,
                       label: Double,
                       prediction: Double,
                       ts: Timestamp
                     )
