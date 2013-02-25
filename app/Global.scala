import com.mongodb.casbah.Imports._
import play.api._
import models._
import se.radley.plugin.salat._

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    //Business.remove(MongoDBObject.empty)
    //BusinessAddress.remove(MongoDBObject.empty)

  }

  override def onStop(app: Application) {
  }
}
