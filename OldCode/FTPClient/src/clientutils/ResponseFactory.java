package clientutils;

public class ResponseFactory
{
	
	private static ResponseFactory factory;
	private StatusFactory stats;
	
	private ResponseFactory()
	{
		this.stats = StatusFactory.getInstance();
	}
	
	public synchronized static ResponseFactory getInstance()
	{
		if(factory == null)
		{
			factory = new ResponseFactory();
		}
		
		return factory;
	}
	
	
	
	

}
