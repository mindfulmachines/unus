package unus.blocking

import unus.db.Patient

class EmailBlocker extends BlockerBase {
  override val name: String = "Email"

  override def filter(r: Patient): Boolean = {
    r.email.nonEmpty
  }

  override def group(r: Patient): String = {
    r.email.getOrElse("")
  }
}
