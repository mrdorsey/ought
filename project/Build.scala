import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "homeHoner"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-salat" % "1.2",
      "com.typesafe" % "play-plugins-mailer_2.9.1" % "2.0.4" 
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId",
      resolvers += Resolver.sonatypeRepo("snapshots")
    )
}
