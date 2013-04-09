package dto

import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._

import models.BusinessAddress
import models.Business

case class BusinessWithAddressDto(
	business: Business,
	address: BusinessAddress
)

implicit val businessWithAddressWrites: Writes[BusinessWithAddressDto] = (
  (__ \ "business").write[Business] and
  (__ \ "address").write[Address]
)(unlift(BusinessWithAddressDto.unapply)) 