package unus.helpers

import java.nio.file.{Files, Paths}

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

abstract class Cacher[T: ClassTag] {
  protected val name: String
  protected def build(): RDD[T]

  private lazy val directory: String = Conf.cacheDir + "/" + name + "/"

  lazy val value: RDD[T] = {
    if (! cached) {
      save(build())
    }
    load().cache()
  }

  protected def load(): RDD[T] = {
    Conf.spark.sparkContext.objectFile(directory)
  }

  protected def save(data: RDD[T]): Unit = {
    data.saveAsObjectFile(directory)
  }

  protected def cached: Boolean = {
    Files.exists(Paths.get(directory))
  }
}
