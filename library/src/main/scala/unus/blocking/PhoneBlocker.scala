package unus.blocking

import unus.db.Patient

class PhoneBlocker extends BlockerBase {
  override val name: String = "Phone"

  override def filter(r: Patient): Boolean = {
    r.phone.nonEmpty
  }

  override def group(r: Patient): String = {
    r.phone.getOrElse("")
  }
}
