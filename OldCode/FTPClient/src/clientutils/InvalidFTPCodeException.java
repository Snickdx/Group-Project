package clientutils;

public class InvalidFTPCodeException extends Exception
{
	
	public InvalidFTPCodeException(String code)
	{
		super(code + " is not a recognized FTP code");
	}

}
