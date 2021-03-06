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
	protected StatusFactory fact;
	protected FTPResponseParser respParser;
	
	
	public Client(InetAddress serverAddress, int serverPort) throws IOException
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
	
	protected void writeToServer(String str) throws IOException
	{
		toServer.writeBytes(str + "\r\n");
	}
	
	protected void writeToServer(byte[] bytes) throws IOException
	{
		toServer.write(bytes, 0, bytes.length);
	}
	
	
	protected String readServer() throws IOException
	{
		return fromServer.readLine();
	}
	
	protected byte[] readServer(int numBytes) throws IOException
	{
		byte[] arr = new byte[numBytes];
		input.read(arr);
		return arr;
	}
	
	public abstract ServerResp<String> login() 
			throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException;
	
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
	
	public ServerResp<String> retr(String dir, String filename) throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeToServer("RETR " + filename);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		if(stat == Status.EXPECT_FILE)
		{
			StringTokenizer st = new StringTokenizer(prod.getBody());
			String intVal = st.nextToken();
			Integer numBytes = Integer.parseInt(intVal);
			byte[] forFile = readServer(numBytes);
			File file = new File(dir + "\\" + filename);
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(forFile);
			String resp1 = readServer();
			FTPParseProduct prod1 = this.respParser.parseResponse(resp1);
			Status stat1 = fact.getStatus(prod1.getCode());
			return new ServerResp<String>(prod1.getBody(), stat1);
		}
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	public ServerResp<String> quit() throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeToServer("QUIT");
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	private Set<String> setDifference(Set<String> set1, Set<String> set2)
	{
		HashSet<String> answer = new HashSet<String>();
		for(String str : set1)
		{
			if(!set2.contains(str))
			{
				answer.add(str);
			}
		}
		return answer;
	}
	
	public void clientMirror(String dir) throws IOException, InvalidFTPCodeException, PoorlyFormedFTPResponse
	{
		ServerResp<String[]> filesResp = this.pwd();
		if(filesResp.getStat() == Status.SUCCESSFUL_FILE_OPERATION)
		{
			String[] fileNames = filesResp.getItem();
			HashSet<String> fileSet = new HashSet<String>();
			File folder = new File(dir);
			File[] filesInFolder = folder.listFiles();
			HashSet<String> names = new HashSet<String>();
			for(File f : filesInFolder)
			{
				names.add(f.getName());
			}
			Set<String> filesToCopy = setDifference(fileSet, names);
			for(String filename : filesToCopy)
			{
				ServerResp<String> resp = this.retr(dir, filename);
			}
		}
		
		
		
	}
}
