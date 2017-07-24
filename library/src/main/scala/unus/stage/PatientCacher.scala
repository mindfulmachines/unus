package unus.stage

import unus.helpers.Conf
import unus.db.{Patient, Repository}
import unus.helpers.Cacher
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType

class PatientCacher extends Cacher[Patient] {
  override protected val name: String = "patient"

  override protected def build(): RDD[Patient] = {
    val schema = ScalaReflection.schemaFor[Patient].dataType.asInstanceOf[StructType]

    import Conf.spark.implicits._

    Conf.spark.read.format("csv")
      .schema(schema)
      .option("header", "true")
      .load(Conf.dataDir + "/FInalDataset.csv")
      .as[Patient]
      .rdd
  }
}
