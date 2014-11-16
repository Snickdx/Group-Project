package clientutils;

import java.net.*;
import java.io.*;
import java.util.*;

public abstract class Client
{
	
	private InetAddress serverAddress;
	private Socket serverSock;
	private int serverPort;
	private InputStream input;
	private BufferedReader fromServer;
	private DataOutputStream toServer;
	private StatusFactory fact;
	private FTPResponseParser respParser;
	
	
	public Client(InetAddress serverAddres, int serverPort) throws IOException
	{
		this.fact = StatusFactory.getInstance();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.serverSock = new Socket(serverAddress, serverPort);
		this.input = serverSock.getInputStream();
		this.fromServer = new BufferedReader(new InputStreamReader(input));
		this.toServer = new DataOutputStream(serverSock.getOutputStream());
		this.respParser = FTPResponseParser.getInstance();
		
	}
	
	private void writeToServer(String str) throws IOException
	{
		toServer.writeBytes(str + "\r\n");
	}
	
	private String readServer() throws IOException
	{
		return fromServer.readLine();
	}
	
	private byte[] readServer(int numBytes) throws IOException
	{
		byte[] arr = new byte[numBytes];
		input.read(arr);
		return arr;
	}
	
	public abstract void login();
	
	public ServerResp<String[]> pwd() throws 
	IOException, InvalidFTPCodeException, PoorlyFormedFTPResponse
	{
		String pwdString = "PWD";
		writeToServer(pwdString);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		String[] arr = prod.getBody().split(":");
		return new ServerResp<String[]>(arr, stat);
	}
	
	
	
}
