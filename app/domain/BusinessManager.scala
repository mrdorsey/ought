package domain

import models.BusinessAddress
import java.net.URL
import java.io.DataOutputStream
import play.mvc.Http

object BusinessManager {

  val GeocodingProviderURL = "http://maps.googleapis.com/maps/api/geocode/json?address=[ADDRESS]&sensor=[USESENSOR]"
  val useSensor = false

  def geocodeAddress(address: BusinessAddress): BusinessAddress = {

    val urlString = GeocodingProviderURL.replace("[ADDRESS]", addressToURLString(address)).replace("[USESENSOR]", useSensor.toString)

    Http(url){inputStream => 
    	var responseJSON = JsonParser.parse(new InputStreamReader(is))
    }

    return address
  }

  private def addressToURLString(address: BusinessAddress): String = {
    var addressURLString: String = ""

    addressURLString.concat(address.line1.concat("+"))
    addressURLString = if (address.line2 != null) addressURLString.concat(address.line2.concat("+")) else addressURLString
    addressURLString.concat(address.city.concat("+"))
    addressURLString.concat(address.state.concat("+"))
    addressURLString.concat(address.zip)

    return addressURLString
  }
}