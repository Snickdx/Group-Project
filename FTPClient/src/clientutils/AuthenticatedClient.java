package clientutils;

import java.io.IOException;
import java.net.InetAddress;

public class AuthenticatedClient extends Client
{
	
	private String username;
	private String password;

	public AuthenticatedClient(InetAddress serverAddress, int serverPort, String username, String password) throws IOException 
	{
		super(serverAddress, serverPort);
		this.username = username;
		this.password = password;
	}

	@Override
	public ServerResp<String> login() 
			throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeString("USER " + username);
		String resp = readString();
		System.out.println(resp);
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = this.fact.getStatus(prod.getCode());
		System.out.println("Username sent and response received");
		if(stat == Status.USERNAME_ACCEPTED)
		{
			writeString("PASS " + password);
			System.out.println("Set password to server");
			String resp1 = readString();
			System.out.println("Got response from server for password " + resp1);
			FTPParseProduct prod1 = this.respParser.parseResponse(resp1);
			Status stat1 = this.fact.getStatus(prod1.getCode());
			return new ServerResp<String>(prod1.getBody(), stat1);
		}
		return new ServerResp<String>(prod.getBody(), stat);
		
		/*
		writeToServer("USER " + username);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = this.fact.getStatus(prod.getCode());
		System.out.println("Username sent and response received");
		if(stat == Status.USERNAME_ACCEPTED)
		{
			writeToServer("PASS " + password);
			System.out.println("Set password to server");
			String resp1 = readServer();
			System.out.println("Got response from server for password " + resp1);
			FTPParseProduct prod1 = this.respParser.parseResponse(resp1);
			Status stat1 = this.fact.getStatus(prod1.getCode());
			return new ServerResp<String>(prod1.getBody(), stat1);
		}
		return new ServerResp<String>(prod.getBody(), stat);*/
	}
	
	

}
