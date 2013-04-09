package models

import play.api.Play.current
import java.util.{Date}
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx

case class User(
  id: ObjectId = new ObjectId,
  username: String,
  email: String,
  city: String,
  password: String,
  added: Date = new Date()
)

object User extends ModelCompanion[User, ObjectId] {
  val dao = new SalatDAO[User, ObjectId](collection = mongoCollection("users")) {}

  def findOneById(id: String): Option[User] = dao.findOne(MongoDBObject("_id" -> id))
  def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))
  def findOneByUserPass(username: String, password: String): Option[User] = dao.findOne(MongoDBObject("username" -> username, "password" -> password))
}