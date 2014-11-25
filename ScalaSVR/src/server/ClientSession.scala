package server

import usermanagement._
import usermanagement.MyAuthenticator.authenticate
import java.io._
import java.net.{InetAddress, Socket}
import java.util.concurrent.{Callable}
import scala.util.matching.Regex
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/*
 * The objects of this class manage the sessions of our users
 * @param client: the socket used to facilitate communication between the 
 *                the client and the server
 * @param user: an instance of the User ADT that specifies the data of the user
 *              operating on the client side
 */

class ClientSession(client : Socket, user : User) extends ServerUnit(client){
  

  private var runClientThread = true
  
  // Used to pattern match against commands from user
   val PASS = """\s*PASS\s+(.+)""".r // implemented
   val USER = """\s*USER\s+(.+)""".r // implemented
   val QUIT = """\s*QUIT""".r // implemented
   val RETR = """\s*RETR\s+(.+)""".r  
   val STOR = """\s*STOR\s+(.+)\s+(\d+)""".r 
   val STOR2 = """\s*STOR\s+(.+)""".r
   val DELE = """\s*DELE\s+(.+)""".r // implemented
   val RNFR = """\s*RNFR\s+(.+)""".r //implemented
   val RNTO = """\s*RNTO\s+(.+)""".r // implemented
   val ABOR = """\s*ABOR""".r 
   val PWD = """\s*PWD""".r
  
  /*
   * Runs the thread that grabs the user's commands
   */
  def runActvity() = {
    System.out.println("Ready for client commands")
    while(runClientThread) {
      //toClient.flush()
      println("Awating response")
      val clientCommand = this.readString//clientCommands.readLine()
      val resp = processClientRequest(clientCommand)
      println("Sending " + resp)
      this.sendString(resp)
      ///toClient.writeBytes(resp + "\r\n")
      //Thread.sleep(1000)
    }
    client.close()
    println("User connection cancelled: " + user.toString)
    
  }
  
  // The following methods hanlde the specfic processing of each command
  def user(username : String) = "503 Already logged in"
  
  def pass(password : String) = "503 Already logged in"
    
  /*
   * Used to retrieve the names of all files in the directory
   */
  def pwd() : String = {
    val filepath = user.dept
    val folder = new File(filepath)
    "250 " + folder.listFiles().map(_.getName()).mkString(":")
  }  
  
  /*
   * Performs the QUIT operation to close a session
   */
  def quit() = {
    this.runClientThread = false
    "231 User logging out..."
  }
  
  /*
   * If called after a RNFR command, renames the file specified
   * in the preceding RNFR command to the new filename specified
   * @param filename: the new filename to applied to the specified file
   * @return: a string containing a status code about the operation
   */
  
  def rnto(oldFilename : String, newFilename: String) = {
    val oldFile = new File(user.dept + "\\" + oldFilename)
    val newFile = new File(user.dept + "\\" + newFilename)
    oldFile.renameTo(newFile) match {
      case true => "250 File rename successful"
      case false => "450 Action not taken"
    }
  }
  

  
  def rnto(filename : String) : String = "530 Invalid command sequence, RNFR must be followed by RNTO"
  
  
  /*
   * Starts the sequence of renaming steps specified by the RNFR operation
   * @param: the file to be renamed
   * @return: the status code about the file renaming operation
   */
  def rnfr(filename : String) : String = {
    val command = this.readString
    command match {
      case RNTO(newname) => rnto(filename, newname)
      case _ => "530 RNTO must follow RNFR"
    }
  } 
  
  /*
   * The DELE operation (can only be performed by admins)
   * @param filename: the name of the file to be deleted
   * @return: a string with a status code about the DELE operation
   */
  def dele(filename : String) = user.role match {
    case "admin" => 
      val file = new File(filename)
      file.delete() match {
        case false => "530 Error in deleting files"
        case true => "250 Delete successful"
      }
    case _ => "530 Only admins may delete files"
  }
  
  /*
   * Implements the ABOR operation (might be dropped from requirements
   */
  def abor() = "202 Command not implemented"
  
  def toInt(str : String) = {
    try {
      Some(str.toInt)
    } 
    catch {
      case e : Exception => None
    }
  }
  
  //def stor(filename: String) = {
    
  //}
  
  def stor(filename: String, n: String): String = toInt(n) match {
    case None => "501 Bad syntax"
    case Some(m) => {
      println("Got to readl " + n )
      val fileLoc = user.dept + "\\" + filename
      val file = new File(fileLoc)
      val bufferedFile = new FileOutputStream(file)
      val buffer = new Array[Byte](m)
      println("Reading in " + m)
      var count = 0
    try 
    {
      
      count = inputStream.read(buffer)
      //while(count > 0)
      //{
        //bufferedFile.write(buffer)
        //count = inputStream.read(buffer)
      //}
      bufferedFile.write(buffer)
      println("Wrote to file")
      bufferedFile.close();
      "250 Success";
    }
    catch {
      case e: Exception => "450 Couldn't complete"
    }
    }
  }
  
   
    
  /*
   * Performs the RETR operation - retrieves a file from the server
   * @param filename: the name of the file to be retrieved
   * @return: the status code 
   */
    
