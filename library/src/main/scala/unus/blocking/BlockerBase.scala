package unus.blocking

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import unus.db.{BlockedRow, Patient}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

case class GroupedRow(
                     key: String,
                     id: String,
                     patient: Patient
                     )

trait BlockerBase extends Serializable {
  val name: String

  def filterPair(p1: Patient, p2: Patient): Boolean = true
  def filter(r: Patient): Boolean
  def group(r: Patient): String = {""}
  def groupSplit(r: Patient): Seq[String] = {
    group(r) :: Nil
  }

  def anon1(r: Patient) = {
    groupSplit(r).map(rr => (rr, r))
  }

  def anon2(kv: (String , Iterable[Patient])) = {
    kv._2.map(r => GroupedRow(kv._1, r.enterpriseId, r))
  }

  def apply(rdd: RDD[Patient]): RDD[BlockedRow] = {
    val now = Timestamp.valueOf(LocalDateTime.now())
    val grouped = rdd.filter(r => filter(r))
        .flatMap(anon1)
        .groupByKey()
        .flatMap(anon2).keyBy(_.key).cache()

    grouped.join(grouped)
      .filter(r => r._2._1.id < r._2._2.id)
      .filter(r => filterPair(r._2._1.patient, r._2._2.patient))
      .map(r=> BlockedRow(name, r._2._1.id,r._2._2.id, now))
      .distinct()
  }
}
