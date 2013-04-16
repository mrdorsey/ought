package domain

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import models.UserAddress
import play.api.libs.json.JsValue
import play.api.libs.ws.Response
import scala.concurrent.Future
import play.api.libs.ws.WS
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import models.User
import dto.UserAddressDto

object UserAddressManager {

	val GeocodingProviderURL = "http://maps.googleapis.com/maps/api/geocode/json?address=[ADDRESS]&sensor=[USESENSOR]"
	val useSensor = false

	def geocodeAndCreateAddress(address: UserAddress): UserAddress = {
		geocodeAddress(address)
		UserAddress.insert(address)
		UserAddress.collection.ensureIndex(MongoDBObject("location" -> "2d"))

		return address
	}

	def geocodeAndUpdateAddress(address: UserAddress): UserAddress = {
		geocodeAddress(address)
		UserAddress.save(address.copy(id = address.id))
		UserAddress.collection.ensureIndex(MongoDBObject("location" -> "2d"))

		return address
	}

	def deleteAddress(id: ObjectId): ObjectId = {
		UserAddress.remove(MongoDBObject("_id" -> id))

		return id
	}

	def findNearestUsers(center: List[Double], radius: Double): List[UserAddressDto] = {
		var businessList: ListBuffer[UserAddressDto] = ListBuffer()
		val addresses: List[UserAddress] = UserAddress.findByCenterAndRadius(center, 20)

		addresses.foreach(address => {
			val business: Option[User] = User.dao.findOne(MongoDBObject("address_id" -> address.id))
			val businessWithAddress: UserAddressDto = UserAddressDto(business.get, address)

			businessList += businessWithAddress
		})

		return businessList.toList
	}

	private def geocodeAddress(address: UserAddress): UserAddress = {
		implicit val timeout = Timeout(50000 milliseconds)

		val urlString: String = GeocodingProviderURL.replace("[ADDRESS]", addressToURLString(address)).replace("[USESENSOR]", useSensor.toString)
		val geocodeRequest = WS.url(urlString).get()
		val geocodeFuture = geocodeRequest map {
			response => response.json
		}
		val geocodeJSON = Await.result(geocodeFuture, timeout.duration).asInstanceOf[JsValue]

		val lat = ((geocodeJSON \ "results")(0) \ "geometry" \ "location" \ "lat").asOpt[Double] match {
			case Some(lat) => lat
			case None => 0
		}

		val long = ((geocodeJSON \ "results")(0) \ "geometry" \ "location" \ "lng").asOpt[Double] match {
			case Some(long) => long
			case None => 0
		}

		System.out.println(lat + ": " + long)
		address.location = List(lat, long)

		return address
	}

	private def addressToURLString(address: UserAddress): String = {
		var addressURLString: String = ""

		addressURLString = addressURLString.concat(address.line1.replaceAll(" ", "+").concat("+"))
		addressURLString = if (address.line2 != null) addressURLString.concat(address.line2.replaceAll(" ", "+").concat("+")) else addressURLString
		addressURLString = addressURLString.concat(address.city.replaceAll(" ", "+").concat("+"))
		addressURLString = addressURLString.concat(address.state.concat("+"))
		addressURLString = addressURLString.concat(address.zip)

		return addressURLString
	}
}