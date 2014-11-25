package server

import usermanagement._
import usermanagement.MyAuthenticator.authenticate
import java.io._
import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.{Callable, ConcurrentHashMap}
import scala.util.matching.Regex

class ClientHandler(clientSock : Socket) 
extends Runnable {
  
  def run() : Unit = {
    try {
      val auth = new AuthenticaionSession(clientSock)
      val clientSession = auth.call()
      // We don't need to establish another thread, so we don't
      // create another thread
      clientSession.map(_.runActvity())
    } catch {
      case se: SocketException => {
        println("Socket exception occurred!")
        println(se.getMessage())
        se.printStackTrace()
      }
      case e:Exception => {
        println("Error in handler " + e.getMessage())
        e.printStackTrace()
      }
    }
    
  }

}