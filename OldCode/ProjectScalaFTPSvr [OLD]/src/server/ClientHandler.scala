package server

import usermanagement._
import usermanagement.MyAuthenticator.authenticate
import java.io._
import java.net.{InetAddress, Socket}
import java.util.concurrent.{Callable, ConcurrentHashMap}
import scala.util.matching.Regex

class ClientHandler(clientSock : Socket) 
extends Runnable {
  
  def run() : Unit = {
    val auth = new AuthenticaionSession(clientSock)
    val clientSession = auth.call()
    // We don't need to establish another thread, so we don't
    // create another thread
    clientSession.map(_.run())
    
  }

}