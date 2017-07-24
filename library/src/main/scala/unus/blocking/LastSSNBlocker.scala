package unus.blocking

import unus.db.Patient

class LastSSNBlocker extends BlockerBase {
  override val name: String = "LastSSN"

  override def filter(r: Patient): Boolean = {
    r.ssn.nonEmpty && r.last.nonEmpty
  }

  override def group(r: Patient): String = {
    r.ssn.getOrElse("") + "_" + r.last.getOrElse("")
  }
}
