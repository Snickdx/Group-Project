package clientutils;

public class PoorlyFormedFTPResponse extends Exception
{
	public PoorlyFormedFTPResponse(String resp)
	{
		super("FTP Response was poorly formed: " + resp);
	}
}
