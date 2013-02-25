package domain

import models.BusinessAddress
import play.api.libs.ws._
import play.api.libs.concurrent.Promise
import play.api.libs.json.JsValue
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import models.Business
import org.bson.types.ObjectId
import dto.BusinessWithAddressDto
import scala.collection.mutable.ListBuffer

object BusinessManager {

  def create(business: Business): Business = {
    Business.insert(business)
    
    return business
  }
  
  def update(business: Business): Business = {
    Business.save(business.copy(id = business.id))
       
    return business
  }
  
  def delete(id: ObjectId): ObjectId = { 
    Business.remove(MongoDBObject("_id" -> id))
    
    return id
  }
  
  def findNearestBusinesses(center: List[Double], radius: Double): List[BusinessWithAddressDto] = {
	var businessList: ListBuffer[BusinessWithAddressDto] = ListBuffer()
    val addresses: List[BusinessAddress] = BusinessAddress.findByCenterAndRadius(center, 20)
        
    addresses.foreach( address => {
      val business: Option[Business] = Business.dao.findOne(MongoDBObject("address_id" -> address.id))
      val businessWithAddress: BusinessWithAddressDto = BusinessWithAddressDto(business.get, address)
               
      businessList += businessWithAddress
     })
     
     return businessList.toList
  }
}