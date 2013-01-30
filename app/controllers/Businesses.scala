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

object Businesses extends Controller {

  def businessForm(id: ObjectId = new ObjectId, addressId: ObjectId = new ObjectId) = Form(

    mapping(
      "id" -> ignored(id),
      "name" -> nonEmptyText,
      "website" -> text,
      "established" -> optional(date("yyyy-MM-dd")),
      "addressId" -> ignored(addressId)
     )
      (Business.apply)(Business.unapply)
  )
  
  def addressForm(id: ObjectId = new ObjectId) = Form(
      mapping(
        "id" -> ignored(id),
        "line1" -> nonEmptyText,
        "line2" -> text,
        "city" -> nonEmptyText,
        "state" -> nonEmptyText,
        "zip" -> nonEmptyText
      )
      {
	      (id, line1, line2, city, state, zip) => BusinessAddress(id, line1, line2, city, state, zip, null) 
      }
      {
    	  address => Some(address.id, address.line1, address.line2, address.city, address.state, address.zip)
      }
  )
  
  def form = Action {
    Ok(html.signup.business(businessForm(), addressForm()))
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
            }
        )
      }
    )
  }
  
  def update(businessId: ObjectId, addressId: ObjectId) = Action { implicit request =>
    businessForm(businessId).bindFromRequest.fold(
      formWithErrors => BadRequest(html.signup.business(formWithErrors, addressForm())),
      business => {
        BusinessManager.update(business)
        addressForm(addressId).bindFromRequest.fold(
            formWithErrors => BadRequest(html.signup.business(businessForm().fill(business), formWithErrors)),
            address => {
              BusinessAddressManager.geocodeAndUpdateAddress(address)
              Ok(html.signup.businessSummary(business, address))
            }
        )
      }
    )
  }
  
}