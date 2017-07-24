package unus.blocking

import unus.db.Patient
import unus.model.Helpers


class DOBFilteredBlocker extends BlockerBase {
  override val name: String = "DOBFiltered"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    Helpers.lev(p1.first, p2.first) <= 3 ||
      Helpers.lev(p1.last, p2.last) <= 3 ||
      Helpers.lev(p1.address1, p2.address1) <= 3
  }

  override def filter(r: Patient): Boolean = {
    r.dob.nonEmpty
  }

  override def group(r: Patient): String = {
    r.dob.getOrElse("")
  }
}
