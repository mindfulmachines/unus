package unus.stage

import unus.db.{Patient, Repository}
import unus.helpers.{Cacher, Conf}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType

class PatientDatabase(repository: Repository, patients: PatientCacher) {
  def run(): Unit = {
    if(! repository.patientsExist()) {
      import Conf.spark.implicits._
      patients.value.toDS().write
        .format("jdbc")
        .option("url", Conf.dbUrl)
        .option("dbtable", "\"Patient\"")
        .option("user", Conf.dbUsername)
        .option("password", Conf.dbPassword)
        .option("truncate", "true")
        .option("jdbcdriver", "org.postgresql.Driver")
        .mode(SaveMode.Overwrite)
        .save()
    }
  }
}
