package unus.stage

import java.nio.file.{Files, Paths}
import java.sql.Timestamp
import java.time.LocalDateTime

import unus.blocking._
import unus.db._
import unus.helpers.Conf
import unus.model.Features
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}


object Stages {
  def saveBlockedRowToDB(data: RDD[BlockedRow]): Unit = {
    import Conf.spark.implicits._

    val size = data.count()
    val samplePerc = math.min(Conf.blockingSample/size.toDouble,1.0)
    data.sample(withReplacement = false, samplePerc, 1234L).toDS().write
      .format("jdbc")
      .option("url", Conf.dbUrl)
      .option("dbtable", "\"BlockedRow\"")
      .option("user", Conf.dbUsername)
      .option("password", Conf.dbPassword)
      .option("jdbcdriver","org.postgresql.Driver")
      .mode(SaveMode.Append)
      .save()
  }

  def savePredictionsToDB(data: Dataset[Prediction]): Unit = {
    data.write
      .format("jdbc")
      .option("url", Conf.dbUrl)
      .option("dbtable", "\"Prediction\"")
      .option("user", Conf.dbUsername)
      .option("password", Conf.dbPassword)
      .option("jdbcdriver","org.postgresql.Driver")
      .mode(SaveMode.Append)
      .save()
  }

  def loadLabeledRowFromDB(): RDD[LabeledRow] = {
    val schema = ScalaReflection.schemaFor[LabeledRow].dataType.asInstanceOf[StructType]

    import Conf.spark.implicits._

    Conf.spark.read
      .format("jdbc")
      .option("url", Conf.dbUrl)
      .option("dbtable", "(select b.blocker, b.id1, b.id2, l.batch, l.valid, l.ts from \"BlockedRow\" b join \"Label\" l on b.id1 = l.id1 and b.id2 = l.id2) t")
      .option("user", Conf.dbUsername)
      .option("password", Conf.dbPassword)
      .option("jdbcdriver","org.postgresql.Driver")
      .load()
      .as[LabeledRow]
      .rdd
  }

  def generatePairedPatients(blocked: RDD[LabeledRow], patients: RDD[Patient]): RDD[PairedPatients] = {
    val p = patients.keyBy(_.enterpriseId).cache()

    blocked.keyBy(_.id1)
      .join(p)
      .values.keyBy(_._1.id2)
      .join(p)
      .values
      .map(r => PairedPatients(r._1._1,r._1._2,r._2))
  }


  def generateTrainTest(features: RDD[Features]): Seq[RDD[(LabeledRow, LabeledPoint)]] = {
    val labeled = features.map(f => (f.blocked, new LabeledPoint(if(f.label) 1.0 else 0.0, new DenseVector(f.features)))).cache()

    val splits = labeled.randomSplit(Array(0.25,0.25,0.25,0.25))
    splits.toSeq
  }

