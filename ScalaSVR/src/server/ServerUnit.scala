package server

import java.util._
import java.net._
import java.io._

class ServerUnit(socket: Socket) {
	
  protected val inputStream = new DataInputStream(socket.getInputStream())
  protected val outStream = new DataOutputStream(socket.getOutputStream())
  
  
  protected def readString = {
    inputStream.readUTF()
    /*
    val arr = new Array[Byte](1024)
    inputStream.read(arr)
    (new String(arr)).trim()*/
  }
  
  protected def sendString(str: String) = {
    outStream.flush()
    outStream.writeUTF(str + "\r\n")
    
    
    /*
    var arr = new Array[Byte](1024)
    var temp = (str + "\r\n").getBytes()
    val len = temp.length
    for(idx <- 0 to len) {
      arr(idx) = temp(idx)
    }
    outStream.write(arr)*/
  }
  
 
  
  
  
}