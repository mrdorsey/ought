package controllers

import org.bson.types.ObjectId
import domain._
import models._
import views._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import dto.BusinessAddressDto
import org.bson.BSON

object Businesses extends Controller {

	def businessForm(id: ObjectId = new ObjectId, addressId: ObjectId = new ObjectId) = Form(

		mapping(
			"id" -> ignored(id),
			"name" -> nonEmptyText,
			"contactEmail" -> email,
			"website" -> text,
			"established" -> optional(date("yyyy-MM-dd")),
			"addressId" -> ignored(addressId),
			"services" -> list(mapping(
				"serviceId" -> nonEmptyText) {
					(serviceId) => new ObjectId(serviceId)
				} {
					serviceId => Some(serviceId.toString())
				}))(Business.apply)(Business.unapply))

	def addressForm(id: ObjectId = new ObjectId) = Form(
		mapping(
			"id" -> ignored(id),
			"line1" -> nonEmptyText,
			"line2" -> text,
			"city" -> nonEmptyText,
			"state" -> nonEmptyText,
			"zip" -> nonEmptyText) {
				(id, line1, line2, city, state, zip) => BusinessAddress(id, line1, line2, city, state, zip, null)
			} {
				address => Some(address.id, address.line1, address.line2, address.city, address.state, address.zip)
			})

	def signup = Action {
		Ok(html.signup.business(businessForm(), addressForm()))
	}

	def search = Action {
		Ok(html.search.businessSearch())
	}

	def create = Action { implicit request =>
		businessForm().bindFromRequest.fold(
			formWithErrors => BadRequest(html.signup.business(formWithErrors, addressForm())),
			business => {
				BusinessManager.create(business)
				addressForm(business.addressId).bindFromRequest.fold(
					formWithErrors => BadRequest(html.signup.business(businessForm().fill(business), formWithErrors)),
					address => {
						BusinessAddressManager.geocodeAndCreateAddress(address)
						Ok(html.signup.businessSummary(business, address))
					})
			})
	}

	def update() = Action { implicit request =>
		val businessId: ObjectId = new ObjectId(request.body.asFormUrlEncoded.get("business_id").head)
		val addressId: ObjectId = new ObjectId(request.body.asFormUrlEncoded.get("address_id").head)

		businessForm(businessId).bindFromRequest.fold(
			formWithErrors => BadRequest(html.signup.business(formWithErrors, addressForm())),
			business => {
				BusinessManager.update(business)
				addressForm(addressId).bindFromRequest.fold(
					formWithErrors => BadRequest(html.signup.business(businessForm().fill(business), formWithErrors)),
					address => {
						BusinessAddressManager.geocodeAndUpdateAddress(address)
						Ok(html.signup.businessSummary(business, address))
					})
			})
	}

	def findNearestBusinesses() = Action { implicit request =>
		request.body.asFormUrlEncoded match {
			case Some(map) => {
				val location: List[Double] = List(map.get("latitude").get.head.toDouble, map.get("longitude").get.head.toDouble)
				val businesses: List[BusinessAddressDto] = BusinessAddressManager.findNearestBusinesses(location, 20)

				Ok(Json.toJson(businesses))
			}
			case None =>
				BadRequest("Error retrieving businesses.")
		}
	}

	def profile(businessId: ObjectId, addressId: ObjectId) = Action { request =>
		var business: Business = null
		var address: BusinessAddress = null

		if (businessId == null || addressId == null) {
			BadRequest("Error retriving business information.")
		}

		if (businessId != null) {
			Business.findOneById(businessId) match {
				case Some(b) => business = b
				case None => BadRequest("Error retriving business information.")
			}
		}

		if (addressId != null) {
			BusinessAddress.findOneById(addressId) match {
				case Some(a) => address = a
				case None => BadRequest("Error retriving business information.")
			}
		}

		Ok(html.business.profile(business, address))
	}

}