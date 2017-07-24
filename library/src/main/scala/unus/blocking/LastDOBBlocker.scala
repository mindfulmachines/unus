package unus.blocking

import unus.db.Patient

class LastDOBBlocker extends BlockerBase {
  override val name: String = "LastDOB"

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.dob.getOrElse("")
  }
}
