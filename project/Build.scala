import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "homeHoner"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "net.liftweb" %% "lift-json" % "2.4-M5",
      "org.scalaj" %% "scalaj-http" % "0.3.6",
      "se.radley" %% "play-plugins-salat" % "1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"  
    )

}
