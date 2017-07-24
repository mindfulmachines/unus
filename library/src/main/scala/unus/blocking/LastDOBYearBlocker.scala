package unus.blocking

import unus.db.Patient

class LastDOBYearBlocker extends BlockerBase {
  override val name: String = "LastDOBYear"

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.dob.getOrElse("").split('/')(2)
  }
}