  def retr(filename: String): String = 
  {
    println("Tryting to send file")
    val filepath = user.dept + "\\" + filename
    val file = new File(filepath)
    file.exists() match
    {
      case true =>
        {
        	try 
        	{
        		val fileReader = new FileInputStream(file)
        		var count = 0
        		
        		val size = fileReader.getChannel().size().toInt
        		this.sendString("125 Expect " + size)
        		var arr = new Array[Byte](size)
        		outStream.flush()
        		println("Telling to expect file")
        		count = fileReader.read(arr)
        		outStream.write(arr)
        		/*
        		while(count > 0)
        		{
        		   println("Read " + count + " from file ")
        			outStream.write(arr)
        			println("Sent file piece")
        			count = fileReader.read(arr)
        		}*/
        		println("Awating acknowldegement")
        		val r = this.readString
        		println("Finished transimiit")
        		outStream.flush()
        		fileReader.close()
        		"250 File send successful"
        	}
        	catch
        	{
        		case e: Exception => "450 Unsuccesful"
        	}
        }
      case false => "553 Doesn't exist"
    }
  }
  
  
 
  /*
   * This method dispatches based on the client's specific commands
   * @param: the client's request string
   * @return: a pair containing the a boolean and a Callable[String],
   *          the boolean indicates to us if we should continue to server this client
   *          the Callable[String] computes the client's desired opertation asynchronously and
   *          and returns the status string for us to display to the user
   */
  def processClientRequest(request : String) = request match{
    case DELE(filename) => dele(filename)
    case RNFR(filename) => rnfr(filename)
    case RNTO(filename) => rnto(filename)
    case USER(username) => user(username)
    case PASS(password) => pass(password)
    case QUIT() => quit()
    case ABOR() => abor()
    case STOR(filename, n) => stor(filename, n)
    case STOR2(filename) => "501 Need number of lines"
    case RETR(filename) => retr(filename)
    case PWD() => pwd()
    case _ => "500 Unrecognized command " + request
  }
}
  

/*

  // Used to facilite communication
  private val inputStream = client.getInputStream()
  private val clientCommands = new BufferedReader(new InputStreamReader(inputStream))
  private val toClient = new DataOutputStream(client.getOutputStream())
  private val objectInput = new ObjectInputStream(inputStream)
  private val objectOutput = new ObjectOutputStream(client.getOutputStream())
  * 
  * 
  * 
  * 
  * 
  * 
  * /
  * 
  *  def retr(filename: String) = {
    val filepath = user.dept + "\\" + filename
    val file = new File(filepath)
    file.exists() match {
      case true => {
        try {
          val path = Paths.get(filepath)
          val entireFile = Files.readAllBytes(path)
          val filePacket = new FilePacket(entireFile)
          objectOutput.writeObject(filePacket)
          "250 File Retrieval Successful" 
        } catch {
          case e: Exception => "450 File retrieval unsuccessful"
        }
      }
      case false => {
        "553 File doesn't exist"
      }
    }
  }
  
  /*
  def retr(filename : String) = {
    val filepath = user.dept + "\\" + filename
    val file = new File(filepath)
    file.exists() match {
      case true =>
        try {
          val path = Paths.get(filepath)
          val arr = Files.readAllBytes(path)
          val size = arr.length
          var blocks = size / 1024
          if(size % 1024 > 0) {
            blocks += 1
          }
          val temp = new Array[Byte] (blocks * 1024)
          for(idx <- 0 to (size - 1))
            temp(idx) = arr(idx)
          toClient.writeBytes("125 Expect " + blocks + "\r\n")
          for(idx <- 0 to (blocks - 1)) {
            println("Writing block " + idx)
            toClient.write(temp,idx * 1024, 1024)
            println("Wrote block " + idx)
          }
          println("Wrote file to client")
          //toClient.write(arr, 0, size)
          toClient.flush()
          "250 File Retrieval Successful" 
        }
        catch {
          case ioe : IOException => "450 File retrieval was not successful"
        }
      case false => "553 File does not exist"
    }
    
  }*/
  
  */

/*
  
  /*
   * This method performs the STOR operation
   * @param filename: the name of the file to be stored
   * @param lines: the number of bytes to expect from the client
   * @return: a string containing the status code
   */
  def stor(filename : String, lines : String) = toInt(lines) match {
    case None => "501 bad syntax in STOR command"
    case Some(n) => 
      val arrs = new Array[Byte](1024)
      val filepath = user.dept + "\\" + filename
      val file = new File(filepath)
      file.exists() match {
        case true => "503 Need admin priveleges to overwrite files"
        case false =>
          try {
            
            val stream = new FileOutputStream(file)
            for(idx <- 1 to n) {
              inputStream.read(arrs)
              stream.write(arrs)
            }
            stream.close()
            println("Wrote new file to server")
            "250 File storage successful"
          }
          catch {
            case ioe : IOException => "450 Couldn't complete operation"
          }
      }
  }
  
       /*
      val arrs = (0 to (n - 1)).map(x => {
        val arr = new Array[Byte](1024)
        inputStream.read(arr, 0, 1024)
        arr
      })
      val fileAsBytes = arrs.reduce((x, y) => x ++ y)
      val filepath = user.dept + "\\" + filename
      val fileStream = new FileOutputStream(filepath)
      val file = new File(filepath)
      file.exists() match {
        case false => 
          fileStream.write(fileAsBytes)
          fileStream.close()
          "200 File storage was successful"
        case true => "503 File already exits! Request admin to delete file"
      } */ */
  
