package controllers

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject

import models.Services
import unus.db._
import unus.model.FeatureBuilder
import unus.stage.{BlockerCacher, PairedPatients, PatientCacher, Stages}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.data.format.Formats._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._

case class LabelBlockerForm(batch: String, id1:String, id2: String, valid: Boolean)
case class BlockerForm(blockers: List[String])

case class ModelForm(model: String, blockers: List[String], batches: List[String], features: String, trees: Int, depth: Int)

case class SubmissionForm(submission: String, model: String, blockers: List[String], f1: Option[BigDecimal], precision: Option[BigDecimal], recall: Option[BigDecimal])

case class BlockerView(name: String, blocker: Option[Blocker], rowsDB: Long, validLabeledDB: Long, totalLabeledDB: Long)
case class BatchView(batch: String, valid: Long, total: Long, ts: Timestamp)

class MainController @Inject()(services: Services,
                               repo: Repository,
                               patientCacher: PatientCacher,
                               blockerCachers: List[BlockerCacher],
                               features: List[FeatureBuilder])(cc: ControllerComponents) extends AbstractController(cc)  with play.api.i18n.I18nSupport {
  implicit val userWrites: Writes[Patient] = Json.writes[Patient]
  implicit val blockedWrites: Writes[BlockedRow] = Json.writes[BlockedRow]
  implicit val labeledWrites: Writes[LabeledRow] = Json.writes[LabeledRow]

  val labelBlockerForm = Form(
    mapping(
      "batch" -> text.verifying(nonEmpty),
      "id1" -> text,
      "id2" -> text,
      "valid" -> boolean
    )(LabelBlockerForm.apply)(LabelBlockerForm.unapply)
  )

  val blockerForm = Form(
    mapping(
      "blockers" -> list(text)
    )(BlockerForm.apply)(BlockerForm.unapply)
  )

  val modelForm = Form(
    mapping(
      "model" -> text.verifying(nonEmpty),
      "blockers" -> list(text),
      "batches" -> list(text),
      "features" -> text,
      "trees" -> number,
      "depth" -> number
    )(ModelForm.apply)(ModelForm.unapply)
  )

  import play.api.data.format.Formats._

  val submissionForm = Form(
    mapping(
      "submission" -> text.verifying(nonEmpty),
      "model" -> text,
      "blockers" -> list(text),
      "f1" -> default(optional(bigDecimal),None),
      "precision" -> default(optional(bigDecimal),None),
      "recall" -> default(optional(bigDecimal),None)
    )(SubmissionForm.apply)(SubmissionForm.unapply)
  )

  def index = Action {
    Ok(views.html.index(services.getBlockers.map(_.blocker)))
  }

  def labelBlocker(blocker: String) = Action(parse.form(labelBlockerForm)) { implicit request =>
    services.createLabel(request.body.batch, request.body.id1, request.body.id2, request.body.valid)
    Redirect(controllers.routes.MainController.comparisonBlocker(blocker, request.body.batch))
  }

  def comparisonBlocker(blocker: String, batch: String) = Action { implicit request =>
    val result = for {
      b <- services.randomBlockedRowUnlabeled(blocker)
      p1 <- services.getPatient(b.id1)
      p2 <- services.getPatient(b.id2)
    } yield {
      (b,p1,p2)
    }
    result match {
      case None => Ok("")
      case Some((b,p1,p2)) => Ok(views.html.comparisonBlocker(batch, b,p1,p2, labelBlockerForm))
    }
  }


  def labelPrediction(model: String) = Action(parse.form(labelBlockerForm)) { implicit request =>
    services.createLabel(request.body.batch, request.body.id1, request.body.id2, request.body.valid)
    Redirect(controllers.routes.MainController.comparisonPrediction(model, request.body.batch))
  }

  def comparisonPrediction(model: String, batch: String) = Action { implicit request =>
    val result = for {
      p <- services.randomWrongPrediction(model, batch)
      p1 <- services.getPatient(p.id1)
      p2 <- services.getPatient(p.id2)
    } yield {
      (p,p1,p2)
    }
    result match {
      case None => Ok("")
      case Some((p,p1,p2)) => Ok(views.html.comparisonPrediction(batch,p,p1,p2, labelBlockerForm))
    }
  }


  def submissions = Action {implicit request =>
    Ok(views.html.submissionsView(services.getSubmissions))
  }

  def submissionView(submission : String) = Action {implicit request =>
    services.getSubmission(submission) match {
      case None => Redirect(controllers.routes.MainController.submissions())
      case Some(s) => Ok(views.html.submissionView(
        getBlockerViews,
        s.submission,
        s.model,
        s.blockers.toSet,
        s.f1.map(BigDecimal.apply),
        s.precision.map(BigDecimal.apply),
        s.recall.map(BigDecimal.apply))
      )
    }
  }

  def submissionCreateView() = Action {implicit request =>
    Ok(views.html.submissionCreate(getBlockerViews, services.getModels.map(_.model)))
  }

  def submissionCloneView(submission: String) = Action {implicit request =>
    services.getSubmission(submission) match {
      case None => Ok(views.html.submissionCreate(getBlockerViews, services.getModels.map(_.model)))
      case Some(s) => Ok(views.html.submissionCreate(getBlockerViews, services.getModels.map(_.model),
        "", s.model, s.blockers.toSet, s.f1.map(BigDecimal.apply), s.precision.map(BigDecimal.apply), s.recall.map(BigDecimal.apply)))
    }
  }

  def submissionCreate() = Action(parse.form(submissionForm)) { implicit request =>
    val b= request.body
    if(services.getSubmission(b.submission).nonEmpty) {
      Redirect(controllers.routes.MainController.submissionCreateView())
    } else {
      val s = Submission(b.submission, b.model, b.blockers, Timestamp.valueOf(LocalDateTime.now()),b.f1.map(_.toDouble), b.precision.map(_.toDouble), b.recall.map(_.toDouble))
      Stages.runSubmission(repo, patientCacher, blockerCachers, s)
      services.createSubmission(s)
      Redirect(controllers.routes.MainController.submissionView(b.submission))
    }
  }

  def submissionUpdate() = Action(parse.form(submissionForm)) { implicit request =>
    val b= request.body
    val s = Submission(b.submission, b.model, b.blockers, Timestamp.valueOf(LocalDateTime.now()),b.f1.map(_.toDouble), b.precision.map(_.toDouble), b.recall.map(_.toDouble))
    services.updateSubmission(s)
    Redirect(controllers.routes.MainController.submissionView(b.submission))
  }

  def models = Action {implicit request =>
    Ok(views.html.modelsView(services.getModels))
  }

  def modelView(model : String) = Action {implicit request =>
    services.getModel(model) match {
      case None => Redirect(controllers.routes.MainController.models())
      case Some(m) => Ok(views.html.modelView(getBlockerViews, services.getBatches, features.map(_.name), m))
    }
  }

  def modelCreateView() = Action {implicit request =>
    Ok(views.html.modelCreate(getBlockerViews, services.getBatches, features.map(_.name)))
  }

  def modelCloneView(model: String) = Action {implicit request =>
    services.getModel(model) match {
      case None => Ok(views.html.modelCreate(getBlockerViews, services.getBatches, features.map(_.name)))
      case Some(m) => Ok(views.html.modelCreate(getBlockerViews, services.getBatches, features.map(_.name),
        m.trees, m.depth, model, m.blockers.toSet, m.batches.toSet, Set(m.features)))
    }
  }

  def modelCreate() = Action(parse.form(modelForm)) { implicit request =>
    val b= request.body
    if(services.getModel(b.model).nonEmpty) {
      Redirect(controllers.routes.MainController.modelCreateView())
    } else {
      val model = Model(b.model, Timestamp.valueOf(LocalDateTime.now()), b.features, b.blockers, b.batches, b.trees, b.depth, -1.0, -1.0, -1.0)
      val result = Stages.runModelTraining(patientCacher, blockerCachers, model)
      val trained = model.copy(f1 = result.map(_.f1).sum / result.length.toDouble,
        precision = result.map(_.precision).sum / result.length.toDouble,
        recall = result.map(_.accuracy).sum / result.length.toDouble
      )
      services.createModel(trained)
      Redirect(controllers.routes.MainController.modelView(b.model))
    }
  }

  def runBlockers = Action(parse.form(blockerForm)) { implicit request =>
    request.body.blockers.foreach(b =>
      blockerCachers.filter(_.blocker.name == b).foreach {
        bc =>
          val rdd = bc.value
          if(services.getBlockedRowCount(bc.blocker.name) == 0) {
            Stages.saveBlockedRowToDB(rdd)
          }
      }
    )
    Redirect(controllers.routes.MainController.blockers())
  }

  def blockers = Action { implicit request =>

    Ok(views.html.blockers(getBlockerViews, blockerForm))
  }

  def getBlockerViews: List[BlockerView] = {
    val blockersInDB = services.getBlockers.map(r => (r.blocker, r)).toMap
    val blockersInCode = blockerCachers.map(_.blocker.name).toSet
    val blockers = blockersInDB.keySet.union(blockersInCode)

    blockers.toList.map(
      b => {
        val labeled = services.getLabelCountForBlocker(b)
        BlockerView(
          b,
          blockersInDB.get(b),
          services.getBlockedRowCount(b),
          labeled._1,
          labeled._2
        )
      }
    )
  }

}
