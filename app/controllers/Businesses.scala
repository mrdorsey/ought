package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import models._
import org.bson.types.ObjectId

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
      (BusinessAddress.apply)(BusinessAddress.unapply)
  )
  
  def form = Action {
    Ok(html.signup.business(businessForm(), addressForm()))
  }

  def create = Action { implicit request =>
    businessForm().bindFromRequest.fold(
      formWithErrors => BadRequest(html.signup.business(formWithErrors, addressForm())),
      business => {
        Business.insert(business)
        addressForm(business.addressId).bindFromRequest.fold(
            formWithErrors => BadRequest(html.signup.business(businessForm().fill(business), formWithErrors)),
            address => {
              BusinessAddress.insert(address)
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
        Business.save(business.copy(id = businessId))
        addressForm(addressId).bindFromRequest.fold(
            formWithErrors => BadRequest(html.signup.business(businessForm().fill(business), formWithErrors)),
            address => {
              BusinessAddress.save(address.copy(id = addressId))
              Ok(html.signup.businessSummary(business, address))
            }
        )
      }
    )
  }
  
}