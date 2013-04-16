package dto

import play.api.libs.json._
import play.api.libs.json.Writes._

import models.BusinessAddress
import models.Business

case class BusinessAddressDto(
	business: Business,
	address: BusinessAddress)

object BusinessAddressDto {
	implicit val businessAddressDtoWriter = new Writes[BusinessAddressDto] {
		def writes(ba: BusinessAddressDto): JsValue = {
			Json.obj(
				"business" -> ba.business,
				"address" -> ba.address)
		}
	}
}