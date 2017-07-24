package unus.blocking

import com.rockymadden.stringmetric.phonetic.RefinedSoundexAlgorithm
import unus.db.Patient
import unus.model.Helpers

class SoundexLastFirstFilteredBlocker extends BlockerBase {
  override val name: String = "SoundexLastFirstFilteredBlocker"

  override def filterPair(p1: Patient, p2: Patient): Boolean = {
    (p1.first != p2.first || p1.last != p2.last) &&
    (p1.ssn.isEmpty || p2.ssn.isEmpty || Helpers.hamm(p1.ssn, p2.ssn) <=2) &&
      (p1.dob.isEmpty || p2.dob.isEmpty || Helpers.hamm(p1.dob, p2.dob) <=2)
  }

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.first.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.flatMap(RefinedSoundexAlgorithm.compute).getOrElse("") + "_" + r.first.flatMap(RefinedSoundexAlgorithm.compute).getOrElse("")
  }
}
