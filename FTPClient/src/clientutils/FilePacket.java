package clientutils;

import java.io.Serializable;

public class FilePacket implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] arr;
	
	public FilePacket(byte[] arr)
	{
		this.arr = arr;
	}

	public byte[] getArr() {
		return arr;
	}

}
