package clientutils;

import java.util.StringTokenizer;

public class FTPResponseParser 
{
	
	private static FTPResponseParser parser;
	
	private FTPResponseParser()
	{
		
	}
	
	public synchronized static FTPResponseParser getInstance()
	{
		if(parser == null)
		{
			parser = new FTPResponseParser();
		}
		
		return parser;
	}
	
	public FTPParseProduct parseResponse(String response) throws PoorlyFormedFTPResponse
	{
		StringTokenizer st = new StringTokenizer(response);
		if(!st.hasMoreTokens())
		{
			throw new PoorlyFormedFTPResponse(response);
		}
		
		StringBuilder sb = new StringBuilder();
		String code = st.nextToken();
		if(st.hasMoreTokens())
		{
			while(st.hasMoreElements())
				sb.append(st.nextToken() + " ");
			return new FTPParseProduct(code, sb.toString());
		}
		return new FTPParseProduct(code, "");
	}

}
