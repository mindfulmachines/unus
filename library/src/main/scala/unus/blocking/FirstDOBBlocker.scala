package unus.blocking

import unus.db.Patient

class FirstDOBBlocker extends BlockerBase {
  override val name: String = "FirstDOB"

  override def filter(r: Patient): Boolean = {
    r.first.nonEmpty && r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.first.getOrElse("") + "_" + r.dob.getOrElse("")
  }
}
