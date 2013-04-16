package controllers

import play.api.mvc._
import play.api.Play.current

object Email extends Controller {

	def appointmentEmail = Action {
		/*val mail = use[MailerPlugin].email
		
		mail.setSubject("Ought Appointment Requested!")
		mail.addRecipient("michael.r.dorsey@gmail.com")
		mail.addFrom("Ought <noreply@ought.com>")
		mail.send( "text", "<html>COME CLEAN MY CRIB</html>")
		*/

		Ok("appointment email sent.")
	}
}