  def trainModel(trainingData: RDD[(LabeledRow, LabeledPoint)],
                 testData: RDD[(LabeledRow, LabeledPoint)],
                 numTrees: Int,
                 maxDepth: Int): (RandomForestModel, ModelResults) = {
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val maxBins = 32
    //val model = DecisionTree.trainClassifier(trainingData.cache(), numClasses, categoricalFeaturesInfo,
    //  impurity, maxDepth, maxBins)
    //val model = new LogisticRegressionWithLBFGS()
    //    .setNumClasses(2)
    //    .run(trainingData.cache())
    val model = RandomForest.trainClassifier(trainingData.map(_._2), numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    val labelAndPreds = testData.map { point =>
      val prediction = model.predict(point._2.features)
      (prediction, point._2.label)
    }
    val metrics = new BinaryClassificationMetrics(labelAndPreds)

    val results = ModelResults(metrics.fMeasureByThreshold().collect().max._2,
      metrics.precisionByThreshold().collect().max._2,
      metrics.recallByThreshold().collect().max._2)

    /*for (b <- testData.map(_._1.blocker).distinct().collect()) {
      val labelAndPredsSub = testData.filter(_._1.blocker==b).map { point =>
        val prediction = model.predict(point._2.features)
        (prediction, point._2.label)
      }
      val metricsSub = new BinaryClassificationMetrics(labelAndPredsSub)

      println(b + "," + metricsSub.fMeasureByThreshold().collect().max._2 +
        "," + metricsSub.precisionByThreshold().collect().max._2 +
        "," + metricsSub.recallByThreshold().collect().max._2 +
        "," + labelAndPredsSub.filter(_._2 >= 0.5).count() +
        "," + labelAndPredsSub.count())
    }*/


    //println("Learned classification tree model:\n" + model.toDebugString)

    (model, results)
  }

  def generatePredictions(name: String, model: RandomForestModel,  testData: RDD[(LabeledRow, LabeledPoint)]): RDD[Prediction] = {
    testData.map { point =>
      val prediction = model.predict(point._2.features)
      Prediction(name, point._1.id1, point._1.id2, point._2.label, prediction, Timestamp.valueOf(LocalDateTime.now()))
    }
  }


  /*


    def trainModelDF(spark: SparkSession, features: RDD[(String, String, Double, Array[Double])]): DecisionTreeModel = {
      import spark.implicits._



      case class Point(
                             label: Double,
                             features: org.apache.spark.ml.linalg.DenseVector
                           )

      val labeled = features.map(f => Point(f._3, new DenseVector(f._4).asML)).toDS().cache()

      val splits = labeled.randomSplit(Array(0.7, 0.3))
      val (trainingData, testData) = (splits(0), splits(1))

      val numClasses = 2
      val categoricalFeaturesInfo = Map[Int, Int]()
      val impurity = "gini"
      val maxDepth = 8
      val numTrees = 8
      val featureSubsetStrategy = "auto" // Let the algorithm choose.
      val maxBins = 32

      val model = new RandomForestClassifier()
        .setLabelCol("label")
        .setFeaturesCol("features")
        .setNumTrees(numTrees)
        .setMaxBins(maxBins)
        .setMaxDepth(maxDepth)
        .fit(trainingData)

      case class Prediction(
                             label: Double,
                             probability: org.apache.spark.ml.linalg.Vector
                           )

      val labelAndPreds = model.transform(testData).as[Prediction].rdd.map(r => (r.probability(1),r.label))

      val metrics = new BinaryClassificationMetrics(labelAndPreds)

      val testErr = metrics.fMeasureByThreshold()
      testErr.collect().foreach(r =>
        println("Test Error = " + r._1 + "," + r._2))
      val testErr2 = metrics.precisionByThreshold()
      testErr2.collect().foreach(r =>
        println("Test Error = " + r._1 + "," + r._2))
      val testErr3 = metrics.recallByThreshold()
      testErr3.collect().foreach(r =>
        println("Test Error = " + r._1 + "," + r._2))
      println("Learned classification tree model:\n" + model.toDebugString)

      model

    }
  */

  def runModelTraining(patientCache: PatientCacher,
          blockerCaches: List[BlockerCacher],
          model: Model
         ): Seq[ModelResults] = {
    import Conf.spark.implicits._

    val data = patientCache.value

    val labeled = loadLabeledRowFromDB()
        .filter(b => model.blockers.contains(b.blocker))
        .filter(b => model.batches.contains(b.batch))
        .filter(_.ts.before(model.ts))
        .groupBy(r => (r.id1, r.id2))
        .map(_._2.toList.sortBy(_.ts.toInstant.toEpochMilli).reverse.head)

    val paired = generatePairedPatients(labeled,data)

    val features = Conf.features.filter(_.name==model.features).head.generateFeatures(paired)

    val splits = generateTrainTest(features)

    val folds = splits.map(test => (test, splits.filter(_!=test).reduce(_.union(_))))

    val results = folds.map { f =>
      val trained = trainModel(f._2, f._1, model.trees, model.depth)
      val predictions = generatePredictions(model.model, trained._1, f._1)
      savePredictionsToDB(predictions.toDS())
      trained._2
    }
    val all = splits.reduce(_.union(_))
    val trainedAll = trainModel(all,all, model.trees, model.depth)
    trainedAll._1.save(Conf.spark.sparkContext,Conf.dataDir + "/model/" + model.model)
    results
  }

  def runSubmission(repository: Repository,
                    patientCache: PatientCacher,
                    blockerCaches: List[BlockerCacher],
                    submission: Submission): Unit = {
    val data = patientCache.value

    val trained = RandomForestModel.load(Conf.spark.sparkContext,Conf.dataDir + "/model/" + submission.model)

    val allBlocked = blockerCaches
      .filter(b => submission.blockers.contains(b.blocker.name))
        .map(_.value)
        .reduce(_.union(_))
        .groupBy(r => (r.id1, r.id2))
        .map(_._2.toList.sortBy(_.ts.toInstant.toEpochMilli).reverse.head)
        .map(r => LabeledRow("",r.id1,r.id2, "", valid = false, Timestamp.valueOf(LocalDateTime.MIN)))
        .distinct()

    val paired = generatePairedPatients(allBlocked,data)

    val model = repository.getModel(submission.model)

    val allFeatures = Conf.features.filter(_.name==model.features).head.generateFeatures(paired)

    val allLabeled = allFeatures.map(f => new LabeledPoint(if(f.label) 1.0 else 0.0, new DenseVector(f.features)))

    val allPredictions = allFeatures.map { f =>
      val prediction = trained.predict(new DenseVector(f.features))
      (f.blocked.id1,f.blocked.id2,prediction)
    }.filter(_._3 >= 0.9999).collect()

    val file = allPredictions
      .map(r => r._1 + "," + r._2 + "," + r._3)
      .mkString("\n").getBytes

    Files.createDirectories(Paths.get(Conf.dataDir + "/submission/"))
    Files.write(Paths.get(Conf.dataDir + "/submission/" + submission.submission + ".csv"), file)
  }

}
