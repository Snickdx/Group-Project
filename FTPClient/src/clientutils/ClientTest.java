package clientutils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import clientui.Globals;



public class ClientTest {
	
	public static void main(String[] args) throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException, ClassNotFoundException {
		System.out.println("Starting client....");
		AuthenticatedClient client = new AuthenticatedClient(InetAddress.getByName("localhost"), 8001,"Nicholas","NicholasPass");
		System.out.println(client.login());
		System.out.println("Testing directory list..");
		ServerResp<String[]> res1 = client.pwd();
		for(String str : res1.getItem())
			System.out.println(str);
		System.out.println("Testing retrieval");
		
		client.retr(Globals.localDir, "rfile.txt");
		System.out.println("Testing sending");
		client.stor(Globals.localDir, "sfile.txt");
		client.rename("somedoc.txt", "newname.txt");
		client.delete("deletedoc.txt");
		client.quit();
	}

}
