package usermanagement

// Algebraic datatype used to represent the user retrieved from the database
case class User(username : String, password : String, salt : Option[String],
    role : String, dept : String) {
  override def toString = 
     "Username: " + username + "\nPassword: " + password
}

// The trait to be mixed into the class responsible for retrieving user informaion
trait Authenticator {
  /*
   * This method accepts the username and password of the user being added
   * and returns an Option monad on a User instance
   * @param username : the username for the client
   * @param password : the password for the client
   * @return: an option monad on a User object, None - couldn't authenticate,
   * 					Some(u), authentication successful
   */
  def authenticate(username : String, password : String) : Option[User]
  
}