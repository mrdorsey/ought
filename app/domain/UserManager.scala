package domain

import org.bson.types.ObjectId
import models.User
import com.mongodb.casbah.commons.MongoDBObject

object UserManager {

  def create(user: User): User = {
    User.insert(user)
    
    return user
  }
  
  def update(user: User): User = {
    User.save(user.copy(id = user.id))
       
    return user
  }
  
  def delete(id: ObjectId): ObjectId = { 
    User.remove(MongoDBObject("_id" -> id))
    
    return id
  }
  
  def findOneById(id: String): Option[User] = {
	  return User.findOneById(id)
  }
  
  def findOneByUsername(username: String): Option[User] = {
    return User.findOneByUsername(username)
  }
  
  def findOneByUsernamePassword(username: String, password: String): Option[User] = {
    return User.findOneByUserPass(username, password)
  }
}