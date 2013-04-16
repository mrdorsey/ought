package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.bson.types.ObjectId
import domain.UserAddressManager
import domain.UserManager
import models.User
import models.UserAddress
import views._
import dto.UserAddressDto

object Users extends Controller {

	def userForm(id: ObjectId = new ObjectId, addressId: ObjectId = new ObjectId) = Form(

		mapping(
			"username" -> text(minLength = 4),
			"email" -> email,

			// Create a tuple mapping for the password/confirm
			"password" -> tuple(
				"main" -> text(minLength = 6),
				"confirm" -> text).verifying(
					// Add an additional constraint: both passwords must match
					"Passwords don't match", passwords => passwords._1 == passwords._2),

			"accept" -> checked("You must accept the conditions"),
			"addressId" -> ignored(addressId)) {
				(username, email, passwords, _, addressId) => User(new ObjectId, username, email, passwords._1, addressId)
			} {
				user => Some(user.username, user.email, (user.password, ""), false, user.addressId)
			}.verifying(
				"This username is not available",
				user => !Seq("admin", "guest").contains(user.username)))

	def addressForm(id: ObjectId = new ObjectId) = Form(
		mapping(
			"id" -> ignored(id),
			"line1" -> nonEmptyText,
			"line2" -> text,
			"city" -> nonEmptyText,
			"state" -> nonEmptyText,
			"zip" -> nonEmptyText,
			"homeType" -> nonEmptyText,
			"size" -> nonEmptyText,
			"services" -> list(mapping(
				"serviceId" -> nonEmptyText) {
					(serviceId) => new ObjectId(serviceId)
				} {
					serviceId => Some(serviceId.toString())
				})) {
				(id, line1, line2, city, state, zip, homeType, size, services) => UserAddress(id, line1, line2, city, state, zip, homeType, size, null, services)
			} {
				address => Some(address.id, address.line1, address.line2, address.city, address.state, address.zip, address.homeType, address.size, address.services)
			})

	def dashboard() = Action {
		Cache.getAs[User]("user") match {
			case Some(user) => Ok(html.user.dashboard(userForm(user.id, user.addressId).fill(user)))
			case None => Redirect(routes.Application.splash)
		}
	}

	def signup = Action {
		Ok(html.signup.user(userForm(), addressForm()))
	}

	def editForm(form: Form[User]) = Action {
		Ok(html.edit.user(form))
	}

	def create = Action { implicit request =>
		userForm().bindFromRequest.fold(
			formWithErrors => BadRequest(html.signup.user(formWithErrors, addressForm())),
			user => {
				UserManager.create(user)
				addressForm(user.addressId).bindFromRequest.fold(
					formWithErrors => BadRequest(html.signup.user(userForm().fill(user), formWithErrors)),
					address => {
						UserAddressManager.geocodeAndCreateAddress(address)
						Ok(html.signup.userSummary(user, address))
					})
			})
	}

	def update = Action { implicit request =>
		System.out.println(request.body.asFormUrlEncoded);

		val userId: ObjectId = new ObjectId(request.body.asFormUrlEncoded.get("user_id").head.toString)
		val addressId: ObjectId = new ObjectId(request.body.asFormUrlEncoded.get("address_id").head.toString)

		userForm(userId, addressId).bindFromRequest.fold(
			errors => BadRequest(html.user.dashboard(errors)),
			user => {
				UserManager.update(user)
				Ok(html.user.dashboard(userForm(user.id, user.addressId).fill(user)))
			})
	}

	def findNearestUsers() = Action { implicit request =>
		request.body.asFormUrlEncoded match {
			case Some(map) => {
				val location: List[Double] = List(map.get("latitude").get.head.toDouble, map.get("longitude").get.head.toDouble)
				val users: List[UserAddressDto] = UserAddressManager.findNearestUsers(location, 20)

				Ok(Json.toJson(users))
			}
			case None =>
				BadRequest("Error retrieving businesses.")
		}
	}
}