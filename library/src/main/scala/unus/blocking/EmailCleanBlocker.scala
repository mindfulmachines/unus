package unus.blocking

import unus.db.Patient

class EmailCleanBlocker extends BlockerBase {
  override val name: String = "EmailClean"

  override def filter(r: Patient): Boolean = {
    r.email.nonEmpty && r.email.get.split("@")(0).length > 1
  }

  override def group(r: Patient): String = {
    r.email.getOrElse("")
  }
}
