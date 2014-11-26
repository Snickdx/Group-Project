
package clientui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import clientutils.AuthenticatedClient;
import clientutils.InvalidFTPCodeException;
import clientutils.PoorlyFormedFTPResponse;
import clientutils.Status;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController extends Controller{
	
	FXMLLoader loader;
	RunApp runApp;
	@FXML Button loginBtn;
	@FXML TextField usernameTF;
	@FXML VBox content;
	@FXML Label statusLbl;
	@FXML PasswordField passwordTF;
	
	public LoginController(RunApp runApp) { 
		super("loginView.fxml");
		this.runApp = runApp;
    }
	
	public void login() throws IOException{
		System.out.println("lol");
		String status = new String();
		runApp.client = new AuthenticatedClient(InetAddress.getByName(Globals.server),Globals.port,usernameTF.getText(),passwordTF.getText());
		try {
			//if(client.login().getStat()==Status.USER_LOGGED_IN)isAuth=true;
			//statusLbl.setText(client.login().toString());
			status = runApp.client.login().getStat().toString();
			Stage stage = (Stage) statusLbl.getScene().getWindow();
			stage.close();
			runApp.showMain();
		} catch (PoorlyFormedFTPResponse | InvalidFTPCodeException e) {
			status = "Invalid Login!";
		}
		statusLbl.setText(status);
	}


}
