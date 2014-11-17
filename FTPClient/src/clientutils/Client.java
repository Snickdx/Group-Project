package clientutils;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	private void writeToServer(byte[] bytes) throws IOException
	{
		toServer.write(bytes, 0, bytes.length);
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
	
	public ServerResp<String> rename(String oldFilename, String newFilename) 
			throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeToServer("RNFR " + oldFilename);
		writeToServer("RNTO " + newFilename);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	public ServerResp<String> delete(String filename)
		throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeToServer("DELE " + filename);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	public ServerResp<String> stor(String dir, String filename) throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		File file = new File(dir + "\\" + filename);
		FileInputStream stream = new FileInputStream(file);
		long size = stream.getChannel().size();
		writeToServer("STOR " + filename + " " + size);
		Path path = Paths.get(dir + "\\" + filename);
		byte[] arr = Files.readAllBytes(path);
		writeToServer(arr);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
		
		
	}
	
	
}
