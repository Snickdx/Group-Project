package server

import java.io._
import java.net._
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable._
import usermanagement.MyAuthenticator

object MainServer extends App{
  
  // This methods sets up the directory structure
  // We create a directory for every department
  def setUpDirectories() = {
    val allDepts = MyAuthenticator.getUsers.values.map(_.dept)
    val dirs = allDepts.map(new File(_)).filterNot(_.exists())
    dirs.foreach(_.mkdir())
  }
  
  println(new File(".").getCanonicalPath())
  var clientNumber = 0
  val synchBuffer = new ArrayBuffer[Thread] with SynchronizedBuffer[Thread]
  val portNumber = 8000
  val serverSock = new ServerSocket(portNumber)
  
  try {
    setUpDirectories()
    println("Directories setup....")
    // Loop forever
    while(true) {
      println("Server running....")
      val client = serverSock.accept()
      println("Client number " + clientNumber + " connected\n")
      clientNumber += 1
      // Now that we have a client, we offload this client to another
      // thread
      val thread = new Thread(new ClientHandler(client))
      synchBuffer += thread
      thread.start()
      println("Created thread to handle client " + (clientNumber - 1))
      // We sleep for a second to conserver resources
      Thread.sleep(1000)
    }
  }
  catch {
    case ioe : IOException => println("IOException encountered")
    case ie : InterruptedException => println("Thread interrupted")
    case e : Exception => println("Unknown exception encountered")
  }
  finally {
    try {
      serverSock.close()
    }
    catch {
      case ioe : IOException => println("Error in closing socket")
    }
  }

}