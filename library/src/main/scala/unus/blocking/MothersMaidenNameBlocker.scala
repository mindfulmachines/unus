package unus.blocking

import unus.db.Patient

class MothersMaidenNameBlocker extends BlockerBase {
  override val name: String = "MothersMaidenName"


  override def filter(r: Patient): Boolean = {
    r.mothersMaidenName.nonEmpty
  }

  override def group(r: Patient): String = {
    r.mothersMaidenName.getOrElse("")
  }
}
