package models

import views.html.signup.user
import domain.UserManager

case class UserCache(
	var user: User)