package models

import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.Imports.ObjectId
import com.novus.salat.annotations.Key
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx

import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Writes._
import se.radley.plugin.salat.mongoCollection
import com.mongodb.DBObject
import salatcontext._

case class Service(
	@Key("_id") id: ObjectId = new ObjectId,
	name: String)

object Service extends ModelCompanion[Service, ObjectId] {
	val collection = mongoCollection("services")
	val dao = new SalatDAO[Service, ObjectId](collection = collection) {}

	val columns = List("_id", "name")

	implicit val serviceWriter = new Writes[Service] {
		def writes(s: Service): JsValue = {
			Json.obj(
				"id" -> s.id.toString(),
				"name" -> s.name)
		}
	}
}
