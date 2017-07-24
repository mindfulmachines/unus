package unus.blocking

import unus.db.Patient

class LastFirstBlocker extends BlockerBase {
  override val name: String = "LastFirst"

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.first.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.first.getOrElse("")
  }
}
