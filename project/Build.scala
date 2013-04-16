import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "ought"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-salat" % "1.2",
      "org.mongodb"         %% "casbah-core"     % "2.5.0",
      "com.novus"          %% "salat-core" % "1.9.2-SNAPSHOT"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId",
      resolvers += Resolver.sonatypeRepo("snapshots")
    )
}