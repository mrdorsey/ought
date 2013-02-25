package models

import java.util.Date
import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.Imports.ObjectId
import com.novus.salat.annotations.Key
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx
import play.api.Play.current
import se.radley.plugin.salat.mongoCollection
import com.mongodb.DBObject

case class Business(
  @Key("_id") id: ObjectId = new ObjectId,
  name: String,
  website: String,
  established: Option[Date] = None,
  @Key("address_id") addressId: ObjectId = new ObjectId)

object Business extends ModelCompanion[Business, ObjectId] {
  val collection = mongoCollection("businesses")
  val dao = new SalatDAO[Business, ObjectId](collection = collection) {}

  val columns = List("dummy", "_id", "name", "established", "address_id")

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


