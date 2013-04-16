package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.cache.Cache
import play.api.cache.EhCachePlugin
import play.mvc.Http
import play.mvc.Http.Context

import java.util.UUID

import domain.UserManager
import models.User

object Login extends Controller {

	val loginForm = Form[(String, String)](
		tuple(
			"email" -> nonEmptyText,
			"password" -> nonEmptyText))

	def login = Action { implicit request =>
		val values = loginForm.bindFromRequest.data
		val username = values("username")
		val password = values("password")

		if ((username != null && !username.isEmpty()) && (password != null && !password.isEmpty())) {
			UserManager.findOneByUsernamePassword(username, password) match {
				case None => BadRequest("Invalid username / password")
				case Some(user) => {
					Cache.set("user", user)
					Ok("Loggin In")
				}
			}
		} else {
			BadRequest("Invalid username / password")
		}
	}

	def logout = Action { implicit request =>
		current.plugin[EhCachePlugin].map {
			cache => cache.cache.remove("user")
		}.getOrElse(false)
		//clear session
		Ok("Session cleared")
	}
}