package unus.helpers

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import unus.blocking._
import unus.model.{FeatureBuilder, FeatureBuilderV1}

object Conf {

  private val conf: Config = ConfigFactory.load

  val dataDir: String = "data/"

  val cacheDir: String = "cache/"

  val dbUrl: String = conf.getString("db.default.url")

  val dbUsername: String = conf.getString("db.default.username")

  val dbPassword: String = conf.getString("db.default.password")

  val blockingSample = 5000

  val features: List[FeatureBuilder] = List(
    new FeatureBuilderV1()
  )
  val blockers: List[BlockerBase] = List(
    new DOBFiltered2Blocker(),
    new DOBFilteredBlocker(),
    new EmailBlocker(),
    new EmailCleanBlocker(),
    new EmailCleanSSNBlocker(),
    new FirstAddressFilteredBlocker(),
    new FirstDOBBlocker(),
    new FirstDOBSimilarBlocker(),
    new FirstMRNBlocker(),
    new FirstMRNFilteredBlocker(),
    new FirstSSNBlocker(),
    new LastDOBBlocker(),
    new LastDOBYearBlocker(),
    new LastDOBYearGenderBlocker(),
    new LastFirstBlocker(),
    new LastFirstFilteredBlocker(),
    new LastSSNBlocker(),
    new LastZipBlocker(),
    new MothersMaidenNameBlocker(),
    new MothersMaidenNameFiltered2Blocker(),
    new MothersMaidenNameFilteredBlocker(),
    new NameDOBBlocker(),
    new PhoneBlocker(),
    new PhoneCleanBlocker(),
    new PhoneCleanSplitBlocker(),
    new SoundexLastFirstFilteredBlocker(),
    new SSNBlocker(),
    new SSNCleanBlocker()
  )

  lazy val spark: SparkSession = SparkSession
      .builder()
      .appName("PatientMatcher")
      .master("local[*]")
      .getOrCreate()
}
