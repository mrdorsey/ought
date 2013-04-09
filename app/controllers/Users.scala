package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.cache.Cache
import play.api.Play.current

import org.bson.types.ObjectId

import domain.UserManager
import models.User
import views._

object Users extends Controller {

  def signupForm(): Form[User] = Form(

    mapping(
      "username" -> text(minLength = 4),
      "email" -> email,
      "city" -> nonEmptyText,
      
      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying(
        // Add an additional constraint: both passwords must match
        "Passwords don't match", passwords => passwords._1 == passwords._2
      ),
      
      "accept" -> checked("You must accept the conditions")
      
    )
    {
      (username, email, city, passwords, _) => User(new ObjectId, username, email, city,  passwords._1) 
    } 
    {
      user => Some(user.username, user.email, user.city, (user.password, ""), false)
    }.verifying(
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )
  )
  
  def editForm(user_id: ObjectId): Form[User] = Form(

    mapping(
      "id" -> optional(text),
      "username" -> text(minLength = 4),
      "email" -> email,
      "password" -> text,
      "city" -> nonEmptyText 
    )
    {
      (id, username, email, password, city) => User(user_id, username, email, city, password) 
    } 
    {
      user => Some(Some(user.id.toString()), user.username, user.email, user.password, user.city)
    }
  )
  
  def dashboard() = Action {
	val user: User = Cache.get("user").asInstanceOf[User]
     Ok(html.user.dashboard(editForm(user.id).fill(user)))
  }

  def signup = Action {
    Ok(html.signup.user(signupForm()))
  }

  def editForm(form: Form[User]) = Action {
     Ok(html.edit.user(form))
  }
  
  def create = Action { implicit request =>
    signupForm().bindFromRequest.fold(
      errors => BadRequest(html.signup.user(errors)),
      user => {
        UserManager.create(user)
        Ok(html.signup.userSummary(user))
      }
    )
  }
  
  def update = Action { implicit request =>
	System.out.println(request.body.asFormUrlEncoded);
	
    val userId: ObjectId = new ObjectId(request.body.asFormUrlEncoded.get("user_id").head.toString)
	  
	editForm(userId).bindFromRequest.fold(
      errors => BadRequest(html.user.dashboard(errors)),
      user => {
        UserManager.update(user)
        Ok(html.user.dashboard(editForm(user.id).fill(user)))
      }
    )
  }
  
}