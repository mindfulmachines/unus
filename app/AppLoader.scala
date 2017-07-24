import java.io.Closeable
import javax.sql.DataSource

import controllers.MainController
import io.getquill._
import models.Services
import unus.db._
import unus.helpers.Conf
import unus.model.FeatureBuilder
import unus.stage.{BlockerCacher, DatabaseBackup, PatientCacher, PatientDatabase}
import org.flywaydb.play.FlywayPlayComponents
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.inject.{Injector, NewInstanceInjector, SimpleInjector}
import play.api.routing.Router
import play.filters.csrf._
import router.Routes

class AppLoader extends ApplicationLoader {
  override def load(context: Context): Application = new BuiltInComponentsFromContext(context)
    with DBComponents with HikariCPComponents  with CSRFComponents
    with FlywayPlayComponents with play.filters.HttpFiltersComponents with _root_.controllers.AssetsComponents {

    lazy val db = new PostgresJdbcContext[PostgresEscape](dbApi.database("default").dataSource.asInstanceOf[DataSource with Closeable])

    lazy val services = new Services(db)
    lazy val patientCacher = new PatientCacher
    lazy val repo = new Repository(db)
    lazy val blockerCachers = Conf.blockers.map(new BlockerCacher(repo, patientCacher.value, _))
    lazy val features =  Conf.features
    lazy val controller = new MainController(services, repo, patientCacher, blockerCachers, features)(controllerComponents)

    lazy val router: Router = new Routes(httpErrorHandler, controller)

    /*val router: Router = Router.from {
      case GET(p"/") => controller.index
      case GET(p"/comparison/$blocker") => controller.comparison(blocker)
      case GET(p"/comparison/" ? q"blocker=$blocker") => controller.comparison(blocker)
      case POST(p"/comparison") => controller.labelBlocker
    }*/

    override lazy val injector: Injector =
      new SimpleInjector(NewInstanceInjector) + cookieSigner + csrfTokenSigner + httpConfiguration + tempFileCreator + router

    override lazy val httpFilters = Seq(csrfFilter)

    flywayPlayInitializer

    new PatientDatabase(repo, patientCacher).run()

    if(services.getLabelCount == 0) {
      new DatabaseBackup[Label]("Label").load()
    }

    if(services.getBlockedRowCount == 0) {
      new DatabaseBackup[BlockedRow]("BlockedRow").load()
    }

  }.application
}
