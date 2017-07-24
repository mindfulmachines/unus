package models
import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import controllers.BatchView
import unus.db._

class Services(val db: DbContext) {
  import db._

  implicit val timestampEncoder = MappedEncoding[LocalDateTime, Timestamp](Timestamp.valueOf)
  implicit val timestampDecoder = MappedEncoding[Timestamp, LocalDateTime](_.toLocalDateTime)

  implicit class Offset[T](q: Query[T]) {
    def randomRow =
      quote(infix"""$q OFFSET FLOOR(random() * (select count(*) from ($q) qq))""".as[Query[T]])
  }

  def getBlockedRowCount(blocker: String): Long =
    run(
      quote{query[BlockedRow].filter(_.blocker==lift(blocker)).size}
    )

  def getBlockedRowCount: Long =
    run(
      quote{query[BlockedRow].size}
    )

  def getLabelCount: Long =
    run(
      quote{
        query[Label].size
      }
    )

  def getLabelCountForBlocker(blocker: String): (Long,Long) =
    (
      run(
        quote{
          query[BlockedRow]
            .filter(_.blocker == lift(blocker))
            .join(query[Label]).on((b,l) => b.id1 == l.id1 && b.id2 == l.id2)
            .filter(_._2.valid).size
        }
      ),
      run(
        quote{
          query[BlockedRow]
            .filter(_.blocker == lift(blocker))
            .join(query[Label]).on((b,l) => b.id1 == l.id1 && b.id2 == l.id2)
            .size
        }
      )
    )

  def getBlockers: List[Blocker] = run(quote{query[Blocker]})

  def getBatches: List[BatchView] =
    run(
      quote{
        query[Label].groupBy(_.batch).map(r=>(r._1,r._2.map(r => if(r.valid) 1 else 0).sum,r._2.size,r._2.map(_.ts).max))
      }
    ).map(r=>BatchView(r._1,r._2.getOrElse(0).toLong,r._3,r._4.getOrElse(Timestamp.valueOf(LocalDateTime.MIN))))

  def getPatient(id: String): Option[Patient] =
    run(
      quote {
        query[Patient]
          .filter(c => c.enterpriseId == lift(id))
          .take(1)
      }
    ).headOption

  def randomBlockedRowUnlabeled(blocker: String): Option[BlockedRow] = {
    run(
      quote {
        querySchema[BlockedRow]("BlockedRowUnLabeled")
          .filter(_.blocker == lift(blocker))
          .randomRow
          .take(1)
      }
    ).headOption
  }

  def randomWrongPrediction(model: String, batch: String): Option[Prediction] = {
    run(
      quote {
        query[Prediction]
          .filter(c => c.model == lift(model) && c.label != c.prediction)
          .leftJoin(query[Label].filter(_.batch==lift(batch))).on((p,l) => p.id1==l.id1 && p.id2 == l.id2)
          .filter(_._2.isEmpty)
          .map(_._1)
          .randomRow
          .take(1)
      }
    ).headOption
  }

  def createLabel(batch:String, id1: String, id2: String, valid: Boolean): Long = {
    val now = Timestamp.valueOf(LocalDateTime.now())
    run(
      quote {
        query[Label]
          .insert(lift(Label(batch, id1, id2, valid, now)))
      }
    )
  }

  def createModel(model: Model): Long = {
    run(
      quote {
        query[Model]
          .insert(lift(model))
      }
    )
  }

  def getModel(model: String): Option[Model] = {
    run(
      quote {
        query[Model]
          .filter(_.model == lift(model)).take(1)
      }
    ).headOption
  }

  def getModels: List[Model] = {
    run(
      quote {
        query[Model]
      }
    )
  }

  def createSubmission(submission: Submission): Long = {
    run(
      quote {
        query[Submission]
          .insert(lift(submission))
      }
    )
  }

  def updateSubmission(submission: Submission): Long = {
    run(
      quote {
        query[Submission]
          .filter(_.submission == lift(submission.submission))
          .update(lift(submission))
      }
    )
  }

  def getSubmission(submission: String): Option[Submission] = {
    run(
      quote {
        query[Submission]
          .filter(_.submission == lift(submission)).take(1)
      }
    ).headOption
  }

  def getSubmissions: List[Submission] = {
    run(
      quote {
        query[Submission]
      }
    )
  }
}
