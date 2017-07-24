package unus.blocking

import unus.db.Patient
import unus.model.Helpers

class FirstMRNFilteredBlocker extends BlockerBase {
  override val name: String = "FirstMRNFiltered"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.lev(p1.ssn, p2.ssn) <=2) &&
      (p1.dob.isEmpty || p2.dob.isEmpty || Helpers.lev(p1.dob, p2.dob) <=2)
  }

  override def filter(r: Patient): Boolean = {
    r.mrn.nonEmpty && r.first.nonEmpty
  }

  override def groupSplit(r: Patient): Seq[String] = {
    r.mrn.toList.flatMap { m =>
      val m5 = m.toInt/5000
      m5 + "_" + r.first.getOrElse("") ::
        (m5+1) + "_" + r.first.getOrElse("") ::
        (m5-1) + "_" + r.first.getOrElse("") :: Nil
    }
  }
}
