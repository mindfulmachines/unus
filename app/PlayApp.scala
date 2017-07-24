import play.api.{Application, ApplicationLoader, Environment, Mode, Play}
import play.core.server.{ServerConfig, ServerProvider}

object PlayApp extends App {

  val config = ServerConfig(mode = Mode.Dev)

  val application: Application = {
    val environment = Environment(config.rootDir, this.getClass.getClassLoader, Mode.Dev)
    val context = ApplicationLoader.createContext(environment)
    val loader = ApplicationLoader(context)
    loader.load(context)
  }

  Play.start(application)

  val serverProvider: ServerProvider = ServerProvider.fromConfiguration(this.getClass.getClassLoader, config.configuration)
  serverProvider.createServer(config, application)
}