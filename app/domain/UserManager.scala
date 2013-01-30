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
}