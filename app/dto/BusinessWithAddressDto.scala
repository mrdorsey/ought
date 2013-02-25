package dto

import models.BusinessAddress
import models.Business

case class BusinessWithAddressDto(
	business: Business,
	address: BusinessAddress
)