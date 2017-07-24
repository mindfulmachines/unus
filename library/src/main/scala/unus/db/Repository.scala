package unus.db

import java.sql.Timestamp
import java.time.LocalDateTime

class Repository(private val db: DbContext) {
  import db._
  private implicit val timestampEncoder = MappedEncoding[LocalDateTime, Timestamp](Timestamp.valueOf)
  private implicit val timestampDecoder = MappedEncoding[Timestamp, LocalDateTime](_.toLocalDateTime)

  def patientsExist(): Boolean = {
    run(
      quote {
        query[Patient].size
      }
    ) > 0
  }

  def getModel(model: String): Model = {
    run(
      quote {
        query[Model].filter(_.model == lift(model)).take(1)
      }
    ).head
  }

  def createBlocker(blocker: Blocker): Long = {
    run(
      quote {
        query[Blocker].insert(lift(blocker))
      }
    )
  }

}
