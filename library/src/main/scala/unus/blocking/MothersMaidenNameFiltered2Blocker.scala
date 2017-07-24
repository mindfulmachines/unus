package unus.blocking

import unus.db.Patient
import unus.model.Helpers

class MothersMaidenNameFiltered2Blocker extends BlockerBase {
  override val name: String = "MothersMaidenNameFiltered2"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.first.isEmpty || p2.first.isEmpty || Helpers.lev(p1.first, p2.first) <=2)  &&
      (p1.last.isEmpty || p2.last.isEmpty || Helpers.lev(p1.last, p2.last) <= 2) &&
      (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.lev(p1.ssn, p2.ssn) <= 2) &&
      (p1.dob.isEmpty || p2.dob.isEmpty || Helpers.lev(p1.dob, p2.dob) <= 2)
  }

  override def filter(r: Patient): Boolean = {
    r.mothersMaidenName.nonEmpty
  }

  override def group(r: Patient): String = {
    r.mothersMaidenName.getOrElse("")
  }
}
