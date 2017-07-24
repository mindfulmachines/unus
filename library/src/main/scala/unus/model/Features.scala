package unus.model

import unus.db.{BlockedRow, LabeledRow}

case class Features(
                     blocked: LabeledRow,
                     label: Boolean,
                     features: Array[Double]
                   )
