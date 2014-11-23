package clientui;

import java.io.IOException;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RunApp extends Application implements ControllerSwitcher {

	Stage stage;
	Controller current;
	
	@Override
	public void start(Stage primaryStage) throws UnknownHostException, IOException {
		
      this.stage = primaryStage;
      this.stage.getIcons().add(new Image("file: icon.png"));
      showMain();
        
	}
	
	public void showLogin(){
		stage.close();
		stage.setScene(new Scene(new LoginController()));
		stage.setTitle("SwiFTP");
	    stage.show();
	}
	
	public void showMain() throws UnknownHostException, IOException{
		stage.close();
		stage.setScene(new Scene(new MainController()));
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
