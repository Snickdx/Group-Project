package server

import usermanagement._
import usermanagement.MyAuthenticator.authenticate
import java.io._
import java.net.{InetAddress, Socket}
import java.util.concurrent.{Callable}
import scala.util.matching.Regex

class AuthenticaionSession(client : Socket) extends ServerUnit(client)
{
  
  private val invalidPasswordString = "430 Invalid username or password"
  private val acceptedUsername = "331 Username accepted, need password\r\n"
  private val goodbyeString = "231 quiting service"
  private val userLoggedIn = "230 User logged in"
  private var username : Option[String] = None
  private var password : Option[String] = None
  private var completed = false;
  
  def call() : Option[ClientSession]  = {
    
    var processed: Either[String, User] = Left("Awaiting client")
    var user : Option[User] = None
    while(!completed) {
      val command = this.readString
      println("received " + command + " from client")
      processed = processClientRequest(command)
      processed match {
        case Left(msg) => {
          println("Client not yet authenticated, sending " + msg)
          this.sendString(msg)
        }
        case Right(u) => {
          println("Client authenticated.....")
          this.sendString(userLoggedIn)
          println("Wrote admission " + userLoggedIn)
          user = Some(u)
          completed = true
        }
      }
    }
    user.map(user => new ClientSession(client, user))
  }
  
 
  
  def authenticateWrapper(username : Option[String], password: Option[String]) = 
    (username, password) match {
    case (Some(u), Some(p)) => authenticate(u, p)
    case _ => None
  }
  
  def processClientRequest(request : String) : 
  Either[String, User] = {
    
    val PASS = """\s*PASS\s+(.+)""".r
    val USER = """\s*USER\s+(.+)""".r
    val QUIT = """\s*QUIT""".r
    
    request match {
      case PASS(pass) => 
        password = Some(pass)
        authenticateWrapper(username, Some(pass)) match {
          case None => Left(invalidPasswordString)
          case Some(u) => Right(u)
        }
      case USER(usern) => 
        username = Some(usern)
        authenticateWrapper(Some(usern), password) match {
          case None => Left(acceptedUsername)
          case Some(u) => Right(u)
        }
      case QUIT() => {
          completed = true
          Left(goodbyeString)
      }
      case x => Left("530 Not logged in " + x +"\r\n")
    }
    
    
      
    
  }

}

//private val inputStream = client.getInputStream()
  //private val clientCommands = new BufferedReader(new InputStreamReader(inputStream))
  //private val toClient = new DataOutputStream(client.getOutputStream())