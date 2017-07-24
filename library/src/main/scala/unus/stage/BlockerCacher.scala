package unus.stage

import java.sql.Timestamp
import java.time.LocalDateTime

import unus.blocking.BlockerBase
import unus.db.{BlockedRow, Patient, Repository}
import unus.helpers.Cacher
import org.apache.spark.rdd.RDD

class BlockerCacher(repo: Repository, patients: RDD[Patient], val blocker: BlockerBase) extends Cacher[BlockedRow] {
  override protected val name: String = "blocker/" + blocker.name

  override protected def build(): RDD[BlockedRow] = {
    blocker(patients).cache()
  }
  override protected def save(data: RDD[BlockedRow]): Unit = {
    super.save(data)
    repo.createBlocker(
      unus.db.Blocker(
        blocker.name,
        data.count(),
        Timestamp.valueOf(LocalDateTime.now())
      )
    )
  }
}
