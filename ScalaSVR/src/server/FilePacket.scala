package server

class FilePacket(private val arr: Array[Byte]) extends Serializable {
  
  def getArr() = this.arr

}