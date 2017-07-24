package unus.blocking

import unus.db.Patient

class FirstSSNBlocker extends BlockerBase {
  override val name: String = "FirstSSN"

  override def filter(r: Patient): Boolean = {
    r.ssn.nonEmpty && r.first.nonEmpty
  }

  override def group(r: Patient): String = {
    r.ssn.getOrElse("") + "_" + r.first.getOrElse("")
  }
}
