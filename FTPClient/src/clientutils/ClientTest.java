package clientutils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import clientui.globals;

public class ClientTest {

	public static void main(String[] args) throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException {
		AuthenticatedClient client = new AuthenticatedClient(InetAddress.getByName("192.168.0.115"), 8000,"Nicholas","NicholasPass");
		System.out.println(client.login());
	}

}
