package models

import java.util.{ Date }
import play.api.Play.current
import play.api.libs.json._
import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import com.novus.salat.global._
import java.util.Dictionary
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import salatcontext._

case class BusinessAddress(
	@Key("_id") id: ObjectId = new ObjectId,
	line1: String,
	line2: String,
	city: String,
	state: String,
	zip: String,
	var location: List[Double])

object BusinessAddress extends ModelCompanion[BusinessAddress, ObjectId] {
	val collection = mongoCollection("businessAddresses")
	val dao = new SalatDAO[BusinessAddress, ObjectId](collection = collection) {}

	val columns = List("dummy", "_id", "line1", "line2", "city", "state", "zip")

	implicit val businessAddressWrites = new Writes[BusinessAddress] {
		def writes(ba: BusinessAddress): JsValue = {
			Json.obj(
				"id" -> ba.id.toString,
				"line1" -> ba.line1,
				"line2" -> ba.line2,
				"city" -> ba.city,
				"state" -> ba.state,
				"zip" -> ba.zip,
				"location" -> (ba.location(0) + ", " + ba.location(1)))
		}
	}

	def findByCenterAndRadius(center: List[Double], radius: Double): List[BusinessAddress] = {
		val query: DBObject = MongoDBObject("location" -> MongoDBObject("$near" -> List(center(0), center(1))))

		return dao.find(query).toList
	}
}