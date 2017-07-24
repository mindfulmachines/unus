package unus.stage

import unus.db.{BlockedRow, Label, LabeledRow, Patient}

case class PairedPatients(
                           labeled: LabeledRow,
                           patient1: Patient,
                           patient2: Patient
                         )