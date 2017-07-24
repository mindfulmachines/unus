package unus.blocking

import unus.db.Patient

class LastDOBYearGenderBlocker extends BlockerBase {
  override val name: String = "LastDOBYearGender"

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.dob.nonEmpty && r.gender.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.dob.getOrElse("").split('/')(2) + "_" + r.gender.getOrElse("U").headOption.getOrElse("U")
  }
}
