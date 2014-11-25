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
	private DataInputStream inStream;
	private DataOutputStream outStream;
	protected StatusFactory fact;
	protected FTPResponseParser respParser;
	/*
	private BufferedReader fromServer;
	private DataOutputStream toServer;
	
	private InputStreamReader inputReader;
	private ObjectOutputStream objectOutput;
	private ObjectInputStream objectInput; */
	
	
	public Client(InetAddress serverAddress, int serverPort) throws IOException
	{
		this.respParser = FTPResponseParser.getInstance();
		this.fact = StatusFactory.getInstance();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.serverSock = new Socket(serverAddress, serverPort);
		this.input = serverSock.getInputStream();
		this.inStream = new DataInputStream(input);
		this.outStream = new DataOutputStream(this.serverSock.getOutputStream());
	}
	
	protected String readString() throws IOException
	{
		return inStream.readUTF();
//		byte[] arr = new byte[1024];
//		inStream.read(arr);
//		String resp =  new String(arr);
//		System.out.println("Received " + resp + " from Server");
//		return resp;
	}
	
	protected void writeString(String str) throws IOException
	{
		outStream.flush();
		outStream.writeUTF(str);
		
//		byte[] arr = (str + "\r\n").getBytes();
//		byte[] temp = new byte[1024];
//		int len = arr.length;
//		for(int idx = 0; idx < len; idx += 1)
//		{
//			temp[idx] = arr[idx];
//		}
//		//System.out.println("Writing " + str);
//		outStream.write(arr);
	}
	
	protected boolean isDelimeter(byte b)
	{
		byte newline = (byte) '\n';
		byte carriage = (byte) '\n';
		return (b == newline) || (b == carriage);
	}

	
	
	
	
	
	
	protected byte[] readServer(int numBytes) throws IOException
	{
		
		byte[] arr = new byte[numBytes];
		input.read(arr, 0, numBytes);
		
		//input.read(arr);
		return arr;
	}
	
	
	
	public abstract ServerResp<String> login() 
			throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException;
	
	public ServerResp<String[]> pwd() throws 
	IOException, InvalidFTPCodeException, PoorlyFormedFTPResponse
	{
		String pwdString = "PWD";
		writeString(pwdString);
		String resp = readString();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		String[] arr = prod.getBody().split(":");
		return new ServerResp<String[]>(arr, stat);
	}
	
	public ServerResp<String> rename(String oldFilename, String newFilename) 
			throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeString("RNFR " + oldFilename);
		writeString("RNTO " + newFilename);
		String resp = readString();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	public ServerResp<String> delete(String filename)
		throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeString("DELE " + filename);
		String resp = readString();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	public ServerResp<String> stor(String dir, String filename) throws PoorlyFormedFTPResponse, IOException, InvalidFTPCodeException
	{
		
		String path = dir + "\\" + filename;
		File file = new File(path);
		FileInputStream fInpt = new FileInputStream(file);
		int fileSize = (int) fInpt.getChannel().size();
		this.writeString("STOR " + filename + " " + fileSize);
		System.out.println("Sending the file size: " + fileSize);
		byte[] arr = new byte[fileSize];
		fInpt.read(arr);
		outStream.flush();
		outStream.write(arr);
		fInpt.close();
		System.out.println("Finihsed sending the file");
		
		//int count = 0;
		//while((count = fInpt.read(arr)) > 0)
		//{
			//outStream.write(arr);
			//count = fInpt.read(arr);
		//}
		//outStream.write("".getBytes());
		String resp = readString();
		System.out.println("Got from server " + resp );
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
		
	}
	
	public ServerResp<String> retr(String dir, String filename) throws PoorlyFormedFTPResponse, IOException, InvalidFTPCodeException
	{
		String path = dir + "\\" + filename;
		File file = new File(path);
		FileOutputStream fOut = new FileOutputStream(file);
		System.out.println("Sending request");
		writeString("RETR " + filename);
		System.out.println("Getting the response");
		String resp = readString();
		System.out.println("Got response " + resp);
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		System.out.println("Expecting file");
		if(stat == Status.EXPECT_FILE)
		{
			System.out.println("Rest of body " + prod.getBody());
			StringTokenizer sta = new StringTokenizer(prod.getBody());
			sta.nextToken();
			Integer val = Integer.parseInt(sta.nextToken());
			byte[] fromServer = new byte[val];
			int count = 0;
			count = inStream.read(fromServer);
			fOut.write(fromServer);
			/*
			while(count > 0) 
			{
				System.out.println("Apple"+count);
				System.out.println("Read from server");
				fOut.write(fromServer);
				System.out.println("Read block from server");
				count = inStream.read(fromServer);
				//count = inStream.read(fromServer);
			} */
			fOut.close();
			System.out.println("Sending acknowledegement");
			this.writeString("RECEIVED");
			System.out.println("Finished reading");
			String re = readString();
			FTPParseProduct p = this.respParser.parseResponse(re);
			Status st = fact.getStatus(p.getCode());
			return new ServerResp<String>(prod.getBody(), st);
		}
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
	
	public ServerResp<String> quit() throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		writeString("QUIT");
		String resp = readString();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
		/*
		writeToServer("QUIT");
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
		*/
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
	
	public void clientMirror(String dir) throws IOException, InvalidFTPCodeException, PoorlyFormedFTPResponse, ClassNotFoundException
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


/*
 * 
 * 
 * 
 * 
 * public ServerResp<String> stor(String dir, String filename) throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException
	{
		
		
		
		//File file = new File(dir + "\\" + filename);
		//FileInputStream stream = new FileInputStream(file);
		Path path = Paths.get(dir + "\\" + filename);
		byte[] arr = Files.readAllBytes(path);
		int size = arr.length;
		File file = new File(dir + "\\" + filename);
		long size1 = file.length();
		FileInputStream fileStream = new FileInputStream(file);
		
		long numFullBlocks = size1  / 1024;
		numFullBlocks += ((size % 1024) > 0) ? 1 : 0;
		//byte[] copy = new byte[1024 * numFullBlocks];
		//for(int idx = 0; idx < size; idx++)
			//copy[idx] = arr[idx];
		//long size = stream.getChannel().size();
		System.out.println("Sending file size of " + numFullBlocks);
		writeToServer("STOR " + filename + " " + numFullBlocks);
		
		for(int i = 0; i < numFullBlocks; i++)
		{
			System.out.println("Writing block " + i);
			byte[] bytes  = new byte[1024];
			long len = 1024;
			if(i * 1024 > size1)
			{
				len = size1 - (i - 1) * 1024;
			}
			fileStream.read(bytes,0 , (int) len);
			toServer.write(bytes);
			System.out.println("Wrote block " + i);
		}
		fileStream.close();
		System.out.println("Finished sending file");
		//writeToServer(copy);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		return new ServerResp<String>(prod.getBody(), stat);
		
		
	}
	
	
	public ServerResp<String> retr(String dir, String filename) throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException, ClassNotFoundException
	{
		writeToServer("RETR " + filename);
		String resp = readServer();
		FTPParseProduct prod = this.respParser.parseResponse(resp);
		Status stat = fact.getStatus(prod.getCode());
		if(stat == Status.EXPECT_FILE)
		{
			System.out.println("Rest of body " + prod.getBody());
			StringTokenizer st = new StringTokenizer(prod.getBody());
			String expect = st.nextToken();
			String intVal = st.nextToken();
			Integer numBytes = Integer.parseInt(intVal);
			System.out.println("Reading file from server,  " + intVal);
			//byte[] forFile = readServer(numBytes);
			File file = new File(dir + "\\" + filename);
			FileOutputStream fileOutput = new FileOutputStream(file);
			/*
			for(int i = 1; i <= numBytes; i++)
			{
				System.out.println("Reading block from server");
				byte[] forFile  = new byte[1024];
				input.read(forFile);
				fileOutput.write(forFile);
				System.out.println("Read block from server");
			}*//*
			Object obj = objectInput.readObject();
			FilePacket packet = (FilePacket) obj;
			fileOutput.write(packet.getArr());
			fileOutput.close();
			System.out.println("Read file from server");
			String resp1 = readServer();
			FTPParseProduct prod1 = this.respParser.parseResponse(resp1);
			Status stat1 = fact.getStatus(prod1.getCode());
			return new ServerResp<String>(prod1.getBody(), stat1);
		}
		return new ServerResp<String>(prod.getBody(), stat);
	}
	
 * 
 * 
 */
