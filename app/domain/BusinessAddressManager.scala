package domain

import models.BusinessAddress
import play.api.libs.ws._
import play.api.libs.concurrent.Promise
import play.api.libs.json.JsValue
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

object BusinessAddressManager {

  val GeocodingProviderURL = "http://maps.googleapis.com/maps/api/geocode/json?address=[ADDRESS]&sensor=[USESENSOR]"
  val useSensor = false

  def geocodeAndCreateAddress(address: BusinessAddress): BusinessAddress = {
    geocodeAddress(address)
    BusinessAddress.insert(address)
    BusinessAddress.collection.ensureIndex(MongoDBObject("location" -> "2d"))
    
    return address
  }
  
  def geocodeAndUpdateAddress(address: BusinessAddress): BusinessAddress = {
    geocodeAddress(address)
    BusinessAddress.save(address.copy(id = address.id))
    BusinessAddress.collection.ensureIndex(MongoDBObject("location" -> "2d"))
    
    return address
  }
  
  def deleteAddress(id: ObjectId): ObjectId = {
    BusinessAddress.remove(MongoDBObject("_id" -> id))
    
    return id
  }

  private def geocodeAddress(address: BusinessAddress): BusinessAddress = {

    val urlString: String = GeocodingProviderURL.replace("[ADDRESS]", addressToURLString(address)).replace("[USESENSOR]", useSensor.toString)
    val geocodeResp: Promise[Response] = WS.url(urlString).get()
    val geocodeJSON: JsValue = geocodeResp.await.get.json

    val lat = ((geocodeJSON \ "results")(0) \ "geometry" \ "location" \ "lat").asOpt[Double] match {
      case Some(lat) => lat
      case None => 0
    }

    val long = ((geocodeJSON \ "results")(0) \ "geometry" \ "location" \ "lng").asOpt[Double] match {
      case Some(long) => long
      case None => 0
    }

    address.location = List(lat, long)

    return address
  }

  private def addressToURLString(address: BusinessAddress): String = {
    var addressURLString: String = ""

    addressURLString = addressURLString.concat(address.line1.replaceAll(" ", "+").concat("+"))
    addressURLString = if (address.line2 != null) addressURLString.concat(address.line2.replaceAll(" ", "+").concat("+")) else addressURLString
    addressURLString = addressURLString.concat(address.city.replaceAll(" ", "+").concat("+"))
    addressURLString = addressURLString.concat(address.state.concat("+"))
    addressURLString = addressURLString.concat(address.zip)

    return addressURLString
  }
}