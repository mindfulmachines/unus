package unus.blocking

import unus.db.Patient

class NameDOBBlocker extends BlockerBase {
  override val name: String = "NameDOB"

  override def groupSplit(r: Patient): Seq[String] = {
    (r.first.toList ::: r.last.toList ::: r.alias.toList.filter(r => ! Set("JOHN DOE","JANE DOE" ,"PATIENTNOTFOUND").contains(r)).flatMap(_.split(' ').toList))
      .distinct.map(_ + "_" + r.dob.get)
  }

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.first.isEmpty || p2.first.isEmpty || p1.first != p2.first) &&
      (p1.last.isEmpty || p2.last.isEmpty || p1.last != p2.last)
  }
  override def filter(r: Patient): Boolean = {
    (r.first.nonEmpty || r.last.nonEmpty || r.alias.nonEmpty) && r.dob.nonEmpty
  }
}
