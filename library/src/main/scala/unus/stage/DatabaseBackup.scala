package unus.stage

import unus.helpers.Conf
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import scala.reflect.runtime.universe._
import org.apache.spark.sql.SaveMode

class DatabaseBackup[T: TypeTag](table: String) {
  private lazy val schema = ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]

  def save(): Unit = {
    Conf.spark.read
      .format("jdbc")
      .option("url", Conf.dbUrl)
      .option("dbtable", s""""$table"""")
      .option("user", Conf.dbUsername)
      .option("password", Conf.dbPassword)
      .option("jdbcdriver","org.postgresql.Driver")
      .load()
      .write
      .format("csv")
      .option("header", "true")
      .save(Conf.dataDir + "/" + table + ".csv")
  }

  def load(): Unit = {
    Conf.spark.read
      .format("csv")
      .option("header", "true")
      .schema(schema)
      .load(Conf.dataDir + "/" + table + ".csv.gz")
      .write
      .format("jdbc")
      .option("url", Conf.dbUrl)
      .option("dbtable", s""""$table"""")
      .option("user", Conf.dbUsername)
      .option("password", Conf.dbPassword)
      .option("jdbcdriver","org.postgresql.Driver")
      .mode(SaveMode.Append)
      .save()
  }
}
