package clientui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import clientutils.AuthenticatedClient;
import clientutils.InvalidFTPCodeException;
import clientutils.PoorlyFormedFTPResponse;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RunApp extends Application implements ControllerSwitcher {

	Stage stage;
	Controller current;
	AuthenticatedClient client;
	
	@Override
	public void start(Stage primaryStage) throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException {
		this.stage = primaryStage;
		this.stage.getIcons().add(new Image("file: icon.png"));
		showLogin();
	}
	
	public void showLogin() throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException{
		stage.close();
		LoginController login =new LoginController(this);
		stage.setScene(new Scene(login));
		stage.setTitle("SwiFTP");
	    stage.show();
	}
	
	public void showMain() throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException{
		stage.close();
		stage.setScene(new Scene(new MainController(client)));
		stage.setTitle("SwiFTP");
	    stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void setViewParent(Controller screenPage) {
		// TODO Auto-generated method stub
		
	}
}
