package unus.blocking

import unus.db.Patient

class FirstMRNBlocker extends BlockerBase {
  override val name: String = "FirstMRN"

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
