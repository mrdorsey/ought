package actions

import models.User
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.mvc.Http
import play.api.mvc._
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.WrappedRequest
import play.api.mvc.SimpleResult

case class AuthenticatedAction(
	val user: User, request: Request[AnyContent]) extends WrappedRequest(request)

object AuthenticatedAction {
	/*def Authenticated(f: AuthenticatedAction => Result) = {
		Action { request =>
			request.session.get("user").flatMap(u => u.asInstanceOf[Option[User]]).map { user =>
				f(AuthenticatedAction(user, request))	
			}.getOrElse()
		}
	}*/
}
