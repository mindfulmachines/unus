package unus.blocking
import unus.db.Patient

class LastZipBlocker extends BlockerBase {
  override val name: String = "LastZip"

  override def filter(r: Patient): Boolean = {
    r.last.nonEmpty && r.zip.nonEmpty
  }

  override def group(r: Patient): String = {
    r.last.getOrElse("") + "_" + r.zip.getOrElse("")
  }
}
