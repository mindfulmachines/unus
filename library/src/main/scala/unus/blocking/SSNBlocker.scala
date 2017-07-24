package unus.blocking

import unus.db.Patient

class SSNBlocker extends BlockerBase {
  override val name: String = "SSN"

  override def filter(r: Patient): Boolean = {
    r.ssn.nonEmpty
  }

  override def group(r: Patient): String = {
    r.ssn.getOrElse("")
  }
}
