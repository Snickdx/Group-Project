package clientutils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPFileHandler {
	
    Socket socket;
    
    BufferedOutputStream bos;
    BufferedInputStream bis;
	InputStream is;
	int bufferSize;
    
    public TCPFileHandler(Socket connection) throws UnknownHostException, IOException{
    	this.socket=connection;
    	bos = new BufferedOutputStream(socket.getOutputStream());
		
    	is = socket.getInputStream();
		bufferSize = socket.getReceiveBufferSize();
    }
    
    public void sendFile(String name) throws IOException{
    	 	File file = new File(name);
    	    long length = file.length();
    	    if (length > Integer.MAX_VALUE) {
    	        System.out.println("File is too large.");
    	    }
    	    byte[] bytes = new byte[(int) length];
    	    bis = new BufferedInputStream(new FileInputStream(file)); 	    
    	    int count;
    	    try {
				while ((count = bis.read(bytes)) > 0) {
				    bos.write(bytes, 0, count);
				}
			} catch (IOException e) {
				System.out.println("File Uploaded");
			}
    	    bos.flush();
    }
	
    public void receiveFile(String name) throws IOException{
    	
    	bos = new BufferedOutputStream(new FileOutputStream(name));
        byte[] bytes = new byte[bufferSize];
	    int count;
	    try {
			while ((count = is.read(bytes)) > 0) {
			    bos.write(bytes, 0, count);
			}
		} catch (IOException e) {
			System.out.println("File Downloaded");
		}
	    bos.flush();
	    
	}
	  	
}
