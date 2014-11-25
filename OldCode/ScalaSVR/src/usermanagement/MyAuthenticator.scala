package usermanagement

object MyAuthenticator extends Authenticator{
  
  // Our "database" of users represented as a map from Strings to User ADTs
  private val users = Map(
      "Inzi" -> User("Inzi", "InziPass", None, "admin", "research"),
      "Nicholas" -> User("Nicholas", "NicholasPass", None, "admin", "development"),
      "Jane" -> User("Jane", "JanePass", None, "admin", "accounts"),
      "John" -> User("John", "JohnPass", None, "normal", "accounts")
      )
  
  def getUsers = users
      
  /*
   * Used to authenticate the user using the database in users
   * @param: username- the users username
   * @param: password - the user's password
   * @return: returns an Option monad over users :- Some(user), if a compatible user was found,
   * else None
   */
  def authenticate(username : String, password : String) = {
    users.get(username)
  }
  
}