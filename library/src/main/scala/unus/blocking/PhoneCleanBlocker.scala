package unus.blocking

import unus.db.Patient

class PhoneCleanBlocker extends BlockerBase {
  override val name: String = "PhoneClean"

  override def filter(r: Patient): Boolean = {
    r.phone.nonEmpty &&
      r.phone.get.replaceAllLiterally("-","").toCharArray.distinct.length > 1 &&
      r.phone.get.replaceAllLiterally("-","") != "1234567890" &&
      r.phone.get.replaceAllLiterally("-","") != "0123456789"
  }

  override def group(r: Patient): String = {
    r.phone.getOrElse("").replaceAllLiterally("-","")
  }
}
