package models

import java.util.{Date}
import play.api.Play.current
import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import com.novus.salat.global._

case class BusinessAddress(
  @Key("_id") id: ObjectId = new ObjectId,
  line1: String,
  line2: String,
  city: String,
  state: String,
  zip: String
)

object BusinessAddress extends ModelCompanion[BusinessAddress, ObjectId] {
  val collection = mongoCollection("businessAddresses")
  val dao = new SalatDAO[BusinessAddress, ObjectId](collection = collection) {}

  val columns = List("dummy", "_id", "line1", "line2", "city", "state", "zip")
  
}