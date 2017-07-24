package unus.blocking

import unus.db.Patient

class EmailCleanSSNBlocker extends BlockerBase {
  override val name: String = "EmailCleanSSN"

  override def filter(r: Patient): Boolean = {
    r.ssn.nonEmpty && r.email.nonEmpty && r.email.get.split("@")(0).length > 1
  }

  override def group(r: Patient): String = {
    r.email.getOrElse("") + "_" + r.ssn.getOrElse("")
  }
}
