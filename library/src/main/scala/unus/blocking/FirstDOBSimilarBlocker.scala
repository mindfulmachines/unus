package unus.blocking

import unus.model.Helpers
import unus.db.Patient

class FirstDOBSimilarBlocker extends BlockerBase {
  override val name: String = "FirstDOBSimilar"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.first != p2.first && Helpers.lev(p1.first, p2.first) <=2)  &&
      (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.lev(p1.ssn, p2.ssn) <= 2)
  }


  override def filter(r: Patient): Boolean = {
    r.first.nonEmpty && r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.dob.getOrElse("")
  }
}
