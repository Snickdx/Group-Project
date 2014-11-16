package clientutils;

public class StatusFactory
{
	
	private static StatusFactory fact;
	
	private StatusFactory()
	{
		
	}
	
	public synchronized static StatusFactory getInstance()
	{
		if(fact == null)
		{
			fact = new StatusFactory();
		}
		return fact;
	}
	
	public Status getStatus(String code) throws InvalidFTPCodeException
	{
		String check = code.trim();
		if(check.equals("403"))
		{
			return Status.INVALID_USERNAME_OR_PASSWORD;
		}
		else if(check.equals("331"))
		{
			return Status.USERNAME_ACCEPTED;
		}
		else if(check.equals("230"))
		{
			return Status.USER_LOGGED_IN;
		}
		else if(check.equals("231"))
		{
			return Status.QUITING_SERVICE;
		}
		else if(check.equals("503"))
		{
			return Status.BAD_COMMAND_SEQUENCE;
		}
		else if(check.equals("530"))
		{
			return Status.NOT_LOGGED_IN;
		}
		else if(check.equals("250"))
		{
			return Status.SUCCESSFUL_FILE_OPERATION;
		}
		else if(check.equals("553"))
		{
			return Status.FILE_DOES_NOT_EXIST;
		}
		else if(check.equals("202"))
		{
			return Status.COMMAND_NOT_IMPLEMENTED;
		}
		else if(check.equals("450"))
		{
			return Status.ACTION_NOT_TAKEN;
		}
		else if(check.equals("501"))
		{
			return Status.SYNTAX_ERROR_IN_REQUEST;
		}
		else if(check.equals("500"))
		{
			return Status.COMMAND_NOT_RECOGNIZED;
		}
		
		throw new InvalidFTPCodeException(check);
		
		
		
	}
}
