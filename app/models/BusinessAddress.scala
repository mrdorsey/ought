package models

import java.util.{Date}
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._

import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import com.novus.salat.global._
import java.util.Dictionary

case class BusinessAddress(
  @Key("_id") id: ObjectId = new ObjectId,
  line1: String,
  line2: String,
  city: String,
  state: String,
  zip: String,
  var location: List[Double]
)

object BusinessAddress extends ModelCompanion[BusinessAddress, ObjectId] {
  val collection = mongoCollection("businessAddresses")
  val dao = new SalatDAO[BusinessAddress, ObjectId](collection = collection) {}

  val columns = List("dummy", "_id", "line1", "line2", "city", "state", "zip")
  
  def findByCenterAndRadius(center: List[Double], radius: Double): List[BusinessAddress] = {
	val query: DBObject = MongoDBObject("location" -> MongoDBObject("$near" -> List(center(0), center(1))))
		
	return dao.find(query).toList
  }
  
  implicit val businessAddressWrites: Writes[BusinessAddress] = (
    (__ \ "_id").write[ObjectId] and
    (__ \ "line1").write[String] and
    (__ \ "line2").write[String] and
    (__ \ "city").write[String] and
    (__ \ "state").write[String] and
    (__ \ "zip").write[String] and
  )(unlift(BusinessAddress.unapply)) 
}