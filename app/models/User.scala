package models

import play.api.Play.current
import java.util.{ Date }
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx
import play.api.libs.json._
import salatcontext._

case class User(
	id: ObjectId = new ObjectId,
	username: String,
	email: String,
	password: String,
	@Key("address_id") addressId: ObjectId = new ObjectId,
	created: Date = new Date())

object User extends ModelCompanion[User, ObjectId] {
	val dao = new SalatDAO[User, ObjectId](collection = mongoCollection("users")) {}

	implicit val userWrites = new Writes[User] {
		def writes(u: User): JsValue = {
			Json.obj(
				"id" -> u.id.toString(),
				"username" -> u.username,
				"email" -> u.email,
				"addressId" -> u.addressId.toString(),
				"created" -> u.created)
		}
	}

	def findOneById(id: String): Option[User] = dao.findOne(MongoDBObject("_id" -> id))
	def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))
	def findOneByUserPass(username: String, password: String): Option[User] = dao.findOne(MongoDBObject("username" -> username, "password" -> password))
}