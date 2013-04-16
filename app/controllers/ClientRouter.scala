package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.Routes

object ClientRouter extends Controller {

	def javascriptRoutes = Action { implicit request =>
		import routes.javascript._

		Ok(
			Routes.javascriptRouter("jsRoutes")(routes.javascript.Businesses.profile)).as("text/javascript")
	}
}