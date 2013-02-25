package controllers

import play.api._
import play.api.mvc._

import views._

object Application extends Controller {
  
  def about = Action {
    Ok(html.about());
  }
  
  def blog = Action {
    Ok(html.blog());
  }
  
  def contact = Action {
    Ok(html.contact());
  }
  
  def home = Action {
    Ok(html.dashboard());
  }
  
  def splash = Action {
    Ok(html.splash());
  }
  
}