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

case class UserAddress(
	@Key("_id") id: ObjectId = new ObjectId,
	line1: String,
	line2: String,
	city: String,
	state: String,
	zip: String,
	homeType: String,
	size: String,
	var location: List[Double],
	services: List[ObjectId] = List[ObjectId]())

object UserAddress extends ModelCompanion[UserAddress, ObjectId] {
	val collection = mongoCollection("userAddresses")
	val dao = new SalatDAO[UserAddress, ObjectId](collection = collection) {}

	val columns = List("dummy", "_id", "line1", "line2", "city", "state", "zip", "homeType", "size", "location")

	implicit val userAddressWrites = new Writes[UserAddress] {
		def writes(ua: UserAddress): JsValue = {
			Json.obj(
				"id" -> ua.id.toString,
				"line1" -> ua.line1,
				"line2" -> ua.line2,
				"city" -> ua.city,
				"state" -> ua.state,
				"zip" -> ua.zip,
				"type" -> ua.homeType,
				"size" -> ua.size,
				"location" -> (ua.location(0) + ", " + ua.location(1)),
				"services" -> Json.arr(ua.services.map(_.toString())))
		}
	}

	def findByCenterAndRadius(center: List[Double], radius: Double): List[UserAddress] = {
		val query: DBObject = MongoDBObject("location" -> MongoDBObject("$near" -> List(center(0), center(1))))

		return dao.find(query).toList
	}
}