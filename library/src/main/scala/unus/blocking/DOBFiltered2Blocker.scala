package unus.blocking

import unus.db.Patient
import unus.model.{Features, Helpers}

class DOBFiltered2Blocker extends BlockerBase {
  override val name: String = "DOBFiltered2"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.first.isEmpty || p2.first.isEmpty || Helpers.lev(p1.first, p2.first) <= 3) &&
      (p1.last.isEmpty || p2.last.isEmpty || Helpers.lev(p1.last, p2.last) <= 3) &&
      (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.lev(p1.ssn, p2.ssn) <= 3)
  }

  override def filter(r: Patient): Boolean = {
    r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.dob.getOrElse("")
  }
}
