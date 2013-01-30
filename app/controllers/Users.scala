package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import models._
import org.bson.types.ObjectId
import domain.UserManager

object Users extends Controller {

  val signupForm: Form[User] = Form(

    mapping(
      "id" -> ignored(new ObjectId),
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
      (id, username, email, city, passwords, _) => User(id, username, email, city,  passwords._1) 
    } 
    {
      user => Some(user.id, user.username, user.email, user.city, (user.password, ""), false)
    }.verifying(
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )
  )

  def form = Action {
    Ok(html.signup.user(signupForm));
  }

  /*def editForm = Action {
    val existingUser = User(
      "fakeuser", "fake@gmail.com", "Seattle, WA", "secret"
    )
    Ok(html.signup.user(signupForm.fill(existingUser)))
  }*/
  
  def submit = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => BadRequest(html.signup.user(errors)),
      user => {
        UserManager.create(user)
        Ok(html.signup.userSummary(user))
      }
    )
  }
  
}