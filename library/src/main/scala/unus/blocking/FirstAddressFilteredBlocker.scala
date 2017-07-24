package unus.blocking

import unus.db.Patient

class FirstAddressFilteredBlocker extends BlockerBase {
  override val name: String = "FirstAddressFiltered"

  override def filter(r: Patient): Boolean = {
    r.first.nonEmpty && r.address1.nonEmpty && ! Set("UNK","UNKNOWN","UNDOMICILED","UNABLE TO OBTAIN","HOMELESS").contains(r.address1.get)
  }

  override def group(r: Patient): String = {
    r.first.getOrElse("") + "_" + r.address1.getOrElse("")
  }
}
