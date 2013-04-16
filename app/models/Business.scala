package models

import java.util.Date

import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.Imports.ObjectId
import com.novus.salat.annotations.Key
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx

import play.api.Play.current
import play.api.libs.json._
import se.radley.plugin.salat.mongoCollection
import com.mongodb.DBObject
import salatcontext._

case class Business(
	@Key("_id") id: ObjectId = new ObjectId,
	name: String,
	contactEmail: String,
	website: String,
	established: Option[Date] = None,
	addressId: ObjectId = new ObjectId,
	services: List[ObjectId] = List[ObjectId]())

object Business extends ModelCompanion[Business, ObjectId] {
	val collection = mongoCollection("businesses")
	val dao = new SalatDAO[Business, ObjectId](collection = collection) {}

	val columns = List("dummy", "_id", "name", "established", "address_id")

	implicit val businessWrites = new Writes[Business] {
		def writes(b: Business): JsValue = {
			Json.obj(
				"id" -> b.id.toString(),
				"name" -> b.name,
				"website" -> b.website,
				"established" -> b.established,
				"addressId" -> b.addressId.toString(),
				"services" -> Json.arr(b.services.map(_.toString())))
		}
	}

	def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[Business] = {

		val where = if (filter == "") MongoDBObject.empty else MongoDBObject("name" -> (""".*""" + filter + """.*""").r)
		val ascDesc = if (orderBy > 0) 1 else -1
		val order = MongoDBObject(columns(orderBy.abs) -> ascDesc)

		val totalRows = count(where);
		val offset = pageSize * page
		val businesses = find(where).sort(order).limit(pageSize).skip(offset).toSeq

		Page(businesses, page, offset, totalRows)
	}

	def findByAddressIds(addressIds: List[ObjectId]): List[Business] = {
		val query: DBObject = MongoDBObject("address_id" -> MongoDBObject("$all" -> addressIds))

		return dao.find(query).toList
	}

}

