package unus.model

import unus.stage.PairedPatients
import org.apache.spark.rdd.RDD

trait FeatureBuilder {
  val name: String
  def generateFeatures(data: RDD[PairedPatients]): RDD[Features]
}
