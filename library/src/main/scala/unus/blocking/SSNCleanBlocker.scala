package unus.blocking

import unus.db.Patient

class SSNCleanBlocker extends BlockerBase {
  override val name: String = "SSNClean"

  override def filter(r: Patient): Boolean = {
    r.ssn.nonEmpty &&
      r.ssn.get.replaceAllLiterally("-","").toCharArray.distinct.length > 1 &&
      r.ssn.get.replaceAllLiterally("-","") != "123456789"
  }

  override def group(r: Patient): String = {
    r.ssn.getOrElse("").replaceAllLiterally("-","")
  }
}
