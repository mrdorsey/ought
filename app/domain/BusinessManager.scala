package domain

import models.BusinessAddress
import play.api.libs.ws._
import play.api.libs.concurrent.Promise
import play.api.libs.json.JsValue
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import models.Business
import org.bson.types.ObjectId

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
}