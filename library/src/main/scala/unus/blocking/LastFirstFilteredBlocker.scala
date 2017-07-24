package unus.blocking

import unus.db.Patient
import unus.model.Helpers

class LastFirstFilteredBlocker extends BlockerBase {
  override val name: String = "LastFirstFiltered"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.hamm(p1.ssn, p2.ssn) <=2) &&
      (p1.dob.isEmpty || p2.dob.isEmpty || Helpers.hamm(p1.dob, p2.dob) <=2)
  }

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.first.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.first.getOrElse("")
  }
}
