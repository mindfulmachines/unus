package unus.blocking

import unus.db.Patient

class PhoneCleanSplitBlocker extends BlockerBase {
  override val name: String = "PhoneCleanSplit"

  override def filter(r: Patient): Boolean = {
    (r.phone.toList.flatMap(_.split("\\^\\^").toList) ::: r.phone2.toList.flatMap(_.split("\\^\\^").toList))
      .exists(v =>
        v.replaceAllLiterally("-", "").toCharArray.distinct.length > 1 &&
          v.replaceAllLiterally("-", "") != "1234567890" &&
          v.replaceAllLiterally("-", "") != "0123456789")
  }

  override def groupSplit(r: Patient): Seq[String] = {
    (r.phone.toList.flatMap(_.split("\\^\\^").toList) ::: r.phone2.toList.flatMap(_.split("\\^\\^").toList))
      .filter(v =>
        v.replaceAllLiterally("-", "").toCharArray.distinct.length > 1 &&
          v.replaceAllLiterally("-", "") != "1234567890" &&
          v.replaceAllLiterally("-", "") != "0123456789")
      .map(_.replaceAllLiterally("-","")).distinct
  }
}
