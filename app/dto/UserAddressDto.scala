package dto

import play.api.libs.json._
import play.api.libs.json.Writes._
import models.UserAddress
import models.User

case class UserAddressDto(
	user: User,
	address: UserAddress)

object UserAddressDto {
	implicit val userAddressDtoWriter = new Writes[UserAddressDto] {
		def writes(ua: UserAddressDto): JsValue = {
			Json.obj(
				"user" -> ua.user,
				"address" -> ua.address)
		}
	}
}